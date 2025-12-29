package com.example.storeapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storeapp.adapters.ProductAdapter
import com.example.storeapp.database.DatabaseHelper
import com.example.storeapp.databinding.ActivityHomeBinding
import com.example.storeapp.dialogs.EditProductDialog

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        setupBottomNavigation(this, R.id.fragmentHome)
        loadProducts()
    }

    private fun loadProducts() {
        val userId = sharedPreferences.getInt("userId", -1)
        if (userId == -1) return

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        val productsList = dbHelper.getAllProducts(userId)
        val productsArray = productsList.toTypedArray()
        
        val adapter = ProductAdapter(
            productsArray,
            dbHelper,
            userId,
            onDeleteCallback = { productId ->
                if (dbHelper.deleteProduct(productId, userId)) loadProducts()
            },
            onEditCallback = { product ->
                val editDialog = EditProductDialog(product, dbHelper) { loadProducts() }
                editDialog.show(supportFragmentManager, "EditProductDialog")
            }
        )
        binding.recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
        binding.bottomNavigationView.selectedItemId = R.id.fragmentHome
    }
}
