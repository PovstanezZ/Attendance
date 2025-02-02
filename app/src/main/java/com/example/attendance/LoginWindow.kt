package com.example.attendance

import android.content.Intent
import android.content.SharedPreferences
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
import com.google.firebase.firestore.FirebaseFirestore

class LoginWindow : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

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
        db = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        val userEmail: EditText = findViewById(R.id.auth_email_text)
        val userPass: EditText = findViewById(R.id.auth_pass_text)
        val buttonAuth: Button = findViewById(R.id.button_auth)
        val loginRegText: TextView = findViewById(R.id.login_reg_text)

        loginRegText.setOnClickListener {
            val intent = Intent(this, RegistrationWindow::class.java)
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
                            checkUserRole(email)
                        } else {
                            val errorMessage = task.exception?.message ?: "Ошибка входа"
                            Toast.makeText(this, "Ошибка входа: $errorMessage", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }

    // Проверка роли пользователя по email
    private fun checkUserRole(email: String) {
        db.collection("admins").document(email).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    updateRoleToAdmin(email)
                    saveUserRole("admin")
                    navigateToAdminMenu()
                } else {
                    updateRoleToUser(email)
                    saveUserRole("user")
                    navigateToUserMenu()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Ошибка проверки роли", Toast.LENGTH_LONG).show()
            }
    }

    // Обновление роли пользователя на 'user'
    private fun updateRoleToUser(email: String) {
        val userRef = db.collection("users").document(email)
        userRef.update("role", "user")
            .addOnSuccessListener {
                Toast.makeText(this, "Роль обновлена на user", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка при обновлении роли: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // Обновление роли пользователя на 'admin'
    private fun updateRoleToAdmin(email: String) {
        val userRef = db.collection("users").document(email)
        userRef.update("role", "admin")
            .addOnSuccessListener {
                Toast.makeText(this, "Роль обновлена до admin", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка при обновлении роли: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // Сохранение роли пользователя в SharedPreferences
    private fun saveUserRole(role: String) {
        val editor = sharedPreferences.edit()
        editor.putString("role", role)
        editor.apply()
    }

    // Перенаправление на окно администратора
    private fun navigateToAdminMenu() {
        Toast.makeText(this, "Добро пожаловать, администратор!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, AdminMenuWindow::class.java)
        startActivity(intent)
        finish()
    }

    // Перенаправление на окно обычного пользователя
    private fun navigateToUserMenu() {
        Toast.makeText(this, "Добро пожаловать, пользователь!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MenuWindow::class.java)
        startActivity(intent)
        finish()
    }
}
