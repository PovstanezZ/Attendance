package com.example.attendance

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class StartWindow : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start_window)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userRole = sharedPreferences.getString("role", null)

        if (userRole == "admin") {
            startActivity(Intent(this, AdminMenuWindow::class.java))
            finish()
        } else if (userRole == "user") {
            startActivity(Intent(this, MenuWindow::class.java))
            finish()
        }

        val welcomeRegButton: TextView = findViewById(R.id.welcome_reg_button)
        val welcomeAuthButton: TextView = findViewById(R.id.welcome_auth_button)

        welcomeRegButton.setOnClickListener {
            val intent = Intent(this, RegistrationWindow::class.java)
            startActivity(intent)
        }

        welcomeAuthButton.setOnClickListener {
            val intent = Intent(this, LoginWindow::class.java)
            startActivity(intent)
        }
    }
}
