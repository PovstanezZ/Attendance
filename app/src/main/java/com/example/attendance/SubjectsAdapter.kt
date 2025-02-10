package com.example.attendance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SubjectsAdapter : RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder>() {
    private val subjects = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subject, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        holder.bind(subjects[position])
    }

    override fun getItemCount(): Int {
        return subjects.size
    }

    fun updateSubjects(newSubjects: List<String>) {
        subjects.clear()
        subjects.addAll(newSubjects)
        notifyDataSetChanged()
    }

    fun addSubject(subject: String) {
        subjects.add(subject)
        notifyItemInserted(subjects.size - 1)
    }

    inner class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val subjectName: TextView = itemView.findViewById(R.id.subjectName)

        fun bind(subject: String) {
            subjectName.text = subject
        }
    }
}
