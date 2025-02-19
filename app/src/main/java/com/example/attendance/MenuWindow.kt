package com.example.attendance

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MenuWindow : AppCompatActivity() {

    private lateinit var recyclerViewCalendar: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var monthTextView: TextView
    private val calendar = Calendar.getInstance()
    private val monthFormat = SimpleDateFormat("LLLL yyyy", Locale("ru"))

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
        setContentView(R.layout.activity_menu_window)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val logoutButton: ImageButton = findViewById(R.id.button_logout)
        logoutButton.setOnClickListener {
            logoutUser()
        }
        val backWeekButton: Button = findViewById(R.id.back_week)
        val nextWeekButton: Button = findViewById(R.id.next_week)
        backWeekButton.text = "<<"
        nextWeekButton.text = ">>"

        monthTextView = findViewById(R.id.month)
        recyclerViewCalendar = findViewById(R.id.weekCalendar)
        recyclerViewCalendar.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        calendarAdapter = CalendarAdapter(calendar) { selectedDate ->
            Log.d("Selected Date", selectedDate)
        }
        recyclerViewCalendar.adapter = calendarAdapter

        updateMonth()

        backWeekButton.setOnClickListener {
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
            updateCalendar()
        }


        nextWeekButton.setOnClickListener {
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
            updateCalendar()
        }
    }
    private fun updateCalendar() {
        calendarAdapter.updateWeek(calendar)
        updateMonth()
    }

    private fun updateMonth() {
        val dateFormat = SimpleDateFormat("LLLL", Locale("ru"))
        monthTextView.text = dateFormat.format(calendar.time).capitalize()
    }
}