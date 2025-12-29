package com.example.storeapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.storeapp.adapters.ProductAdapter
import com.example.storeapp.database.DatabaseHelper
import com.example.storeapp.databinding.FragmentSearchBinding
import com.example.storeapp.dialogs.EditProductDialog

class FragmentSearch : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: ProductAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        dbHelper = DatabaseHelper(requireContext())
        sharedPreferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
    }

    private fun setupRecyclerView() {
        val userId = sharedPreferences.getInt("userId", -1)
        if (userId == -1) {
            return
        }

        val recyclerView: RecyclerView = binding.recyclerViewSearch
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize with empty list
        val emptyArray = emptyArray<com.example.storeapp.models.Product>()
        adapter = ProductAdapter(
            emptyArray,
            dbHelper,
            userId,
            onDeleteCallback = { productId ->
                if (dbHelper.deleteProduct(productId, userId)) {
                    performSearch(binding.searchView.text?.toString() ?: "")
                }
            },
            onEditCallback = { product ->
                val editDialog = EditProductDialog(product, dbHelper) {
                    performSearch(binding.searchView.text?.toString() ?: "")
                }
                editDialog.show(parentFragmentManager, "EditProductDialog")
            }
        )
        recyclerView.adapter = adapter
    }

    private fun setupSearchView() {
        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString() ?: ""
                if (query.isNotEmpty()) {
                    performSearch(query)
                } else {
                    // Clear results when search is empty
                    val userId = sharedPreferences.getInt("userId", -1)
                    if (userId != -1) {
                        adapter = ProductAdapter(
                            emptyArray(),
                            dbHelper,
                            userId,
                            onDeleteCallback = { productId ->
                                if (dbHelper.deleteProduct(productId, userId)) {
                                    performSearch(binding.searchView.text?.toString() ?: "")
                                }
                            },
                            onEditCallback = { product ->
                                val editDialog = EditProductDialog(product, dbHelper) {
                                    performSearch(binding.searchView.text?.toString() ?: "")
                                }
                                editDialog.show(parentFragmentManager, "EditProductDialog")
                            }
                        )
                        binding.recyclerViewSearch.adapter = adapter
                        binding.textViewNoResults.visibility = View.GONE
                        binding.recyclerViewSearch.visibility = View.VISIBLE
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun performSearch(query: String) {
        val userId = sharedPreferences.getInt("userId", -1)
        if (userId == -1) {
            return
        }

        if (query.isEmpty()) {
            binding.textViewNoResults.visibility = View.GONE
            binding.recyclerViewSearch.visibility = View.VISIBLE
            return
        }

        val searchResults = dbHelper.searchProductsByName(query, userId)

        if (searchResults.isEmpty()) {
            binding.textViewNoResults.visibility = View.VISIBLE
            binding.recyclerViewSearch.visibility = View.GONE
        } else {
            binding.textViewNoResults.visibility = View.GONE
            binding.recyclerViewSearch.visibility = View.VISIBLE

            val productsArray = searchResults.toTypedArray()
            adapter = ProductAdapter(
                productsArray,
                dbHelper,
                userId,
                onDeleteCallback = { productId ->
                    if (dbHelper.deleteProduct(productId, userId)) {
                        performSearch(query)
                    }
                },
                onEditCallback = { product ->
                    val editDialog = EditProductDialog(product, dbHelper) {
                        performSearch(query)
                    }
                    editDialog.show(parentFragmentManager, "EditProductDialog")
                }
            )
            binding.recyclerViewSearch.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}