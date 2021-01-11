package com.dan.codessert.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dan.codessert.R
import com.dan.codessert.products.Product
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.random.Random


class HomeFragment : Fragment() {
    private lateinit var tvWelcomeMsg: TextView
    lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    private lateinit var name: String
    lateinit var auxView: View

    //Elementos de la recomendacion
    private lateinit var tvHomeProductDrink: TextView
    private lateinit var tvHomePriceDrink: TextView
    private lateinit var ivHomeProductDrink: CircleImageView

    private lateinit var tvHomeProductDessert: TextView
    private lateinit var tvHomePriceDessert: TextView
    private lateinit var ivHomeProductDessert: CircleImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        auxView = view

        //Recommendation
        tvHomeProductDrink = view.findViewById(R.id.tvHomeProductDrink)
        tvHomePriceDrink = view.findViewById(R.id.tvHomePriceDrink)
        ivHomeProductDrink = view.findViewById(R.id.ivHomeProductDrink)
        tvHomeProductDessert = view.findViewById(R.id.tvHomeProductDessert)
        tvHomePriceDessert = view.findViewById(R.id.tvHomePriceDessert)
        ivHomeProductDessert = view.findViewById(R.id.ivHomeProductDessert)

        getRecommendation()

        if (currentUser != null)
            db.collection("users").document(currentUser.uid).get().addOnSuccessListener {
                name = it.getString("name").toString()
                tvWelcomeMsg = view.findViewById(R.id.tvWelcomeMsg)
                tvWelcomeMsg.text = "Hola, $name"
                getProfilePicture()
            }

        val pullToRefresh: SwipeRefreshLayout = view.findViewById(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener {
            getRecommendation()
            pullToRefresh.isRefreshing = false
        }
        return view
    }

    private fun getProfilePicture() {
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val ref = storageReference.child("${currentUser.uid}/profilePic")
            val mb: Long = 4096 * 4096
            ref.getBytes(mb)
                .addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    val ivProfilePhoto = auxView.findViewById<ImageView>(R.id.ivHomeProfilePicture)
                    ivProfilePhoto.setImageBitmap(bitmap)
                }
                .addOnFailureListener {

                }
        }
    }

    private fun getRecommendation () {
        db.collection("drinks")
                .whereEqualTo("available", true)
                .get()
                .addOnCompleteListener {
            if (it.isSuccessful) {
                val drinks: ArrayList<Product> = ArrayList()
                for (document: DocumentSnapshot in it.result!!){
                    val drink: Product? = document.toObject(Product::class.java)
                    drink!!.id = document.id
                    drinks.add(drink)
                }
                val drinksListSize = drinks.size
                if (drinksListSize != 0) {
                    val randomDrink: Product = drinks[Random.nextInt(drinksListSize)]
                    getProductImage(randomDrink.id, "drinks")
                    tvHomeProductDrink.text = randomDrink.name
                    tvHomePriceDrink.text = "Precio: $${randomDrink.price}"
                }
                else {
                    tvHomeProductDrink.text = "Sin bebidas disponibles por el momento"
                    tvHomePriceDrink.text = ""
                }
            }
        }

        db.collection("desserts")
                .whereEqualTo("available", true)
                .get()
                .addOnCompleteListener {
            if (it.isSuccessful) {
                val desserts: ArrayList<Product> = ArrayList()
                for (document: DocumentSnapshot in it.result!!){
                    val dessert: Product? = document.toObject(Product::class.java)
                    dessert!!.id = document.id
                    desserts.add(dessert)
                }
                val dessertsListSize = desserts.size
                if (dessertsListSize != 0) {
                    val randomDessert: Product = desserts[Random.nextInt(dessertsListSize)]
                    getProductImage(randomDessert.id, "desserts")
                    tvHomeProductDessert.text = randomDessert.name
                    tvHomePriceDessert.text = "Precio: $${randomDessert.price}"
                }
                else {
                    tvHomeProductDessert.text = "Sin postres disponibles por el momento"
                    tvHomePriceDessert.text = ""
                }
            }
        }
    }

    private fun getProductImage(id: String, product: String) {
        val ref = storageReference.child("$product/$id.jpg")
        val mb: Long = 4096 * 4096
        ref.getBytes(mb)
                .addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    when (product) {
                        "drinks" -> ivHomeProductDrink.setImageBitmap(bitmap)
                        "desserts" -> ivHomeProductDessert.setImageBitmap(bitmap)
                    }
                }
                .addOnFailureListener {

                }
    }

}