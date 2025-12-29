package com.example.storeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.storeapp.database.DatabaseHelper

class SignupActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_signup)

        dbHelper = DatabaseHelper(this)

        val button = findViewById<Button>(R.id.signupsignupButton)
        val usernameEditText = findViewById<EditText>(R.id.signupTextUsername)
        val passwordEditText = findViewById<EditText>(R.id.signupTextPassword)
        val fullnameEditText = findViewById<EditText>(R.id.signupTextFullname)

        button.setOnClickListener {
            val email = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString()
            val fullname = fullnameEditText.text.toString().trim()

            // Validate input
            if (email.isEmpty() || password.isEmpty() || fullname.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if user already exists
            if (dbHelper.userExists(email)) {
                Toast.makeText(this, "Bu email adresi zaten kullanılıyor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Add user to database
            val result = dbHelper.addUser(email, password, fullname)
            if (result > 0) {
                Toast.makeText(this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Kayıt başarısız. Lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
