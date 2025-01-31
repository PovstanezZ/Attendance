package com.example.attendance

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginWindow : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_window)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()

        val userEmail: EditText = findViewById(R.id.auth_email_text)
        val userPass: EditText = findViewById(R.id.auth_pass_text)
        val buttonAuth: Button = findViewById(R.id.button_auth)
        val login_reg_text: TextView = findViewById(R.id.login_reg_text)

        login_reg_text.setOnClickListener{
            val intent = Intent (this,RegistrationWindow::class.java)
            startActivity(intent)
        }

        buttonAuth.setOnClickListener {
            val email = userEmail.text.toString().trim()
            val password = userPass.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_LONG).show()
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Авторизация успешна!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MenuWindow::class.java))
                            finish()
                        } else {
                            val errorMessage = task.exception?.message ?: "Ошибка входа"
                            Toast.makeText(this, "Ошибка входа: $errorMessage", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }
}