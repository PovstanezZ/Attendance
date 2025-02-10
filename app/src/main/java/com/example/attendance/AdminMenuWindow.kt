package com.example.attendance

import android.content.Intent
import android.os.Bundle
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
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var subjectsRecyclerView: RecyclerView
    private lateinit var addSubjectButton: Button
    private lateinit var openJournalButton: Button
    private val subjectsAdapter = SubjectsAdapter()

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
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_menu_window)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val logoutButton: Button = findViewById(R.id.admin_button_logout)
        logoutButton.setOnClickListener {
            logoutUser()
        }

        calendarRecyclerView = findViewById(R.id.weekCalendar)
        subjectsRecyclerView = findViewById(R.id.recyclerView)
        addSubjectButton = findViewById(R.id.btn_add_subject)
        openJournalButton = findViewById(R.id.btn_open_journal)

        setupCalendar()
        setupSubjectsRecyclerView()

        addSubjectButton.setOnClickListener {
            addNewSubject()
        }

        openJournalButton.setOnClickListener {
            openJournal()
        }
    }

    private fun setupCalendar() {
        calendarRecyclerView.layoutManager = GridLayoutManager(this, 7) // 7 дней в неделе
        val calendarAdapter = CalendarAdapter { selectedDate ->
            updateSubjectsForDate(selectedDate)
        }
        calendarRecyclerView.adapter = calendarAdapter
    }

    private fun setupSubjectsRecyclerView() {
        subjectsRecyclerView.layoutManager = LinearLayoutManager(this)
        subjectsRecyclerView.adapter = subjectsAdapter
    }

    private fun updateSubjectsForDate(selectedDate: String) {
        // Здесь можно подгружать предметы из БД или списка
        subjectsAdapter.updateSubjects(getSubjectsForDate(selectedDate))
    }

    private fun addNewSubject() {
        subjectsAdapter.addSubject("Новый предмет")
    }

    private fun openJournal() {
        val intent = Intent(this, JournalActivity::class.java)
        startActivity(intent)
    }

    private fun getSubjectsForDate(date: String): List<String> {
        // Здесь можно реализовать логику получения предметов по дате
        return listOf("Математика", "Физика", "История")
    }
}


