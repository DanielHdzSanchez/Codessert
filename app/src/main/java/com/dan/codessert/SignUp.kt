package com.dan.codessert

import android.R.attr.password
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception
import kotlin.math.sign


class SignUp : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var name: String

    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etPassword: EditText

    lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        email = findViewById<TextInputEditText>(R.id.etEmail).text.toString()
        password = findViewById<TextInputEditText>(R.id.etPassword).text.toString()

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)


        val btnContinue = findViewById<Button>(R.id.btnContinue)
        btnContinue.setOnClickListener { checkBoxes() }
    }

    private fun signUp() {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                currentUser = mAuth.currentUser!!
                //uid = currentUser.uid
                Toast.makeText(this@SignUp, "Te damos la bienvenida a tu nueva experiencia con el café", Toast.LENGTH_SHORT).show()
                addClient()
                } else {
                    Toast.makeText(this@SignUp, "Verifica los datos.", Toast.LENGTH_SHORT).show()
                }

            }
    }

    private fun checkBoxes(){
        email = findViewById<TextInputEditText>(R.id.etEmail).text.toString().trim()
        password = findViewById<TextInputEditText>(R.id.etPassword).text.toString().trim()
        name = findViewById<TextInputEditText>(R.id.etName).text.toString()
        if (name == "" || password == "" || email=="")
            showError()
        else
            signUp()
        //Snackbar.make(window.decorView.rootView, "Este es el ID: ${user.ui}", Snackbar.LENGTH_SHORT).show()
    }

    private fun showError(){
        if (etName.text.toString() == "") etName.error = "Campo requerido"
        if (etEmail.text.toString() == "") etEmail.error = "Campo requerido"
        if (etPassword.text.toString() == "") etPassword.error = "Campo requerido"
    }

    private fun addClient() {
        val user = hashMapOf(
                "name" to name,
                "email" to email,
                "membership" to "Normal"
        )

        db.collection("users").document(currentUser.uid)
                .set(user)
                .addOnSuccessListener {
                    //Toast.makeText(this, "Sign up registered added", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Dashboard::class.java).apply {
                        //putExtra("id", id)
                    })
                    finish()
                    Animatoo.animateSlideLeft(this)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Hubo un problema, intenta de nuevo mas tarde.", Toast.LENGTH_SHORT).show()
                }
    }
}