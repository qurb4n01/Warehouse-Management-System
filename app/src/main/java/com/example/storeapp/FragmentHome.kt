package com.example.storeapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.storeapp.adapters.ProductAdapter
import com.example.storeapp.database.DatabaseHelper
import com.example.storeapp.databinding.FragmentHomeBinding
import com.example.storeapp.dialogs.EditProductDialog

class FragmentHome : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        dbHelper = DatabaseHelper(requireContext())
        sharedPreferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProducts()
    }

    private fun loadProducts() {
        val userId = sharedPreferences.getInt("userId", -1)
        if (userId == -1) {
            return
        }

        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Get products from database filtered by user
        val productsList = dbHelper.getAllProducts(userId)

        if (productsList.isEmpty()) {
            // Show empty state if needed
            return
        }

        // Convert List to Array for adapter
        val productsArray = productsList.toTypedArray()
        val adapter = ProductAdapter(
            productsArray,
            dbHelper,
            userId,
            onDeleteCallback = { productId ->
                // Delete callback
                if (dbHelper.deleteProduct(productId, userId)) {
                    loadProducts() // Refresh list
                }
            },
            onEditCallback = { product ->
                // Edit callback - show edit dialog
                val editDialog = EditProductDialog(product, dbHelper) {
                    loadProducts() // Refresh list after update
                }
                editDialog.show(parentFragmentManager, "EditProductDialog")
            }
        )
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        // Refresh products when returning to this fragment
        loadProducts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




