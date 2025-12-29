package com.example.storeapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.storeapp.R
import com.example.storeapp.models.Product

class ProductAdapter(
    private val productList: Array<Product>,
    private val dbHelper: com.example.storeapp.database.DatabaseHelper,
    private val userId: Int,
    private val onDeleteCallback: (Int) -> Unit,
    private val onEditCallback: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productId: TextView = itemView.findViewById(R.id.textViewProductId)
        val productName: TextView = itemView.findViewById(R.id.textViewProductName)
        val productCount: TextView = itemView.findViewById(R.id.textViewProductCount)
        val productPrice: TextView = itemView.findViewById(R.id.textViewProductPrice)
        val btnEdit: android.widget.Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct = productList[position]
        // Show Product Name in the green box
        holder.productId.text = currentProduct.name.ifEmpty { "No Name" }
        holder.productName.text = currentProduct.qrCode.ifEmpty { "No QR Code" }
        holder.productCount.text = currentProduct.productCount.toString()
        holder.productPrice.text = currentProduct.price.toString()

        // Edit button functionality
        holder.btnEdit.setOnClickListener {
            onEditCallback(currentProduct)
        }

        // Delete button functionality
        holder.btnDelete.setOnClickListener {
            if (dbHelper.deleteProduct(currentProduct.id, userId)) {
                Toast.makeText(holder.itemView.context, "Ürün silindi", Toast.LENGTH_SHORT).show()
                onDeleteCallback(currentProduct.id)
            } else {
                Toast.makeText(holder.itemView.context, "Silme hatası", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}
