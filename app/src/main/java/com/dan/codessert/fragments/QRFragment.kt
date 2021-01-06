package com.dan.codessert.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.dan.codessert.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.sql.Timestamp

class QRFragment : Fragment() {

    private lateinit var codeScanner: CodeScanner
    private lateinit var auxView: View
    private val CAMERA_REQUEST_CODE = 101

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_qr, container, false)
        auxView = view
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        setupPermissions()
        setupScanner()
        return view
    }

    private fun setupScanner() {
        val scannerView = auxView.findViewById<CodeScannerView>(R.id.scannerView)

        codeScanner = CodeScanner(activity!!, scannerView)
        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false
        }

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            activity!!.runOnUiThread {
                //Logic in here***********************************************************************************
                /*Se registra la hora de ingreso
                    * En la coleccion "inTimes" del cliente en cuestion, poner un documento con un field de status *done *processing
                    * Cuando el cliente presione "Ordenar" reemplazar el fragment de InTime por una ventana de espera y cambiar el documento a *processing*
                    * Cuando el field pase a *done* devolver la vista al cliente para que pueda volver a ordenar*/
                if (it.text == "go")
                    createInTime()
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            activity!!.runOnUiThread {
                Snackbar.make(activity!!.window.decorView.rootView, "Error en la inicializacion de la camara: ${it.message}", Snackbar.LENGTH_LONG).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun createInTime() {
        val user: FirebaseUser? = mAuth.currentUser
        if (user != null) {
            val inTime: HashMap<String, Any> = hashMapOf(
                    "time" to Timestamp(System.currentTimeMillis()),
                    "order" to "done",
                    "status" to "started"
            )
            db.collection("users").document(user.uid)
                    .collection("inTimes")
                    .add(inTime)
                    .addOnSuccessListener {
                activity!!.supportFragmentManager.beginTransaction().apply {
                    replace(R.id.flDashboard, InTimeFragment())
                    savePreference(it.id)
                    commit()
                }
            }
        }
    }

    private fun savePreference(id: String) {
        val preference = activity!!.getSharedPreferences("order", AppCompatActivity.MODE_PRIVATE)
        val editor = preference.edit()
        editor.apply {
            putString("orderID", id)
            putString("status", "done")
        }
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(activity!!, android.Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED)
            makeRequest()
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(activity!!, arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    Snackbar.make(activity!!.window.decorView.rootView, "Necesitas conceder los permisos de la camara para escanear", Snackbar.LENGTH_LONG).show()
            }
        }
    }
}