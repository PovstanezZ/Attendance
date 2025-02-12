package com.example.attendance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(private var calendar: Calendar, private val onDateSelected: (String) -> Unit) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var currentWeekDates: List<String> = getWeekDates()
    private val daysOfWeek = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
    private var selectedDate: String? = getTodayDate()

    private fun getTodayDate(): String {
        val today = Calendar.getInstance()
        return today.get(Calendar.DAY_OF_MONTH).toString()
    }

    private fun getWeekDates(): List<String> {
        val weekDates = mutableListOf<String>()
        val tempCalendar = calendar.clone() as Calendar
        tempCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        for (i in 0 until 7) {
            weekDates.add(tempCalendar.get(Calendar.DAY_OF_MONTH).toString())
            tempCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return weekDates
    }

    fun updateWeek(newCalendar: Calendar) {
        calendar = newCalendar
        currentWeekDates = getWeekDates()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_date, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = currentWeekDates[position]
        holder.bind(date, daysOfWeek[position])
        val background = if (date == selectedDate) {
            R.drawable.selected_day_background
        } else {
            R.drawable.default_day_background
        }
        holder.itemView.setBackgroundResource(background)
        holder.itemView.setOnClickListener {
            selectedDate = date
            onDateSelected(date)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return currentWeekDates.size
    }

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateText: TextView = itemView.findViewById(R.id.dateText)

        fun bind(date: String, dayOfWeek: String) {
            dateText.text = "$dayOfWeek\n$date"
        }
    }
}
