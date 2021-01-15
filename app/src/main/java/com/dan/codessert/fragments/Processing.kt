package com.dan.codessert.fragments

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dan.codessert.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class Processing : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_processing, container, false)
        auth = FirebaseAuth.getInstance()
        val btnCancelOrder = view.findViewById<Button>(R.id.btnCancelOrder)
        btnCancelOrder.setOnClickListener {
            val user = auth.currentUser
            if (user != null) {
                db.collection("users")
                        .document(user.uid)
                        .collection("inTimes")
                        .document(getIDCurrentOrder())
                        .update("order", "done")
                        .addOnSuccessListener {
                            changeFragment()
                        }
            }
        }
        checkOrder()
        getOrder()
        return view
    }

    private fun changeFragment() {
        try {
            activity!!.supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out
                )
                replace(R.id.flDashboard, InTimeFragment())
                commit()
            }
        } catch (e: Exception) {
            print("Error: $e")
        }
    }

    private fun getOrder() {
        val user = auth.currentUser
        if (user != null) {
            db.collection("users").document(user.uid).collection("inTimes")
                    .document(getIDCurrentOrder())
                    .collection("items")
                    .document(getIDItems())
                    .get()
                    .addOnSuccessListener {
                        val tvItems = view!!.findViewById<TextView>(R.id.tvItems)
                        var items = ""
                        if (it["Muffin"] != 0 && it["Muffin"] != null) {
                            items += "Muffin: ${it["Muffin"]}\n"
                        }
                        if (it["Tea"] != 0 && it["Tea"] != null) {
                            items += "Té: ${it["Tea"]}\n"
                        }
                        if (it["Frappe"] != 0 && it["Frappe"] != null) {
                            items += "Frappe: ${it["Frappe"]}\n"
                        }
                        if (it["ItalianSoda"] != 0 && it["ItalianSoda"] != null) {
                            items += "Soda italiana: ${it["ItalianSoda"]}\n"
                        }
                        if (it["Cheesecake"] != 0 && it["Cheesecake"] != null) {
                            items += "Cheesecake: ${it["Cheesecake"]}\n"
                        }
                        if (it["Cookies"] != 0 && it["Cookies"] != null) {
                            items += "Galletas: ${it["Cookies"]}\n"
                        }
                        if (it["Americano"] != 0 && it["Americano"] != null) {
                            items += "Americano: ${it["Americano"]}\n"
                        }
                        tvItems.text = items
                    }
        }
    }

    private fun getIDItems(): String {
        val preference = activity!!.getSharedPreferences("order", AppCompatActivity.MODE_PRIVATE)
        return preference.getString("itemsID", "Items not found").toString()
    }

    private fun checkOrder() {
        val user = auth.currentUser
        if (user != null) {
            db.collection("users").document(user.uid).collection("inTimes")
                    .document(getIDCurrentOrder())
                    .addSnapshotListener { value, error ->
                        if (value != null) {
                            if (value.getString("order").toString() == "done"){
                                try {
                                    MediaPlayer.create(activity!!, R.raw.bell).start()
                                }
                                catch (e: Exception) {

                                }
                                Timer().schedule(object: TimerTask() {
                                    override fun run() {
                                        changeFragment()
                                    }
                                },2000)
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