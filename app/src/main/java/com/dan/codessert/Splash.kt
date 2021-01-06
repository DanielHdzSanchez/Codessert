package com.dan.codessert

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.Toast
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.daimajia.androidanimations.library.Techniques
import com.dan.codessert.R.*
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.viksaa.sssplash.lib.activity.AwesomeSplash
import com.viksaa.sssplash.lib.cnst.Flags
import com.viksaa.sssplash.lib.model.ConfigSplash

class Splash : AwesomeSplash() {
    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }*/
    private lateinit var mAuth: FirebaseAuth
    override fun initSplash(configSplash: ConfigSplash?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (configSplash != null) {
            configSplash.backgroundColor = color.purple_500
            configSplash.animCircularRevealDuration = 900
            configSplash.revealFlagX = Flags.REVEAL_LEFT
            configSplash.revealFlagY = Flags.REVEAL_BOTTOM

            configSplash.titleSplash = "Te damos la bienvenida"
            configSplash.titleTextColor = color.white
            configSplash.titleTextSize = 25f
            configSplash.animTitleDuration = 900
            configSplash.animTitleTechnique = Techniques.FadeIn
            configSplash.logoSplash = drawable.ic_coffee
        }

    }

    override fun animationsFinished() {
        getRememberMe()
    }

    private fun getRememberMe() {
        val preference = getSharedPreferences("session", MODE_PRIVATE)
        val email = preference.getString("email", "Email not found").toString()
        val password = preference.getString("password", "Password not found").toString()
        if (email!="Email not found" && password!="Password not found")
            logIn(email, password)
        else {
            goToMain()
        }
    }

    //go to main
    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        Animatoo.animateSlideLeft(this)
    }

    private fun logIn (email: String, password: String) {
        mAuth = FirebaseAuth.getInstance()
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        this
                ) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        startActivity(Intent(this, Dashboard::class.java))
                        finish()
                        Animatoo.animateSlideLeft(this)
                    } else {
                        goToMain()
                    }
                }
    }
}