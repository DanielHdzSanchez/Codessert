package com.dan.codessert.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.dan.codessert.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Processing : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_processing, container, false)
        auth = FirebaseAuth.getInstance()
        checkOrder()
        return view
    }

    private fun checkOrder() {
        val user = auth.currentUser
        if (user != null) {
            db.collection("users").document(user.uid).collection("inTimes")
                    .document(getIDCurrentOrder())
                    .addSnapshotListener { value, error ->
                        if (value != null) {
                            if (value.getString("order").toString() == "done"){
                                activity!!.supportFragmentManager.beginTransaction().apply {
                                    replace(R.id.flDashboard, InTimeFragment())
                                    commit()
                                }
                                //Snackbar.make(activity!!.window.decorView.rootView, "Tu orden esta lista y por llegar", Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }
        }
    }

    private fun getIDCurrentOrder(): String {
        val preference = activity!!.getSharedPreferences("order", AppCompatActivity.MODE_PRIVATE)
        return preference.getString("orderID", "Order not found").toString()
    }

}