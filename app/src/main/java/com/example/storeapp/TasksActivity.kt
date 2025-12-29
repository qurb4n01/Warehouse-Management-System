package com.example.storeapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.storeapp.databinding.ActivityTasksBinding

class TasksActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTasksBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation(this, R.id.fragmentTasks)

        binding.layoutTasks.textViewTasksTitle.text = "Warehouse Tasks"
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
        binding.layoutTasks.textViewTasksContent.text = tasks.joinToString("\n\n")
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigationView.selectedItemId = R.id.fragmentTasks
    }
}
