package com.example.attendance

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AdminMenuWindow : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var subjectAdapter: SubjectAdapter
    private lateinit var calendarAdapter: CalendarAdapter
    private var selectedDate: String? = null
    private val db = FirebaseFirestore.getInstance()
    private val calendar = Calendar.getInstance()

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

        // Инициализация календаря
        val calendarRecyclerView: RecyclerView = findViewById(R.id.admin_weekCalendar)
        calendarAdapter = CalendarAdapter(calendar) { date ->
            selectedDate = date
            loadLessonsForDate(selectedDate!!)
        }
        calendarRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        calendarRecyclerView.adapter = calendarAdapter

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
        loadLessonsForDate(selectedDate!!)
    }
}
