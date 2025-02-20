package com.example.attendance

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendance.JournalWindow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AdminMenuWindow : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var subjectAdapter: SubjectAdapter
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var monthTextView: TextView
    private var selectedDate: String? = null
    private val db = FirebaseFirestore.getInstance()
    private val calendar = Calendar.getInstance()
    private val monthFormat = SimpleDateFormat("LLLL yyyy", Locale("ru"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_menu_window)

        // Инициализация RecyclerView для уроков
        recyclerView = findViewById(R.id.admin_sub_res_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        subjectAdapter = SubjectAdapter(
            onDelete = { lessonId -> deleteLesson(lessonId) },
            onRename = { lessonId, newName -> renameLesson(lessonId, newName) }
        )
        recyclerView.adapter = subjectAdapter

        val journalIntentButton: Button = findViewById(R.id.btn_open_journal)
        val intentJournalBtn = Intent(this, JournalWindow::class.java)
        journalIntentButton.setOnClickListener{
            startActivity(intentJournalBtn)
        }

        // Инициализация календаря
        val calendarRecyclerView: RecyclerView = findViewById(R.id.admin_weekCalendar)
        calendarAdapter = CalendarAdapter(calendar) { date ->
            selectedDate = date
            loadLessonsForDate(selectedDate!!)
        }
        calendarRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        calendarRecyclerView.adapter = calendarAdapter

        // Выйти из аккаунта
        val logoutButton: ImageButton = findViewById(R.id.admin_button_logout)
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut() // Выход из аккаунта
            val intent = Intent(this, LoginWindow::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Текстовое поле для отображения месяца
        monthTextView = findViewById(R.id.admin_month)
        updateMonthDisplay()

        // Установка сегодняшней даты как выбранной
        selectedDate = getTodayDate()

        // Загрузка уроков для текущей даты
        loadLessonsForDate(selectedDate!!)

        // Кнопка добавления урока
        val addLessonButton: Button = findViewById(R.id.admin_btn_add_subject)
        addLessonButton.setOnClickListener { addLesson() }

        // Обработчики кнопок для смены недели
        val backWeekButton: Button = findViewById(R.id.admin_back_week)
        val nextWeekButton: Button = findViewById(R.id.admin_next_week)
        backWeekButton.text = "<<"
        nextWeekButton.text = ">>"

        backWeekButton.setOnClickListener {
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
            updateCalendar()
        }

        nextWeekButton.setOnClickListener {
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
            updateCalendar()
        }
    }

    private fun getTodayDate(): String {
        val today = Calendar.getInstance()
        return today.get(Calendar.DAY_OF_MONTH).toString()
    }

    private fun loadLessonsForDate(date: String) {
        db.collection("lessons")
            .whereEqualTo("date", date)
            .get()
            .addOnSuccessListener { result ->
                val lessons = result.map { doc ->
                    Lesson(id = doc.id, name = doc.getString("name") ?: "")
                }
                subjectAdapter.updateSubjects(lessons)
            }
    }

    private fun addLesson() {
        selectedDate?.let { date ->
            val lesson = hashMapOf(
                "name" to "Новый урок",
                "date" to date
            )
            db.collection("lessons")
                .add(lesson)
                .addOnSuccessListener {
                    loadLessonsForDate(date)
                }
        }
    }

    private fun deleteLesson(lessonId: String) {
        db.collection("lessons").document(lessonId)
            .delete()
            .addOnSuccessListener {
                selectedDate?.let { loadLessonsForDate(it) }
            }
    }

    private fun renameLesson(lessonId: String, newName: String) {
        db.collection("lessons").document(lessonId)
            .update("name", newName)
            .addOnSuccessListener {
                selectedDate?.let { loadLessonsForDate(it) }
            }
    }

    private fun updateCalendar() {
        calendarAdapter.updateWeek(calendar)
        updateMonthDisplay()  // Обновляем название месяца
        loadLessonsForDate(selectedDate!!)
    }

    private fun updateMonthDisplay() {
        monthTextView.text = monthFormat.format(calendar.time).replaceFirstChar { it.uppercaseChar() }
    }
}
