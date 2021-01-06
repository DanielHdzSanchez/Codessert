package com.dan.codessert.products

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dan.codessert.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

class DrinkAdapter(options: FirestoreRecyclerOptions<Product>) : FirestoreRecyclerAdapter<Product, DrinkAdapter.ProductAdapterVH>(options){
    lateinit var listener: OnItemClickListener
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdapterVH {
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        return ProductAdapterVH(LayoutInflater.from(parent.context).inflate(R.layout.drinks_cards, parent, false))
    }

    override fun onBindViewHolder(holder: ProductAdapterVH, position: Int, model: Product) {
        holder.name.text = model.name
        holder.price.text = "Precio: $${model.price}"

        val ref = storageReference.child("drinks/${model.id.trim()}.jpg")
        val mb: Long = 4096 * 4096
        ref.getBytes(mb)
                .addOnSuccessListener {
                    val bitmap: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    holder.image.setImageBitmap(bitmap)
                }
                .addOnFailureListener {

                }
    }

    inner class ProductAdapterVH(itemView: View): RecyclerView.ViewHolder(itemView) {
        var name = itemView.findViewById<TextView>(R.id.tvCardDrinkName)
        var price = itemView.findViewById<TextView>(R.id.tvCardDrinkPrice)
        var image = itemView.findViewById<CircleImageView>(R.id.ivCardDrinkImage)
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(snapshots.getSnapshot(position), position)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(documentSnapshot: DocumentSnapshot, position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

}