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

    private val currentMonthDates: List<String>
    private val calendar: Calendar = Calendar.getInstance()
    private var selectedDate: String? = null // Храним выбранную дату

    init {
        currentMonthDates = generateCurrentMonthDates()
    }

    private fun generateCurrentMonthDates(): List<String> {
        val dates = mutableListOf<String>()
        val firstDayOfMonth = calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
        val lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Определяем первый день недели текущего месяца
        calendar.set(Calendar.DAY_OF_MONTH, firstDayOfMonth)
        val startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1: Sunday, 7: Saturday
        val offset = if (startDayOfWeek == 1) 6 else startDayOfWeek - 2 // Adjust to start from Monday

        // Добавляем пустые ячейки для дней перед первым числом месяца
        for (i in 0 until offset) {
            dates.add("") // Пустая ячейка для пустых дней перед числом
        }

        // Добавляем числа месяца
        for (day in firstDayOfMonth..lastDayOfMonth) {
            dates.add(day.toString())
        }

        // Добавляем пустые ячейки для оставшихся дней после последнего числа месяца
        while (dates.size % 7 != 0) {
            dates.add("") // Пустая ячейка для дней после числа месяца
        }

        return dates
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_date, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = currentMonthDates[position]
        if (date.isNotEmpty()) {
            holder.bind(date)
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
        } else {
            holder.clear()
            holder.itemView.setBackgroundResource(R.drawable.default_day_background) // Для пустых ячеек тоже ставим стандартный фон
        }
    }

    override fun getItemCount(): Int {
        return currentMonthDates.size
    }

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateText: TextView = itemView.findViewById(R.id.dateText)

        fun bind(date: String) {
            dateText.text = date
        }

        fun clear() {
            dateText.text = ""
        }
    }
}


