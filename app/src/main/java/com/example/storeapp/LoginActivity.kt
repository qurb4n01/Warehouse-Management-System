package com.example.storeapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.storeapp.database.DatabaseHelper

class LoginActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_login)

        dbHelper = DatabaseHelper(this)
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val button = findViewById<Button>(R.id.loginLoginButton)
        val usernameEditText = findViewById<EditText>(R.id.loginTextUsername)
        val passwordEditText = findViewById<EditText>(R.id.loginTextPassword)
        val rememberMeCheckBox = findViewById<android.widget.CheckBox>(R.id.loginCheckBox)

        button.setOnClickListener {
            val email = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Lütfen email ve şifre girin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
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

                    Toast.makeText(this, "Giriş başarılı!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Parol ya da username sehvdir",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: com.example.storeapp.models.AppException.DatabaseException) {
                Toast.makeText(this, "Veritabanı hatası: ${e.message}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Beklenmedik bir hata oluştu", Toast.LENGTH_LONG).show()
            }
        }
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
