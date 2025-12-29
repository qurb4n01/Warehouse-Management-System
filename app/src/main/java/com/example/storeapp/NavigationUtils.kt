package com.example.storeapp

import android.content.Context
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView

fun setupBottomNavigation(activity: androidx.appcompat.app.AppCompatActivity, currentItemId: Int) {
    val bottomNavigationView = activity.findViewById<BottomNavigationView>(R.id.bottomNavigationView) ?: return
    bottomNavigationView.selectedItemId = currentItemId

    bottomNavigationView.setOnItemSelectedListener { item ->
        if (item.itemId == currentItemId) return@setOnItemSelectedListener true

        val intent = when (item.itemId) {
            R.id.fragmentHome -> Intent(activity, HomeActivity::class.java)
            R.id.fragmentSearch -> Intent(activity, SearchActivity::class.java)
            R.id.fragmentAdd -> Intent(activity, AddActivity::class.java)
            R.id.fragmentTasks -> Intent(activity, TasksActivity::class.java)
            R.id.fragmentProfile -> Intent(activity, ProfileActivity::class.java)
            else -> null
        }

        intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) 
            activity.startActivity(it)
            true
        } ?: false
    }
}
