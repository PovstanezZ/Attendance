package com.example.attendance

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class AdminMenuWindow : AppCompatActivity() {

    // Логика выхода из приложения
    private fun logoutUser() {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("role")  // Удаляем сохраненную роль
        editor.apply()

        FirebaseAuth.getInstance().signOut() // Выход из Firebase

        // Переход на стартовое окно
        val intent = Intent(this, StartWindow::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Очистка всех предыдущих активностей
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_menu_window)

        // Установка слушателя на кнопку "Log Out"
        val logoutButton: Button = findViewById(R.id.admin_button_logout)
        logoutButton.setOnClickListener {
            logoutUser()
        }

        // Календарь
        val recyclerViewCalendar: RecyclerView = findViewById(R.id.weekCalendar)
        recyclerViewCalendar.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val calendarAdapter = CalendarAdapter { selectedDate ->
            // Обработка выбора даты
            Log.d("Selected Date", selectedDate)
        }
        recyclerViewCalendar.adapter = calendarAdapter

        // Уроки
        val recyclerViewSubjects: RecyclerView = findViewById(R.id.recyclerView)
        recyclerViewSubjects.layoutManager = LinearLayoutManager(this)
        val subjects = listOf("Математика", "Русский язык", "История")  // Пример списка
        val subjectAdapter = SubjectAdapter(subjects)
        recyclerViewSubjects.adapter = subjectAdapter

        // Кнопка "Добавить предмет"
        val addSubjectButton: Button = findViewById(R.id.btn_add_subject)
        addSubjectButton.setOnClickListener {
            // Логика для добавления предмета
            // Можно открыть диалог или новое активити
        }

        // Кнопка "Журнал"
        val openJournalButton: Button = findViewById(R.id.btn_open_journal)
        openJournalButton.setOnClickListener {
            // Логика для перехода в журнал
            val intent = Intent(this, JournalActivity::class.java) // Переход к журналу
            startActivity(intent)
        }
    }
}



