package com.example.attendance

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationWindow : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

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
        db = FirebaseFirestore.getInstance()

        // Объявление элементов
        val userEmail: EditText = findViewById(R.id.email_text)
        val userPass: EditText = findViewById(R.id.pass_text)
        val buttonReg: Button = findViewById(R.id.button_reg)
        val loginAuthText: TextView = findViewById(R.id.login_auth_text)

        // Переход в окно авторизации
        loginAuthText.setOnClickListener {
            val intent = Intent(this, LoginWindow::class.java)
            startActivity(intent)
        }

        // Регистрация
        buttonReg.setOnClickListener {
            val email = userEmail.text.toString().trim()
            val pass = userPass.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Заполни все поля!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                        checkAdminRoleAndSaveUser(userId, email)
                    } else {
                        val errorMessage = task.exception?.message ?: "Ошибка регистрации"
                        Toast.makeText(this, "Ошибка: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    // Проверяем, есть ли email в коллекции "admins", и сохраняем пользователя
    private fun checkAdminRoleAndSaveUser(userId: String, email: String) {
        db.collection("admins").document(email).get()
            .addOnSuccessListener { document ->
                val role = if (document.exists()) "admin" else "user"
                saveUserToFirestore(userId, email, role)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Ошибка проверки роли", Toast.LENGTH_LONG).show()
            }
    }

    // Сохранение данных пользователя в Firestore
    private fun saveUserToFirestore(userId: String, email: String, role: String) {
        val user = hashMapOf(
            "uid" to userId,
            "email" to email,
            "role" to role
        )

        db.collection("users").document(email)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Регистрация успешна! Роль: $role", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginWindow::class.java))
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка сохранения данных: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
