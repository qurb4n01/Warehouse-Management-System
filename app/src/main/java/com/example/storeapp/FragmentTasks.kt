package com.example.storeapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.storeapp.databinding.FragmentTasksBinding

class FragmentTasks : Fragment() {
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set warehouse tasks
        binding.textViewTasksTitle.text = "Warehouse Tasks"

        val tasks = listOf(
            "1. Daily Inventory Count",
            "2. Receive and Inspect Incoming Shipments",
            "3. Update Product Quantities in System",
            "4. Organize Products by Category",
            "5. Check Expiration Dates",
            "6. Label New Products with QR Codes",
            "7. Conduct Weekly Stock Audit",
            "8. Prepare Products for Shipment",
            "9. Update Product Prices",
            "10. Maintain Warehouse Cleanliness",
            "11. Check Storage Conditions",
            "12. Report Damaged Products",
            "13. Update Product Locations",
            "14. Verify Product Information Accuracy",
            "15. Generate Inventory Reports"
        )

        binding.textViewTasksContent.text = tasks.joinToString("\n\n")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

