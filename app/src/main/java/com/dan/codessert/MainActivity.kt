package com.dan.codessert

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {
    lateinit var document: Task<DocumentSnapshot>
    lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    private lateinit var email: String
    private lateinit var password: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener { logIn() }

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        btnRegister.setOnClickListener { register() }
    }

    private fun logIn () {
        email = findViewById<TextInputEditText>(R.id.tvEmailLogin).text.toString().trim()
        password = findViewById<TextInputEditText>(R.id.tvPasswordLogin).text.toString()
        if (email=="")
            findViewById<TextInputEditText>(R.id.tvEmailLogin).error = "Este campo es necesario"
        else if (password=="")
            findViewById<TextInputEditText>(R.id.tvPasswordLogin).error = "Este campo es necesario"
        else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                            this
                    ) { task ->
                        if (task.isSuccessful) {
                            val user = mAuth.currentUser

                            //Toast.makeText(this@MainActivity, "Login succesful", Toast.LENGTH_SHORT).show()
                            val cbRememberMe = findViewById<CheckBox>(R.id.cbRememberMe)
                            if (cbRememberMe.isChecked)
                                savePreference()
                            startActivity(Intent(this, Dashboard::class.java).apply {
                                putExtra("id", getID())
                            })
                            finish()
                            Animatoo.animateSlideLeft(this)
                        } else {
                            Toast.makeText(
                                    this@MainActivity, "Verifica tus datos.",
                                    Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
        }
    }

    private fun savePreference() {
        val preference = getSharedPreferences("session", MODE_PRIVATE)
        val editor = preference.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.commit()
    }

    private fun getID(): String{
        var id = ""
        document = db.collection("users").document(email).get()
                .addOnSuccessListener {
                    if(it.exists()) {
                        id = it.id
                        Toast.makeText(this, "Esta es la funcion getID -> $id", Toast.LENGTH_SHORT).show()
                    }
                }
        return id
    }

    private fun register() {
        startActivity(Intent(this, SignUp::class.java))
        Animatoo.animateSlideLeft(this)
    }
}