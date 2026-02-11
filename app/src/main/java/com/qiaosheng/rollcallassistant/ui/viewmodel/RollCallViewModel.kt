package com.qiaosheng.rollcallassistant.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qiaosheng.rollcallassistant.data.local.AppDatabase
import com.qiaosheng.rollcallassistant.data.models.*
import com.qiaosheng.rollcallassistant.data.repository.RollCallRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RollCallViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RollCallRepository(AppDatabase.getDatabase(application).rollCallDao())

    val students: StateFlow<List<Student>> = repository.getAllStudents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val courses: StateFlow<List<Course>> = repository.getAllCourses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sessions: StateFlow<List<RollCallSession>> = repository.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leaveAppointments: StateFlow<List<LeaveAppointment>> = repository.getAllLeaveAppointments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reminders: StateFlow<List<ReminderSetting>> = repository.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Student Management
    fun addStudent(student: Student) {
        viewModelScope.launch { repository.insertStudent(student) }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch { repository.deleteStudent(student) }
    }

    fun importStudents(uri: Uri, context: Context) {
        viewModelScope.launch { repository.importStudents(uri, context) }
    }

    fun importStudentsFromExcel(uri: Uri, context: Context) {
        viewModelScope.launch { repository.importStudentsFromExcel(uri, context) }
    }

    fun exportStudents(uri: Uri, context: Context) {
        viewModelScope.launch { repository.exportStudents(uri, context, students.value) }
    }

    // Course Management
    fun addCourse(name: String) {
        viewModelScope.launch { repository.insertCourse(Course(name = name)) }
    }

    fun updateCourse(course: Course) {
        viewModelScope.launch { repository.updateCourse(course) }
    }

    fun deleteCourse(course: Course) {
        viewModelScope.launch { repository.deleteCourse(course) }
    }

    fun importCourses(uri: Uri, context: Context) {
        viewModelScope.launch { repository.importCourses(uri, context) }
    }

    fun exportCourses(uri: Uri, context: Context) {
        viewModelScope.launch { repository.exportCourses(uri, context, courses.value) }
    }

    // Session Management
    fun saveSession(session: RollCallSession) {
        viewModelScope.launch { repository.insertSession(session) }
    }

    fun deleteSession(session: RollCallSession) {
        viewModelScope.launch { repository.deleteSession(session) }
    }

    // Leave Appointments
    fun addLeaveAppointment(appointment: LeaveAppointment) {
        viewModelScope.launch { repository.insertLeaveAppointment(appointment) }
    }

    fun deleteLeaveAppointment(appointment: LeaveAppointment) {
        viewModelScope.launch { repository.deleteLeaveAppointment(appointment) }
    }

    // Reminders
    fun addReminder(reminder: ReminderSetting) {
        viewModelScope.launch { repository.insertReminder(reminder) }
    }

    fun updateReminder(reminder: ReminderSetting) {
        viewModelScope.launch { repository.updateReminder(reminder) }
    }

    fun deleteReminder(reminder: ReminderSetting) {
        viewModelScope.launch { repository.deleteReminder(reminder) }
    }
}
