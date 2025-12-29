package com.example.storeapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.storeapp.database.DatabaseHelper

class FragmentSignup : Fragment() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)
        dbHelper = DatabaseHelper(requireContext())

        val button = view.findViewById<Button>(R.id.signupsignupButton)
        val usernameEditText = view.findViewById<EditText>(R.id.signupTextUsername)
        val passwordEditText = view.findViewById<EditText>(R.id.signupTextPassword)
        val fullnameEditText = view.findViewById<EditText>(R.id.signupTextFullname)

        button.setOnClickListener {
            val email = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString()
            val fullname = fullnameEditText.text.toString().trim()

            // Validate input
            if (email.isEmpty() || password.isEmpty() || fullname.isEmpty()) {
                Toast.makeText(activity, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if user already exists
            if (dbHelper.userExists(email)) {
                Toast.makeText(activity, "Bu email adresi zaten kullanılıyor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Add user to database
            val result = dbHelper.addUser(email, password, fullname)
            if (result > 0) {
                Toast.makeText(activity, "Kayıt başarılı!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_fragmentSignup_to_fragmentLogin)
            } else {
                Toast.makeText(activity, "Kayıt başarısız. Lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }
}
