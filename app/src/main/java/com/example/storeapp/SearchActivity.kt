package com.example.storeapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storeapp.adapters.ProductAdapter
import com.example.storeapp.database.DatabaseHelper
import com.example.storeapp.databinding.ActivitySearchBinding
import com.example.storeapp.dialogs.EditProductDialog

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: ProductAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        setupBottomNavigation(this, R.id.fragmentSearch)
        setupRecyclerView()
        setupSearchView()
    }

    private fun setupRecyclerView() {
        val userId = sharedPreferences.getInt("userId", -1)
        if (userId == -1) return

        binding.layoutSearch.recyclerViewSearch.layoutManager = LinearLayoutManager(this)

        adapter = ProductAdapter(
            emptyArray(),
            dbHelper,
            userId,
            onDeleteCallback = { productId ->
                if (dbHelper.deleteProduct(productId, userId)) {
                    performSearch(binding.layoutSearch.searchView.text?.toString() ?: "")
                }
            },
            onEditCallback = { product ->
                val editDialog = EditProductDialog(product, dbHelper) {
                    performSearch(binding.layoutSearch.searchView.text?.toString() ?: "")
                }
                editDialog.show(supportFragmentManager, "EditProductDialog")
            }
        )
        binding.layoutSearch.recyclerViewSearch.adapter = adapter
    }

    private fun setupSearchView() {
        binding.layoutSearch.searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString() ?: ""
                performSearch(query)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun performSearch(query: String) {
        val userId = sharedPreferences.getInt("userId", -1)
        if (userId == -1) return

        if (query.isEmpty()) {
            binding.layoutSearch.textViewNoResults.visibility = View.GONE
            binding.layoutSearch.recyclerViewSearch.visibility = View.VISIBLE
            binding.layoutSearch.recyclerViewSearch.adapter = ProductAdapter(emptyArray(), dbHelper, userId, {}, { _ -> })
            return
        }

        try {
            val searchResults = dbHelper.searchProductsByName(query, userId)
            if (searchResults.isEmpty()) {
                binding.layoutSearch.textViewNoResults.visibility = View.VISIBLE
                binding.layoutSearch.recyclerViewSearch.visibility = View.GONE
            } else {
                binding.layoutSearch.textViewNoResults.visibility = View.GONE
                binding.layoutSearch.recyclerViewSearch.visibility = View.VISIBLE
                adapter = ProductAdapter(
                    searchResults.toTypedArray(),
                    dbHelper,
                    userId,
                    onDeleteCallback = { productId ->
                        try {
                            if (dbHelper.deleteProduct(productId, userId)) performSearch(query)
                        } catch (e: com.example.storeapp.models.AppException.DatabaseException) {
                            Toast.makeText(this, "Silme hatası: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onEditCallback = { product ->
                        EditProductDialog(product, dbHelper) { performSearch(query) }
                            .show(supportFragmentManager, "EditProductDialog")
                    }
                )
                binding.layoutSearch.recyclerViewSearch.adapter = adapter
            }
        } catch (e: com.example.storeapp.models.AppException.DatabaseException) {
            Toast.makeText(this, "Arama hatası: ${e.message}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Beklenmedik bir hata oluştu", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigationView.selectedItemId = R.id.fragmentSearch
    }
}
