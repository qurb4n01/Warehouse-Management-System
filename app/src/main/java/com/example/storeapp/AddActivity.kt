package com.example.storeapp

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.storeapp.database.DatabaseHelper
import com.example.storeapp.databinding.ActivityAddBinding

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private lateinit var codeScanner: CodeScanner
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        setupBottomNavigation(this, R.id.fragmentAdd)

        activityResultLauncher.launch(arrayOf(Manifest.permission.CAMERA))

        codeScanner = CodeScanner(this, binding.layoutAdd.scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = true
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                binding.layoutAdd.tvResult.text = "ID: ${it.text} added to Database"
                addProductToDatabase(it.text)
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this, "Error scanner: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }

        codeScanner.startPreview()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
        binding.bottomNavigationView.selectedItemId = R.id.fragmentAdd
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach { entry ->
                if (!entry.value) {
                    Toast.makeText(this, "Please enable camera permission to use this feature!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun addProductToDatabase(qrCodeText: String) {
        val userId = sharedPreferences.getInt("userId", -1)
        if (userId == -1) return

<<<<<<< HEAD
        try {
            val dbHelper = DatabaseHelper(this)
            val existingProduct = dbHelper.getProductByQrCode(qrCodeText, userId)

            if (existingProduct != null) {
                Toast.makeText(this, "Bu QR kod zaten kayıtlı: ${existingProduct.name}", Toast.LENGTH_SHORT).show()
                return
            }

            val newProduct = com.example.storeapp.models.Product(
                id = 0,
                name = qrCodeText,
                productCount = 0,
                price = 0.0,
                qrCode = qrCodeText,
                userId = userId,
                category = com.example.storeapp.models.ProductCategory.OTHERS
            )

            val result = dbHelper.addProduct(newProduct)
            if (result > 0) {
                Toast.makeText(this, "Ürün veritabanına eklendi!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Ürün eklenirken hata oluştu", Toast.LENGTH_SHORT).show()
            }
        } catch (e: com.example.storeapp.models.AppException.DatabaseException) {
            Toast.makeText(this, "Veritabanı hatası: ${e.message}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Beklenmedik bir hata oluştu", Toast.LENGTH_LONG).show()
=======
        val dbHelper = DatabaseHelper(this)
        val existingProduct = dbHelper.getProductByQrCode(qrCodeText, userId)

        if (existingProduct != null) {
            Toast.makeText(this, "Bu QR kod zaten kayıtlı: ${existingProduct.name}", Toast.LENGTH_SHORT).show()
            return
        }

        val newProduct = com.example.storeapp.models.Product(
            id = 0,
            name = qrCodeText,
            productCount = 0,
            price = 0.0,
            qrCode = qrCodeText,
            userId = userId
        )

        val result = dbHelper.addProduct(newProduct)
        if (result > 0) {
            Toast.makeText(this, "Ürün veritabanına eklendi!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Ürün eklenirken hata oluştu", Toast.LENGTH_SHORT).show()
>>>>>>> 143d4c0c0b34ade09b11b274824de82891a56248
        }
    }
}
