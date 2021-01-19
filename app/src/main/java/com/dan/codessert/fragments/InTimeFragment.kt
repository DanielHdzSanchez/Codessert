package com.dan.codessert.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dan.codessert.R
import com.dan.codessert.products.DessertAdapter
import com.dan.codessert.products.DrinkAdapter
import com.dan.codessert.products.Product
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*
import kotlin.collections.HashMap


class InTimeFragment : Fragment() {
    private lateinit var drinksList: RecyclerView
    private lateinit var dessertsList: RecyclerView
    private lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val drinksRef: Query = db.collection("drinks").whereEqualTo("available", true)
    private val dessertsRef: Query = db.collection("desserts").whereEqualTo("available", true)
    var drinkAdapter: DrinkAdapter? = null
    var dessertsAdapter: DessertAdapter? = null
    private lateinit var cgOrder: ChipGroup

    private lateinit var chipTea: Chip
    private lateinit var chipAmericano: Chip
    private lateinit var chipFrappe: Chip
    private lateinit var chipItalianSoda: Chip
    private lateinit var chipCookies: Chip
    private lateinit var chipMuffin: Chip
    private lateinit var chipCheesecake: Chip

    private var counterTea = 0
    private var counterAmericano = 0
    private var counterFrappe = 0
    private var counterItalianSoda = 0
    private var counterCookies = 0
    private var counterMuffin = 0
    private var counterCheesecake = 0


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_in_time, container, false)
        drinksList = view.findViewById(R.id.rvDrinks)
        dessertsList = view.findViewById(R.id.rvDesserts)
        auth = FirebaseAuth.getInstance()
        cgOrder = view.findViewById(R.id.cgOrderChipGroud)
        setupDesserts()
        setupDrinks()
        setupChips(view)
        val btnOrder: CardView = view.findViewById(R.id.btnOrder)
        btnOrder.setOnClickListener {
            if (counterCheesecake==0
                    &&counterCookies==0
                    &&counterFrappe==0
                    &&counterItalianSoda==0
                    &&counterMuffin==0
                    &&counterAmericano==0
                    &&counterTea==0)
                        Toast.makeText(activity!!, "Por favor, selecciona al menos un elemento.", Toast.LENGTH_SHORT).show()
            else
                processOrder()
        }
        val btnEndOrder: CardView = view.findViewById(R.id.btnEndOrder)
        btnEndOrder.setOnClickListener {
            checkMembership()
        }
        return view
    }

    private fun setChipsToZero() {
        counterMuffin=0
        counterItalianSoda=0
        counterFrappe=0
        counterCookies=0
        counterCheesecake=0
        counterAmericano=0
        counterTea=0
    }

    private fun changeViews(counter: Int, chip: Chip) {
        if (counter==0)
            chip.visibility = View.GONE
        else
            chip.visibility = View.VISIBLE
    }

    private fun setupChips(v: View) {
        chipTea = v.findViewById(R.id.chipTea)
        chipTea.setOnClickListener {
            counterTea--
            changeViews(counterTea, chipTea)
            changeChips()
        }
        chipAmericano = v.findViewById(R.id.chipAmericano)
        chipAmericano.setOnClickListener {
            counterAmericano--
            changeViews(counterAmericano, chipAmericano)
            changeChips()
        }
        chipCheesecake = v.findViewById(R.id.chipCheesecake)
        chipCheesecake.setOnClickListener {
            counterCheesecake--
            changeViews(counterCheesecake, chipCheesecake)
            changeChips()
        }
        chipCookies = v.findViewById(R.id.chipCookies)
        chipCookies.setOnClickListener {
            counterCookies--
            changeViews(counterCookies, chipCookies)
            changeChips()
        }
        chipFrappe = v.findViewById(R.id.chipFrappe)
        chipFrappe.setOnClickListener {
            counterFrappe--
            changeViews(counterFrappe, chipFrappe)
            changeChips()
        }
        chipItalianSoda = v.findViewById(R.id.chipItalianSoda)
        chipItalianSoda.setOnClickListener {
            counterItalianSoda--
            changeViews(counterItalianSoda, chipItalianSoda)
            changeChips()
        }
        chipMuffin = v.findViewById(R.id.chipMuffin)
        chipMuffin.setOnClickListener {
            counterMuffin--
            changeViews(counterMuffin, chipMuffin)
            changeChips()
        }
    }

    private fun processOrder() {
        val user = auth.currentUser
        if (user != null) {
            db.collection("users").document(user.uid).collection("inTimes")
                    .document(getIDCurrentOrder())
                    .update("order", "processing")
                    .addOnSuccessListener {
                        createOrder()
                        /*activity!!.supportFragmentManager.beginTransaction().apply {
                            replace(R.id.flDashboard, Processing())
                            commit()
                        }*/
                    }
                    .addOnFailureListener {
                        Snackbar.make(activity!!.window.decorView.rootView, "Hubo un problema con tu orden", Snackbar.LENGTH_LONG).show()
                    }
        }
    }

    private fun createOrder() {
        val user = auth.currentUser
        val items = HashMap<String, Int>()
        if (counterTea!=0)
            items["Tea"] = counterTea
        if (counterAmericano!=0)
            items["Americano"] = counterAmericano
        if (counterMuffin!=0)
            items["Muffin"] = counterMuffin
        if (counterItalianSoda!=0)
            items["ItalianSoda"] = counterItalianSoda
        if (counterFrappe!=0)
            items["Frappe"] = counterFrappe
        if (counterCookies!=0)
            items["Cookies"] = counterCookies
        if (counterCheesecake!=0)
            items["Cheesecake"] = counterCheesecake
        if (user != null) {
            db.collection("users").document(user.uid).collection("inTimes")
                    .document(getIDCurrentOrder())
                    .collection("items")
                    //.document()
                    .add(items)
                    .addOnSuccessListener {
                        print("Items added")
                        val preference = activity!!.getSharedPreferences("order", AppCompatActivity.MODE_PRIVATE)
                        val editor = preference.edit()
                        editor.apply {
                            putString("itemsID", it.id)
                        }
                        editor.apply()

                        activity!!.supportFragmentManager.beginTransaction().apply {
                            replace(R.id.flDashboard, Processing())
                            commit()
                        }
                    }
        }
    }
    private fun getIDCurrentOrder(): String {
        val preference = activity!!.getSharedPreferences("order", AppCompatActivity.MODE_PRIVATE)
        return preference.getString("orderID", "Order not found").toString()
    }

    private fun setupDrinks() {
        val querySnapshot: Query = drinksRef
        val firestoreRecyclerOptions: FirestoreRecyclerOptions<Product> = FirestoreRecyclerOptions.Builder<Product>()
            .setQuery(querySnapshot, Product::class.java)
            .build()
        drinkAdapter = DrinkAdapter(firestoreRecyclerOptions)
        drinksList.layoutManager = LinearLayoutManager(activity!!)
        drinksList.adapter = drinkAdapter
        drinkAdapter!!.setOnItemClickListener(object : DrinkAdapter.OnItemClickListener {
            override fun onItemClick(documentSnapshot: DocumentSnapshot, position: Int) {
                when (documentSnapshot["name"]) {
                    /*Crear un metodo que lleve la cuenta de cada chip, y en estos casos
                    * sumar 1 al contador de cada chip
                    *
                    * ponerle listener a cada chip para que decremente en 1 la cantidad de items de esa categoria, cuando llegue a 0, el chip no se muestra*/
                    "Frappe" -> {
                        counterFrappe++
                        changeViews(counterFrappe, chipFrappe)
                    }
                    "Soda italiana" -> {
                        counterItalianSoda++
                        changeViews(counterItalianSoda, chipItalianSoda)
                    }
                    "Americano" -> {
                        counterAmericano++
                        changeViews(counterAmericano, chipAmericano)
                    }
                    "Té" -> {
                        counterTea++
                        changeViews(counterTea, chipTea)
                    }
                }
                changeChips()
            }
        })
    }

    private fun setupDesserts() {
        val snapshot: Query = dessertsRef
        val recyclerOptions: FirestoreRecyclerOptions<Product> = FirestoreRecyclerOptions.Builder<Product>()
            .setQuery(snapshot, Product::class.java)
            .build()
        dessertsAdapter = DessertAdapter(recyclerOptions)
        dessertsList.layoutManager = LinearLayoutManager(activity!!)
        dessertsList.adapter = dessertsAdapter
        dessertsAdapter!!.setOnItemClickListener(object : DessertAdapter.OnItemClickListener {
            override fun onItemClick(documentSnapshot: DocumentSnapshot, position: Int) {
                when (documentSnapshot["name"]) {
                    "Galletas" -> {
                        counterCookies++
                        changeViews(counterCookies, chipCookies)
                    }
                    "Cheesecake" -> {
                        counterCheesecake++
                        changeViews(counterCheesecake, chipCheesecake)
                    }
                    "Muffin" -> {
                        counterMuffin++
                        changeViews(counterMuffin, chipMuffin)
                    }
                }
                changeChips()
            }
        })
    }


    private fun changeChips() {
        chipTea.text = "Te: $counterTea"
        chipMuffin.text = "Muffin: $counterMuffin"
        chipItalianSoda.text = "Soda italiana: $counterItalianSoda"
        chipFrappe.text = "Frappe: $counterFrappe"
        chipCookies.text = "Galletas: $counterCookies"
        chipAmericano.text = "Americano: $counterAmericano"
        chipCheesecake.text = "Cheesecake: $counterCheesecake"
    }


    override fun onStart() {
        super.onStart()
        drinkAdapter!!.startListening()
        dessertsAdapter!!.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        drinkAdapter!!.stopListening()
        dessertsAdapter!!.stopListening()
        counterMuffin=0
    }

    private fun checkMembership() {
        val user = auth.currentUser
        if (user != null) {
            db.collection("users")
                    .document(user.uid)
                    .get()
                    .addOnSuccessListener { itM ->
                        if (itM.getString("membership")=="Prime") {
                            val dialog = AlertDialog.Builder(activity!!)
                            with(dialog) {
                                setTitle("Terminar")
                                setMessage("Gracias por tu visita.")
                                setNeutralButton("Ok") { _: DialogInterface, _: Int ->
                                    finishOrderingSession()
                                }
                                setIcon(android.R.drawable.ic_dialog_alert)
                                show()
                            }
                        }
                        else
                            db.collection("users")
                                    .document(user.uid)
                                    .collection("inTimes")
                                    .document(getIDCurrentOrder())
                                    .get()
                                    .addOnSuccessListener {
                                        val outTime: Long = System.currentTimeMillis()
                                        val inTimeAux = it.getTimestamp("time")!!.toDate()
                                        val inTime = inTimeAux.time
                                        val hoursDif = getHours(outTime) - getHours(inTime)
                                        val minutesDif = getMinutes(outTime) - getMinutes(inTime)
                                        var charge =0
                                        when (hoursDif) {
                                            0 -> charge = (minutesDif*1.16).toInt()
                                            in 1..Int.MAX_VALUE -> charge = ((hoursDif*70)+(minutesDif*1.16)).toInt()
                                        }
                                        //val charge = totalTime*70
                                        val dialog = AlertDialog.Builder(activity!!)
                                                with(dialog) {
                                                    setTitle("Terminar")
                                                    var msg = ""
                                                    val str1 = when (getMinutes(inTime)) {
                                                        in 0..9 -> "Hora de entrada: ${getHours(inTime)}:0${getMinutes(inTime)}"
                                                        else -> "Hora de entrada: ${getHours(inTime)}:${getMinutes(inTime)}"
                                                    }
                                                    val str2 = when (getMinutes(outTime)) {
                                                        in 0..9 -> "\nHora de salida: ${getHours(outTime)}:0${getMinutes(outTime)} \nPor favor, pasa a la caja a pagar $$charge. Gracias por tu visita."
                                                        else -> "\nHora de salida: ${getHours(outTime)}:${getMinutes(outTime)} \nPor favor, pasa a la caja a pagar $$charge. Gracias por tu visita."
                                                    }
                                                    msg = "$str1 $str2"
                                                    setMessage(msg)
                                                    setNeutralButton("Ok") { _: DialogInterface, _: Int ->
                                                        finishOrderingSession()
                                                    }
                                                    setIcon(android.R.drawable.ic_dialog_alert)
                                                    show()
                                                }
                                    }
                    }
        }
    }

    private fun getHours(timeInSeconds: Long): Int {
        var time = timeInSeconds
        val date = Date(time)
        val hours = date.hours
        //val hours = time / 3600
        /*time %= 3600
        val minutes = time / 60
        time %= 60
        val seconds = time*/
        return hours
    }

    private fun getMinutes(time: Long): Int {
        val date = Date(time)
        val minutes = date.minutes
        return minutes
    }

    private fun finishOrderingSession() {
        val preference = activity!!.getSharedPreferences("order", AppCompatActivity.MODE_PRIVATE)
        val editor = preference.edit()
        editor.clear()
        editor.apply()
        activity!!.supportFragmentManager.beginTransaction().apply {
            replace(R.id.flDashboard, QRFragment())
            commit()
        }
    }

    override fun onPause() {
        super.onPause()
        setChipsToZero()
    }
}