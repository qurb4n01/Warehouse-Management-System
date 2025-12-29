package com.example.storeapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.storeapp.database.DatabaseHelper
import com.example.storeapp.databinding.FragmentProfileBinding
import com.google.android.material.textfield.TextInputEditText

class FragmentProfile : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences
    private var currentUserId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        dbHelper = DatabaseHelper(requireContext())
        sharedPreferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get current user ID from SharedPreferences
        currentUserId = sharedPreferences.getInt("userId", -1)

        if (currentUserId == -1) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Load and display user information
        loadUserInfo()

        // Save button
        binding.btnSaveProfile.setOnClickListener {
            saveUserInfo()
        }

        // Logout button
        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun loadUserInfo() {
        val user = dbHelper.getUserById(currentUserId)
        if (user != null) {
            binding.editProfileFullName.setText(user.fullName)
            binding.editProfileEmail.setText(user.email)
            binding.editProfilePassword.setText(user.password)
        } else {
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserInfo() {
        val fullName = binding.editProfileFullName.text.toString().trim()
        val email = binding.editProfileEmail.text.toString().trim()
        val password = binding.editProfilePassword.text.toString()

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (dbHelper.updateUser(currentUserId, email, password, fullName)) {
            Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout() {
        // Clear SharedPreferences
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        // Navigate to login
        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}