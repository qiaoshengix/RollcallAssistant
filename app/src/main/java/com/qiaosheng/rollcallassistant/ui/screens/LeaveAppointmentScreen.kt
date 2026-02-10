package com.qiaosheng.rollcallassistant.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qiaosheng.rollcallassistant.R
import com.qiaosheng.rollcallassistant.model.LeaveAppointment
import com.qiaosheng.rollcallassistant.model.Student
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveAppointmentScreen(
    leaveAppointments: List<LeaveAppointment>,
    courses: List<String>,
    students: List<Student>,
    onAddAppointment: (LeaveAppointment) -> Unit,
    onDeleteAppointment: (LeaveAppointment) -> Unit
) {
    var showAddLayer by remember { mutableStateOf(false) }

    // TODO: In the future, leave request can be managed via a backend service or SMS auto-parsing.

    Box(Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = showAddLayer,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = ""
        ) { isAdding ->
            if (isAdding) {
                AddAppointmentLayer(
                    courses = courses,
                    students = students,
                    onDismiss = { showAddLayer = false },
                    onConfirm = { appt -> onAddAppointment(appt); showAddLayer = false })
            } else {
                if (leaveAppointments.isEmpty()) {
                    Column(
                        Modifier.fillMaxSize(),
                        Arrangement.Center,
                        Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            null,
                            Modifier.size(64.dp),
                            tint = colorScheme.outline.copy(alpha = 0.3f)
                        ); Text(
                        stringResource(R.string.no_leave_appointments),
                        color = colorScheme.outline
                    )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(leaveAppointments) { appt ->
                            val studentName = students.find { it.id == appt.studentId }?.name ?: appt.studentId
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                            ) {
                                ListItem(
                                    headlineContent = { Text("$studentName - ${appt.course}") },
                                    supportingContent = { Text("${appt.date} | ${stringResource(R.string.reason_label)}: ${appt.reason}") },
                                    leadingContent = { Icon(Icons.Default.Person, null) },
                                    trailingContent = {
                                        IconButton(onClick = { onDeleteAppointment(appt) }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                null,
                                                tint = Color.Red
                                            )
                                        }
                                    },
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                )
                            }
                        }
                    }
                }
            }
        }
        if (!showAddLayer) {
            ExtendedFloatingActionButton(
                onClick = { showAddLayer = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text(stringResource(R.string.add_leave_appointment)) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppointmentLayer(
    courses: List<String>,
    students: List<Student>,
    onDismiss: () -> Unit,
    onConfirm: (LeaveAppointment) -> Unit
) {
    var selectedCourse by remember { mutableStateOf(courses.firstOrNull() ?: "") }
    var selectedStudentId by remember { mutableStateOf(students.firstOrNull()?.id ?: "") }
    var reason by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var studentSearchQuery by remember { mutableStateOf("") }
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(Date(it))
    } ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val filteredStudents = students.filter {
        it.name.contains(
            studentSearchQuery,
            ignoreCase = true
        ) || it.id.contains(studentSearchQuery)
    }
    BackHandler(onBack = onDismiss)
    Surface(Modifier.fillMaxSize(), color = colorScheme.surface) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.add_leave_appointment_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            null
                        )
                    }
                },
                actions = {
                    TextButton(onClick = {
                        if (selectedCourse.isNotBlank() && selectedStudentId.isNotBlank()) {
                            onConfirm(
                                LeaveAppointment(
                                    date = selectedDate,
                                    course = selectedCourse,
                                    studentId = selectedStudentId,
                                    reason = reason
                                )
                            )
                        }
                    }, enabled = selectedCourse.isNotBlank() && selectedStudentId.isNotBlank()) {
                        Text(
                            stringResource(R.string.save),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                })
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = {},
                        label = { Text(stringResource(R.string.leave_date)) },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = {
                                showDatePicker = true
                            }) { Icon(Icons.Default.DateRange, null) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                item {
                    var courseExp by remember { mutableStateOf(false) }; ExposedDropdownMenuBox(
                    expanded = courseExp,
                    onExpandedChange = { courseExp = it }) {
                    OutlinedTextField(
                        value = selectedCourse,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.select_course)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = courseExp) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ); ExposedDropdownMenu(
                    expanded = courseExp,
                    onDismissRequest = { courseExp = false }) {
                    courses.forEach { c ->
                        DropdownMenuItem(
                            text = { Text(c) },
                            onClick = { selectedCourse = c; courseExp = false })
                    }
                }
                }
                }
                item {
                    Text(
                        stringResource(R.string.select_student),
                        style = MaterialTheme.typography.titleSmall,
                        color = colorScheme.primary
                    ); OutlinedTextField(
                    value = studentSearchQuery,
                    onValueChange = { studentSearchQuery = it },
                    placeholder = { Text(stringResource(R.string.search_student_hint)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                }
                items(filteredStudents) { student ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedStudentId = student.id },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedStudentId == student.id) colorScheme.primaryContainer else colorScheme.surfaceVariant.copy(
                                alpha = 0.3f
                            )
                        ),
                        border = if (selectedStudentId == student.id) androidx.compose.foundation.BorderStroke(
                            2.dp,
                            colorScheme.primary
                        ) else null
                    ) {
                        ListItem(
                            headlineContent = { Text(student.name, fontWeight = FontWeight.Bold) },
                            supportingContent = { Text(student.id) },
                            trailingContent = {
                                if (selectedStudentId == student.id) Icon(
                                    Icons.Default.Check,
                                    null,
                                    tint = colorScheme.primary
                                )
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text(stringResource(R.string.leave_reason)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3
                    )
                }
            }
        }
    }
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                }) { Text(stringResource(R.string.confirm_button)) }
            }) { DatePicker(state = datePickerState) }
    }
}
