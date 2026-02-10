package com.qiaosheng.rollcallassistant.model

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.qiaosheng.rollcallassistant.R
import java.util.UUID

enum class StatusDisplayStyle { TEXT, ICON }

enum class Gender(val resId: Int) {
    MALE(R.string.gender_male),
    FEMALE(R.string.gender_female),
    OTHER(R.string.gender_other)
}

enum class RollCallStatus(val resId: Int) {
    NONE(R.string.none),
    PRESENT(R.string.present),
    ABSENT(R.string.absent),
    LEAVE(R.string.leave)
}

@Entity(tableName = "students")
data class Student(
    @PrimaryKey val id: String,
    val name: String,
    val gender: Gender = Gender.MALE,
    val absenceCount: Int = 0,
    @Ignore var status: RollCallStatus = RollCallStatus.NONE
) {
    // Secondary constructor for Room
    constructor(id: String, name: String, gender: Gender, absenceCount: Int) : this(id, name, gender, absenceCount, RollCallStatus.NONE)
}

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

@Entity(tableName = "roll_call_sessions")
data class RollCallSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val course: String,
    val results: List<Student>
)

@Entity(tableName = "leave_appointments")
data class LeaveAppointment(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val date: String, // yyyy-MM-dd
    val course: String,
    val studentId: String,
    val reason: String
)

@Entity(tableName = "reminder_settings")
data class ReminderSetting(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val courseName: String,
    val time: String, // HH:mm
    val repeatDays: String, // Comma separated: 1,2,3
    var isEnabled: Boolean = true
)

enum class SettingSubPage { MAIN, COURSE, STUDENT, THEME, ABOUT, REMINDER }

enum class RollCallSubPage { SETUP, LEAVE_APPOINTMENT }

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromGender(value: Gender) = value.name

    @TypeConverter
    fun toGender(value: String) = Gender.valueOf(value)

    @TypeConverter
    fun fromRollCallStatus(value: RollCallStatus) = value.name

    @TypeConverter
    fun toRollCallStatus(value: String) = RollCallStatus.valueOf(value)

    @TypeConverter
    fun fromStudentList(value: List<Student>?): String? = gson.toJson(value)

    @TypeConverter
    fun toStudentList(value: String?): List<Student>? {
        val type = object : TypeToken<List<Student>>() {}.type
        return gson.fromJson(value, type)
    }
}
