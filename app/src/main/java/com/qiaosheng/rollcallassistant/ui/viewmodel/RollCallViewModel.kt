package com.qiaosheng.rollcallassistant.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.qiaosheng.rollcallassistant.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class RollCallViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).rollCallDao()
    private val gson = Gson()

    val students: StateFlow<List<Student>> = dao.getAllStudents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val courses: StateFlow<List<Course>> = dao.getAllCourses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sessions: StateFlow<List<RollCallSession>> = dao.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leaveAppointments: StateFlow<List<LeaveAppointment>> = dao.getAllLeaveAppointments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reminders: StateFlow<List<ReminderSetting>> = dao.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Student Management
    fun addStudent(student: Student) {
        viewModelScope.launch { dao.insertStudent(student) }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch { dao.deleteStudent(student) }
    }

    fun importStudents(uri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val json = reader.readText()
                    val type = object : TypeToken<List<Student>>() {}.type
                    val importedStudents: List<Student> = gson.fromJson(json, type)
                    importedStudents.forEach { dao.insertStudent(it.copy(id = it.id.trim())) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun importStudentsFromExcel(uri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val workbook = WorkbookFactory.create(inputStream)
                    val sheet = workbook.getSheetAt(0)
                    val importedStudents = mutableListOf<Student>()
                    
                    for (row in sheet) {
                        if (row.rowNum == 0) continue // Skip header if exists
                        
                        val idCell = row.getCell(0)
                        val nameCell = row.getCell(1)
                        
                        if (idCell != null && nameCell != null) {
                            val id = when (idCell.cellType) {
                                org.apache.poi.ss.usermodel.CellType.NUMERIC -> idCell.numericCellValue.toLong().toString()
                                else -> idCell.toString().trim()
                            }
                            val name = nameCell.toString().trim()
                            
                            if (id.isNotEmpty() && name.isNotEmpty()) {
                                importedStudents.add(Student(id, name))
                            }
                        }
                    }
                    
                    importedStudents.forEach { dao.insertStudent(it) }
                    workbook.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun exportStudents(uri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    val writer = OutputStreamWriter(outputStream)
                    val studentsList = students.value
                    writer.write(gson.toJson(studentsList))
                    writer.flush()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Course Management
    fun addCourse(name: String) {
        viewModelScope.launch { dao.insertCourse(Course(name = name)) }
    }

    fun updateCourse(course: Course) {
        viewModelScope.launch { dao.updateCourse(course) }
    }

    fun deleteCourse(course: Course) {
        viewModelScope.launch { dao.deleteCourse(course) }
    }

    fun importCourses(uri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val json = reader.readText()
                    val type = object : TypeToken<List<Course>>() {}.type
                    val importedCourses: List<Course> = gson.fromJson(json, type)
                    importedCourses.forEach { dao.insertCourse(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun exportCourses(uri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    val writer = OutputStreamWriter(outputStream)
                    val coursesList = courses.value
                    writer.write(gson.toJson(coursesList))
                    writer.flush()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Session Management
    fun saveSession(session: RollCallSession) {
        viewModelScope.launch { dao.insertSession(session) }
    }

    fun deleteSession(session: RollCallSession) {
        viewModelScope.launch { dao.deleteSession(session) }
    }

    // Leave Appointments
    fun addLeaveAppointment(appointment: LeaveAppointment) {
        viewModelScope.launch { dao.insertLeaveAppointment(appointment) }
    }

    fun deleteLeaveAppointment(appointment: LeaveAppointment) {
        viewModelScope.launch { dao.deleteLeaveAppointment(appointment) }
    }

    // Reminders
    fun addReminder(reminder: ReminderSetting) {
        viewModelScope.launch { dao.insertReminder(reminder) }
    }

    fun updateReminder(reminder: ReminderSetting) {
        viewModelScope.launch { dao.updateReminder(reminder) }
    }

    fun deleteReminder(reminder: ReminderSetting) {
        viewModelScope.launch { dao.deleteReminder(reminder) }
    }
}
