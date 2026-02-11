package com.qiaosheng.rollcallassistant.data.local

import com.qiaosheng.rollcallassistant.data.models.*

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RollCallDao {
    // Students
    @Query("SELECT * FROM students")
    fun getAllStudents(): Flow<List<Student>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    // Courses
    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<Course>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course)

    @Update
    suspend fun updateCourse(course: Course)

    @Delete
    suspend fun deleteCourse(course: Course)

    // Sessions
    @Query("SELECT * FROM roll_call_sessions ORDER BY id DESC")
    fun getAllSessions(): Flow<List<RollCallSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: RollCallSession)

    @Delete
    suspend fun deleteSession(session: RollCallSession)

    // Leave Appointments
    @Query("SELECT * FROM leave_appointments")
    fun getAllLeaveAppointments(): Flow<List<LeaveAppointment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaveAppointment(appointment: LeaveAppointment)

    @Delete
    suspend fun deleteLeaveAppointment(appointment: LeaveAppointment)

    // Reminders
    @Query("SELECT * FROM reminder_settings")
    fun getAllReminders(): Flow<List<ReminderSetting>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderSetting)

    @Update
    suspend fun updateReminder(reminder: ReminderSetting)

    @Delete
    suspend fun deleteReminder(reminder: ReminderSetting)
}
