package com.example.attendance

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class AdminMenuWindow : AppCompatActivity() {

    private lateinit var recyclerViewCalendar: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var monthTextView: TextView
    private val calendar = Calendar.getInstance()

    private fun logoutUser() {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("role")
        editor.apply()

        FirebaseAuth.getInstance().signOut()

        val intent = Intent(this, StartWindow::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_menu_window)

        val logoutButton: Button = findViewById(R.id.admin_button_logout)
        logoutButton.setOnClickListener { logoutUser() }

        val backWeekButton: Button = findViewById(R.id.admin_back_week)
        val nextWeekButton: Button = findViewById(R.id.admin_next_week)
        backWeekButton.text = "<<"
        nextWeekButton.text = ">>"

        monthTextView = findViewById(R.id.admin_month)
        recyclerViewCalendar = findViewById(R.id.admin_weekCalendar)
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

