package com.example.storeapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.storeapp.database.DatabaseHelper

class FragmentLogin : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        dbHelper = DatabaseHelper(requireContext())
        sharedPreferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val button = view.findViewById<Button>(R.id.loginLoginButton)
        val usernameEditText = view.findViewById<EditText>(R.id.loginTextUsername)
        val passwordEditText = view.findViewById<EditText>(R.id.loginTextPassword)
        val rememberMeCheckBox = view.findViewById<android.widget.CheckBox>(R.id.loginCheckBox)

        button.setOnClickListener {
            val email = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(activity, "Lütfen email ve şifre girin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check user in database
            val user = dbHelper.getUser(email, password)
            if (user != null) {
                // Save login status and user ID
                val token = "local_token_${user.id}"

                if (rememberMeCheckBox.isChecked) {
                    saveTokenToSharedPreferences(token)
                } else {
                    clearTokenFromSharedPreferences()
                }

                saveUserIdToSharedPreferences(user.id)

                Toast.makeText(activity, "Giriş başarılı!", Toast.LENGTH_SHORT).show()
                val intent = Intent(activity, HomeActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(
                    activity,
                    "Parol ya da username sehvdir",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return view
    }

    private fun saveTokenToSharedPreferences(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.apply()
    }

    private fun clearTokenFromSharedPreferences() {
        val editor = sharedPreferences.edit()
        editor.remove("token")
        editor.apply()
    }

    private fun saveUserIdToSharedPreferences(userId: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("userId", userId)
        editor.apply()
    }
}
