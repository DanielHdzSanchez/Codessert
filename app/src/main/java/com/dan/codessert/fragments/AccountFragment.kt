package com.dan.codessert.fragments

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.dan.codessert.MainActivity
import com.dan.codessert.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class AccountFragment : Fragment() {
    val PICK_IMG_REQUEST = 1
    lateinit var btnChangePhoto: Button
    lateinit var ivProfilePhoto: ImageView
    lateinit var imgUri: Uri
    lateinit var tvAccountName: TextView
    lateinit var tvAccountEmail: TextView
    lateinit var tvAccountMembership: TextView

    private lateinit var mAuth: FirebaseAuth
    lateinit var auxView: View

    lateinit var db: FirebaseFirestore
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        auxView = view
        btnChangePhoto = view.findViewById(R.id.btnChangePhoto)
        ivProfilePhoto = view.findViewById(R.id.ivAccountProfilePhoto)

        btnChangePhoto.setOnClickListener {
            openFileChooser()
        }

        val btnLogout = view.findViewById<CardView>(R.id.cardLogout)
        btnLogout.setOnClickListener {
            logout()
        }

        mAuth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        db = FirebaseFirestore.getInstance()
        db.collection("users")
                .document(mAuth.currentUser!!.uid)
                .addSnapshotListener { value, error ->
                    if (error != null)
                        Snackbar.make(activity!!.window.decorView.rootView, "There was a problem with realtime changes", Snackbar.LENGTH_SHORT).show()
                    else
                        getData()
                }

        getProfilePicture()
        return view
    }

    private fun logout() {
        mAuth.signOut()
        quitPreference()
        startActivity(Intent(activity, MainActivity::class.java))
        activity!!.finish()
        Animatoo.animateSlideRight(activity)
    }

    private fun quitPreference() {
        val preference = activity!!.getSharedPreferences("session", AppCompatActivity.MODE_PRIVATE)
        val editor = preference.edit()
        editor.clear()
        editor.commit()
    }

    private fun getProfilePicture() {
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val ref = storageReference.child("${currentUser.uid}/profilePic")
            val mb: Long = 4096 * 4096
            ref.getBytes(mb)
                .addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    ivProfilePhoto.setImageBitmap(bitmap)
                }
                .addOnFailureListener {
                    Snackbar.make(auxView, "No tienes imagen aun", Snackbar.LENGTH_SHORT).show()
                }
        }
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMG_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMG_REQUEST && resultCode == RESULT_OK &&
                data != null && data.data != null) {
            imgUri = data.data!!
            //Picasso.get().load(imgUri).into(ivProfilePhoto)
            uploadImage()
        }
    }

    // UploadImage method
    private fun uploadImage() {
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            storageReference.child("${currentUser.uid}/profilePic")
                .putFile(imgUri)
                .addOnSuccessListener {
                    Snackbar.make(auxView, "Nueva imagen lista", Snackbar.LENGTH_SHORT).show()
                    getProfilePicture()
                }
                .addOnFailureListener {
                    Snackbar.make(auxView, "Hubo un problema con la imagen", Snackbar.LENGTH_SHORT).show()
                }
        }
    }

    private fun getData() {
        tvAccountEmail = auxView.findViewById(R.id.tvAccountEmail)
        tvAccountName = auxView.findViewById(R.id.tvAccountName)
        tvAccountMembership = auxView.findViewById(R.id.tvAccountMembership)
        val currentUser = mAuth.currentUser
        if (currentUser != null)
            db.collection("users").document(currentUser.uid).get().addOnSuccessListener {
                tvAccountName.text = it.getString("name").toString()
                tvAccountEmail.text = mAuth.currentUser?.email ?: ""
                tvAccountMembership.text = it.getString("membership").toString()
            }
    }
}