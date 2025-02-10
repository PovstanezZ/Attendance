package com.example.attendance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(private val onDateSelected: (String) -> Unit) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private val currentWeekDates: List<String>
    private val daysOfWeek: List<String>
    private var selectedDate: String? = null // Храним выбранную дату

    init {
        currentWeekDates = getCurrentWeekDates()
        daysOfWeek = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
        selectedDate = currentWeekDates.find { it == getTodayDate() } // Устанавливаем текущую дату как выбранную
    }

    // Получаем сегодняшнюю дату в виде строки
    private fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_MONTH).toString()
    }

    // Получаем список дней текущей недели
    private fun getCurrentWeekDates(): List<String> {
        val calendar = Calendar.getInstance()
        val startOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // Получаем текущий день недели

        // Устанавливаем календарь на начало недели (понедельник)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startOfWeekDate = calendar.get(Calendar.DAY_OF_MONTH)

        val weekDates = mutableListOf<String>()
        for (i in 0 until 7) {
            weekDates.add((startOfWeekDate + i).toString())
        }
        return weekDates
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_date, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = currentWeekDates[position]
        holder.bind(date, daysOfWeek[position])

        // Устанавливаем фон в зависимости от того, выбран ли день
        val background = if (date == selectedDate) {
            R.drawable.selected_day_background
        } else {
            R.drawable.default_day_background
        }
        holder.itemView.setBackgroundResource(background)

        holder.itemView.setOnClickListener {
            selectedDate = date // Обновляем выбранную дату
            onDateSelected(date)
            notifyDataSetChanged() // Обновляем адаптер для перерисовки
        }
    }

    override fun getItemCount(): Int {
        return currentWeekDates.size
    }

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateText: TextView = itemView.findViewById(R.id.dateText)

        fun bind(date: String, dayOfWeek: String) {
            // Формируем текст с днем недели и числом
            dateText.text = "$dayOfWeek\n$date"
        }
    }
}



