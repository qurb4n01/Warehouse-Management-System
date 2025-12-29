package com.example.storeapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.storeapp.database.DatabaseHelper
import com.example.storeapp.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences
    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        setupBottomNavigation(this, R.id.fragmentProfile)

        currentUserId = sharedPreferences.getInt("userId", -1)
        if (currentUserId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadUserInfo()

        binding.layoutProfile.btnSaveProfile.setOnClickListener {
            saveUserInfo()
        }

        binding.layoutProfile.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun loadUserInfo() {
        val user = dbHelper.getUserById(currentUserId)
        if (user != null) {
            binding.layoutProfile.editProfileFullName.setText(user.fullName)
            binding.layoutProfile.editProfileEmail.setText(user.email)
            binding.layoutProfile.editProfilePassword.setText(user.password)
        }
    }

    private fun saveUserInfo() {
        val fullName = binding.layoutProfile.editProfileFullName.text.toString().trim()
        val email = binding.layoutProfile.editProfileEmail.text.toString().trim()
        val password = binding.layoutProfile.editProfilePassword.text.toString()

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (dbHelper.updateUser(currentUserId, email, password, fullName)) {
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigationView.selectedItemId = R.id.fragmentProfile
    }
}
