package com.example.attendance

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class RegistrationWindow : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration_window)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()

        //Объявление кнопок
        val userEmail: EditText = findViewById(R.id.email_text)
        val userPass: EditText = findViewById(R.id.pass_text)
        val buttonReg: Button = findViewById(R.id.button_reg)
        val login_auth_text: TextView = findViewById(R.id.login_auth_text)

        //Переход между окнами авторизации
        login_auth_text.setOnClickListener{
            val intent = Intent (this,LoginWindow::class.java)
            startActivity(intent)
        }

        //Регистрация
        buttonReg.setOnClickListener {
            val email = userEmail.text.toString().trim()
            val pass = userPass.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Не все поля заполнены, долбаёб >.<", Toast.LENGTH_LONG).show()
            }
            else {
                auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                            val intent = Intent (this,LoginWindow::class.java)
                            startActivity(intent)
                        } else {
                            val errorMessage = task.exception?.message ?: "Неизвестная ошибка"
                            Toast.makeText(this, "Ошибка регистрации: $errorMessage", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }
}