package com.example.storeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            val sharedPreferences = getSharedPreferences("MyPreferences", android.content.Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("token", null)

            val intent = if (token != null) {
                Intent(this, HomeActivity::class.java)
            } else {
                Intent(this, MainActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 3000)
    }
}