package com.example.attendance

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class SubjectAdapter(
    private var subjects: List<Lesson> = emptyList(),
    private val onDelete: (String) -> Unit,
    private val onRename: (String, String) -> Unit
) : RecyclerView.Adapter<SubjectAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val subjectName: TextView = view.findViewById(R.id.subject_name)
        val deleteButton: ImageButton = view.findViewById(R.id.btn_delete_subject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subject, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val subject = subjects[position]

        holder.subjectName.text = subject.name

        // Изменение названия по двойному нажатию
        holder.subjectName.setOnClickListener { view ->
            val context = view.context
            showRenameDialog(context, subject.id, subject.name)
        }

        // Удаление урока
        holder.deleteButton.setOnClickListener {
            onDelete(subject.id)
        }
    }

    override fun getItemCount() = subjects.size

    fun updateSubjects(newSubjects: List<Lesson>) {
        subjects = newSubjects
        notifyDataSetChanged()
    }

    private fun showRenameDialog(context: Context, lessonId: String, oldName: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Изменить название урока")
        val input = android.widget.EditText(context)
        input.setText(oldName)
        builder.setView(input)
        builder.setPositiveButton("OK") { _, _ ->
            val newName = input.text.toString().trim()
            if (newName.isNotEmpty()) {
                onRename(lessonId, newName)
            } else {
                Toast.makeText(context, "Название не может быть пустым!", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Отмена", null)
        builder.show()
    }
}
