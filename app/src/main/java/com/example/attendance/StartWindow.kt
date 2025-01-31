package com.example.attendance

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

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

        val welcome_reg_button: TextView = findViewById(R.id.welcome_reg_button)
        val welcome_auth_button: TextView = findViewById(R.id.welcome_auth_button)

        welcome_reg_button.setOnClickListener{
            val intent = Intent (this,RegistrationWindow::class.java)
            startActivity(intent)
        }
        welcome_auth_button.setOnClickListener{
            val intent = Intent (this,LoginWindow::class.java)
            startActivity(intent)
        }

    }
}