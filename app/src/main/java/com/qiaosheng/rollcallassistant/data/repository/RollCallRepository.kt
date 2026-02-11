package com.qiaosheng.rollcallassistant.data.repository

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.qiaosheng.rollcallassistant.data.local.RollCallDao
import com.qiaosheng.rollcallassistant.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class RollCallRepository(private val dao: RollCallDao) {
    private val gson = Gson()

    fun getAllStudents(): Flow<List<Student>> = dao.getAllStudents()
    fun getAllCourses(): Flow<List<Course>> = dao.getAllCourses()
    fun getAllSessions(): Flow<List<RollCallSession>> = dao.getAllSessions()
    fun getAllLeaveAppointments(): Flow<List<LeaveAppointment>> = dao.getAllLeaveAppointments()
    fun getAllReminders(): Flow<List<ReminderSetting>> = dao.getAllReminders()

    suspend fun insertStudent(student: Student) = dao.insertStudent(student)
    suspend fun deleteStudent(student: Student) = dao.deleteStudent(student)
    
    suspend fun insertCourse(course: Course) = dao.insertCourse(course)
    suspend fun updateCourse(course: Course) = dao.updateCourse(course)
    suspend fun deleteCourse(course: Course) = dao.deleteCourse(course)
    
    suspend fun insertSession(session: RollCallSession) = dao.insertSession(session)
    suspend fun deleteSession(session: RollCallSession) = dao.deleteSession(session)
    
    suspend fun insertLeaveAppointment(appointment: LeaveAppointment) = dao.insertLeaveAppointment(appointment)
    suspend fun deleteLeaveAppointment(appointment: LeaveAppointment) = dao.deleteLeaveAppointment(appointment)
    
    suspend fun insertReminder(reminder: ReminderSetting) = dao.insertReminder(reminder)
    suspend fun updateReminder(reminder: ReminderSetting) = dao.updateReminder(reminder)
    suspend fun deleteReminder(reminder: ReminderSetting) = dao.deleteReminder(reminder)

    suspend fun importStudents(uri: Uri, context: Context) = withContext(Dispatchers.IO) {
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

    suspend fun importStudentsFromExcel(uri: Uri, context: Context) = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val workbook = WorkbookFactory.create(inputStream)
                val sheet = workbook.getSheetAt(0)
                val importedStudents = mutableListOf<Student>()
                
                for (row in sheet) {
                    if (row.rowNum == 0) continue
                    
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

    suspend fun exportStudents(uri: Uri, context: Context, students: List<Student>) = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                val writer = OutputStreamWriter(outputStream)
                writer.write(gson.toJson(students))
                writer.flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun importCourses(uri: Uri, context: Context) = withContext(Dispatchers.IO) {
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

    suspend fun exportCourses(uri: Uri, context: Context, courses: List<Course>) = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                val writer = OutputStreamWriter(outputStream)
                writer.write(gson.toJson(courses))
                writer.flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
