package com.example.storeapp

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.storeapp.database.DatabaseHelper
import com.example.storeapp.databinding.FragmentAddBinding

class FragmentAdd : Fragment() {
    private var _binding: FragmentAddBinding?=null
    private val binding get()=_binding!!
    private lateinit var codeScanner: CodeScanner
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activityResultLauncher.launch(arrayOf(Manifest.permission.CAMERA))
        _binding= FragmentAddBinding.inflate(inflater,container,false)
        sharedPreferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        codeScanner = CodeScanner(requireContext(), binding.scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = true
        codeScanner.decodeCallback = DecodeCallback {
            requireActivity().runOnUiThread {
                binding.tvResult.text = "ID: ${it.text} added to Database"
                addProductToDatabase(it.text)
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            requireActivity().runOnUiThread {
                Toast.makeText(
                    requireContext(),
                    "Error scanner: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        activityResultLauncher.launch(arrayOf(Manifest.permission.CAMERA))

        codeScanner.startPreview()
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach { entry ->
                if (!entry.value) {
                    Toast.makeText(requireContext(), "Please enable camera permission to use this feature!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun addProductToDatabase(qrCodeText: String) {
        val userId = sharedPreferences.getInt("userId", -1)
        if (userId == -1) {
            Toast.makeText(requireContext(), "Kullanıcı giriş yapmamış", Toast.LENGTH_SHORT).show()
            return
        }

        val dbHelper = DatabaseHelper(requireContext())
        val existingProduct = dbHelper.getProductByQrCode(qrCodeText, userId)

        if (existingProduct != null) {
            Toast.makeText(requireContext(), "Bu QR kod zaten kayıtlı: ${existingProduct.name}", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(requireContext(), "Ürün veritabanına eklendi!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Ürün eklenirken hata oluştu", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}
