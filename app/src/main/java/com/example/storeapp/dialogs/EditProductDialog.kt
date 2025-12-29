package com.example.storeapp.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.storeapp.R
import com.example.storeapp.database.DatabaseHelper
import com.example.storeapp.models.Product

class EditProductDialog(
    private val product: Product,
    private val dbHelper: DatabaseHelper,
    private val onUpdateCallback: () -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_edit_product, null)

        val editName = view.findViewById<EditText>(R.id.editProductName)
        val editProductCount = view.findViewById<EditText>(R.id.editProductCount)
        val editPrice = view.findViewById<EditText>(R.id.editPrice)
<<<<<<< HEAD
        val spinnerCategory = view.findViewById<android.widget.Spinner>(R.id.editProductCategory)
=======
>>>>>>> 143d4c0c0b34ade09b11b274824de82891a56248
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        // Populate fields with current product values
        editName.setText(product.name)
        editProductCount.setText(product.productCount.toString())
        editPrice.setText(product.price.toString())

<<<<<<< HEAD
        // Setup Spinner
        val categories = com.example.storeapp.models.ProductCategory.values()
        val adapter = android.widget.ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories.map { it.displayName }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
        spinnerCategory.setSelection(categories.indexOf(product.category))

=======
>>>>>>> 143d4c0c0b34ade09b11b274824de82891a56248
        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()

        btnSave.setOnClickListener {
            val name = editName.text.toString().trim()
            val productCount = editProductCount.text.toString().toIntOrNull() ?: 0
            val price = editPrice.text.toString().toDoubleOrNull() ?: 0.0

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Ürün adı boş olamaz", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

<<<<<<< HEAD
            val selectedCategory = com.example.storeapp.models.ProductCategory.values()[spinnerCategory.selectedItemPosition]

=======
>>>>>>> 143d4c0c0b34ade09b11b274824de82891a56248
            val updatedProduct = Product(
                id = product.id,
                name = name,
                productCount = productCount,
                price = price,
                qrCode = product.qrCode,
<<<<<<< HEAD
                userId = product.userId,
                category = selectedCategory
            )

            try {
                if (dbHelper.updateProduct(updatedProduct)) {
                    Toast.makeText(requireContext(), "Ürün güncellendi", Toast.LENGTH_SHORT).show()
                    onUpdateCallback()
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), "Güncelleme hatası", Toast.LENGTH_SHORT).show()
                }
            } catch (e: com.example.storeapp.models.AppException.DatabaseException) {
                Toast.makeText(requireContext(), "Veritabanı hatası: ${e.message}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Beklenmedik bir hata oluştu", Toast.LENGTH_LONG).show()
=======
                userId = product.userId
            )

            if (dbHelper.updateProduct(updatedProduct)) {
                Toast.makeText(requireContext(), "Ürün güncellendi", Toast.LENGTH_SHORT).show()
                onUpdateCallback()
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Güncelleme hatası", Toast.LENGTH_SHORT).show()
>>>>>>> 143d4c0c0b34ade09b11b274824de82891a56248
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }
}

