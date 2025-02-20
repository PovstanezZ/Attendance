package com.example.attendance

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendance.R
import com.google.firebase.firestore.FirebaseFirestore

class JournalWindow : AppCompatActivity() {
    private lateinit var groupSpinner: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var addGroupButton: Button
    private lateinit var addStudentButton: Button
    private lateinit var deleteGroupButton: Button
    private lateinit var deleteStudentButton: Button
    private val db = FirebaseFirestore.getInstance()
    private val groupList = mutableListOf<String>()
    private val studentList = mutableListOf<Student>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal)

        groupSpinner = findViewById(R.id.groupSpinner)
        recyclerView = findViewById(R.id.journalResView)
        addGroupButton = findViewById(R.id.btnAddGroup)
        addStudentButton = findViewById(R.id.btnAddStudent)
        deleteGroupButton = findViewById(R.id.btnDeleteGroup)
        deleteStudentButton = findViewById(R.id.btnDeleteStudent)

        recyclerView.layoutManager = LinearLayoutManager(this)

        loadGroups()

        addGroupButton.setOnClickListener { showAddGroupDialog() }
        addStudentButton.setOnClickListener { showAddStudentDialog() }

        // Обработчик для удаления группы
        deleteGroupButton.setOnClickListener {
            val selectedGroup = groupSpinner.selectedItem.toString()
            if (selectedGroup.isNotEmpty()) {
                db.collection("groups").document(selectedGroup).delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Группа удалена", Toast.LENGTH_SHORT).show()
                        loadGroups()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Ошибка удаления: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Выберите группу для удаления", Toast.LENGTH_SHORT).show()
            }
        }

        // Обработчик для удаления студента
        deleteStudentButton.setOnClickListener {
            val selectedStudent = getSelectedStudent() // Выбираем студента
            if (selectedStudent != null) {
                val selectedGroup = groupSpinner.selectedItem.toString()
                db.collection("groups").document(selectedGroup)
                    .collection("students").document(selectedStudent.id)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Студент удалён", Toast.LENGTH_SHORT).show()
                        loadStudents(selectedGroup)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Ошибка удаления: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Выберите студента для удаления", Toast.LENGTH_SHORT).show()
            }
        }

        groupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                val selectedGroup = groupList[position]
                loadStudents(selectedGroup)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadGroups() {
        db.collection("groups").get().addOnSuccessListener { documents ->
            groupList.clear()
            for (document in documents) {
                groupList.add(document.id)
            }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, groupList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            groupSpinner.adapter = adapter
        }
    }

    private fun showAddGroupDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_group, null)
        val groupNameInput = dialogView.findViewById<EditText>(R.id.editGroupName)

        AlertDialog.Builder(this)
            .setTitle("Добавить группу")
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                val groupName = groupNameInput.text.toString()
                if (groupName.isNotEmpty()) {
                    db.collection("groups").document(groupName).set(mapOf("name" to groupName))
                        .addOnSuccessListener { loadGroups() }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showAddStudentDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_student, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.editStudentName)
        val emailInput = dialogView.findViewById<EditText>(R.id.editStudentEmail)

        AlertDialog.Builder(this)
            .setTitle("Добавить ученика")
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                val name = nameInput.text.toString()
                val email = emailInput.text.toString()
                val group = groupSpinner.selectedItem.toString()

                if (name.isNotEmpty() && email.isNotEmpty()) {
                    val student = Student(name, email, group)
                    db.collection("groups").document(group)
                        .collection("students").add(student)
                        .addOnSuccessListener { loadStudents(group) }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun loadStudents(group: String) {
        db.collection("groups").document(group)
            .collection("students").get().addOnSuccessListener { documents ->
                studentList.clear()
                for (document in documents) {
                    val student = document.toObject(Student::class.java)
                    student.id = document.id
                    studentList.add(student)
                }
                recyclerView.adapter = StudentAdapter(studentList) { student ->
                    showEditStudentDialog(student, group)
                }
            }
    }

    private fun getSelectedStudent(): Student? {
        // Предполагается, что у тебя есть выбор студента, например, в RecyclerView.
        // Здесь мы можем просто вернуть первый элемент для примера:
        return studentList.firstOrNull()
    }

    private fun showEditStudentDialog(student: Student, group: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_student, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.editStudentName)
        val emailInput = dialogView.findViewById<EditText>(R.id.editStudentEmail)

        nameInput.setText(student.name)
        emailInput.setText(student.email)

        AlertDialog.Builder(this)
            .setTitle("Редактировать ученика")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val newName = nameInput.text.toString()
                val newEmail = emailInput.text.toString()

                db.collection("groups").document(group)
                    .collection("students").document(student.id)
                    .set(Student(newName, newEmail, group))
                    .addOnSuccessListener { loadStudents(group) }
            }
            .setNegativeButton("Удалить") { _, _ ->
                db.collection("groups").document(group)
                    .collection("students").document(student.id)
                    .delete()
                    .addOnSuccessListener { loadStudents(group) }
            }
            .setNeutralButton("Отмена", null)
            .show()
    }
}



