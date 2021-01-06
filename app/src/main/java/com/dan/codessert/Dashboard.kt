package com.dan.codessert

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.CodeScanner
import com.dan.codessert.fragments.*
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Dashboard : AppCompatActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var last: LatLng = LatLng(28.0, -100.0)
    private val LOCATION_REQUEST_CODE = 10001
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    var orderID: String = "none"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val homeFragment = HomeFragment()
        val accountFragment = AccountFragment()
        val inTimeFragment = InTimeFragment()
        val qrFragment = QRFragment()
        val reservationFragment = MapsFragment()
        val processingFragment = Processing()

        changeFragment(homeFragment)

        val bnm = findViewById<BottomNavigationView>(R.id.bnm)
        bnm.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.itemHome -> changeFragment(homeFragment)
                R.id.itemAccount -> changeFragment(accountFragment)
                R.id.itemInTime -> {
                    checkOrder()
                    when (getIDStatus()) {
                        "done" -> changeFragment(inTimeFragment)
                        "processing" -> changeFragment(processingFragment)
                        else -> changeFragment(qrFragment)
                    }
                }
                R.id.itemReservation -> changeFragment(reservationFragment)
            }
            true
        }
    }

    private fun checkOrder(): String {
        orderID = getIDCurrentOrder()
        //Toast.makeText(this, "Este es el ID de la orden: $orderID", Toast.LENGTH_LONG).show()
        var order = "none"
        val user = auth.currentUser
        if (user != null) {
            db.collection("users").document(user.uid).collection("inTimes")
                    .document(orderID)
                    .addSnapshotListener { value, error ->
                        if (value != null) {
                            order = value.getString("order").toString()
                            //Toast.makeText(this, "Este es el estado de la orden: $order", Toast.LENGTH_LONG).show()
                            saveOrder(order)
                        }
                    }
        }
        return order
    }

    private fun getIDCurrentOrder(): String {
        val preference = getSharedPreferences("order", MODE_PRIVATE)
        return preference.getString("orderID", "Order not found").toString()
    }

    private fun getIDStatus(): String {
        val preference = getSharedPreferences("order", MODE_PRIVATE)
        return preference.getString("status", "Status not found").toString()
    }

    private fun saveOrder(order: String) {
        val preference = getSharedPreferences("order", AppCompatActivity.MODE_PRIVATE)
        val editor = preference.edit()
        editor.apply {
            putString("status", order)
        }
        editor.apply()
    }

    private fun changeFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            val args =Bundle()
            args.putDouble("latitude", last.latitude)
            args.putDouble("longitude", last.longitude)
            fragment.arguments = args
            replace(R.id.flDashboard, fragment)
            commit()
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        val location: Task<Location> = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {
            last = LatLng(it.latitude, it.longitude)
        }
        location.addOnFailureListener {
            Toast.makeText(this, "Hubo un problema con la localizacion", Toast.LENGTH_SHORT).show()

        }
    }

    private fun askPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            else
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_REQUEST_CODE)
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getLastLocation()
    }

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            getLastLocation()
        else
            askPermission()
    }
}