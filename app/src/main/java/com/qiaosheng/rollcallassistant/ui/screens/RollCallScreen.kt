package com.qiaosheng.rollcallassistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qiaosheng.rollcallassistant.R
import com.qiaosheng.rollcallassistant.data.models.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionSetupScreen(
    courses: List<String>,
    students: List<Student>,
    defaultStatus: RollCallStatus,
    leaveAppointments: List<LeaveAppointment>,
    onStart: (RollCallSession) -> Unit,
    onNavigateToLeaveAppointment: () -> Unit
) {
    var selectedCourse by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val currentDateTime = sdf.format(Date())
    val sessionDateOnly = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            stringResource(R.string.quick_start),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.AutoMirrored.Filled.LibraryBooks,
                        null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(
                        stringResource(R.string.select_course),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (courses.isEmpty()) {
                    Text(stringResource(R.string.no_courses), color = colorScheme.error)
                } else {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }) {
                        OutlinedTextField(
                            value = selectedCourse,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            placeholder = { Text(stringResource(R.string.select_course_label)) }
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }) {
                            courses.forEach { course ->
                                DropdownMenuItem(
                                    text = { Text(course) },
                                    onClick = { selectedCourse = course; expanded = false })
                            }
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AccessTime,
                        null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(
                        stringResource(R.string.select_date),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                OutlinedTextField(
                    value = currentDateTime,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.History, null, modifier = Modifier.size(20.dp)) }
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onNavigateToLeaveAppointment,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.secondaryContainer,
                    contentColor = colorScheme.onSecondaryContainer
                )
            ) {
                Icon(Icons.Default.DateRange, null)
                Spacer(Modifier.size(8.dp))
                Text(
                    stringResource(R.string.leave_appointment),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    val sessionStudents = students.map { student ->
                        val isLeave =
                            leaveAppointments.any { it.date == sessionDateOnly && it.course == selectedCourse && it.studentId == student.id }
                        if (isLeave) student.copy(status = RollCallStatus.LEAVE) else student.copy(
                            status = defaultStatus
                        )
                    }
                    onStart(RollCallSession(date = currentDateTime, course = selectedCourse, results = sessionStudents))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(20.dp),
                enabled = selectedCourse.isNotBlank() && students.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                )
            ) {
                Text(
                    stringResource(R.string.start_roll_call),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RollCallScreen(
    session: RollCallSession,
    displayStyle: StatusDisplayStyle,
    onUpdate: (RollCallSession) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(
            start = 12.dp,
            end = 12.dp,
            top = 8.dp,
            bottom = 88.dp // 增加底部边距，为悬浮按钮留出空间
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(session.results) { index, student ->
            RollCallItem(student, displayStyle) { newStatus ->
                val newResults = session.results.toMutableList()
                    .apply { this[index] = student.copy(status = newStatus) }
                onUpdate(session.copy(results = newResults))
            }
        }
    }
}

@Composable
fun RollCallItem(
    student: Student,
    displayStyle: StatusDisplayStyle,
    onStatusClick: (RollCallStatus) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant.copy(alpha = 0.2f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Student Avatar
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    student.name.take(1),
                    style = MaterialTheme.typography.labelLarge,
                    color = colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Column(Modifier.weight(1f)) {
                Text(
                    student.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        student.id,
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.outline
                    )
                    if (student.absenceCount > 0) {
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "(${student.absenceCount})",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Status Selection Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = if (displayStyle == StatusDisplayStyle.TEXT) Modifier.width(160.dp) else Modifier.wrapContentWidth()
            ) {
                StatusButton(
                    label = stringResource(R.string.present),
                    icon = Icons.Default.Check,
                    selected = student.status == RollCallStatus.PRESENT,
                    color = Color(0xFF4CAF50),
                    displayStyle = displayStyle,
                    modifier = if (displayStyle == StatusDisplayStyle.TEXT) Modifier.weight(1f) else Modifier,
                    onClick = { onStatusClick(RollCallStatus.PRESENT) }
                )
                StatusButton(
                    label = stringResource(R.string.absent),
                    icon = Icons.Default.Close,
                    selected = student.status == RollCallStatus.ABSENT,
                    color = Color(0xFFF44336),
                    displayStyle = displayStyle,
                    modifier = if (displayStyle == StatusDisplayStyle.TEXT) Modifier.weight(1f) else Modifier,
                    onClick = { onStatusClick(RollCallStatus.ABSENT) }
                )
                StatusButton(
                    label = stringResource(R.string.leave),
                    icon = Icons.Default.EventBusy,
                    selected = student.status == RollCallStatus.LEAVE,
                    color = Color(0xFFFF9800),
                    displayStyle = displayStyle,
                    modifier = if (displayStyle == StatusDisplayStyle.TEXT) Modifier.weight(1f) else Modifier,
                    onClick = { onStatusClick(RollCallStatus.LEAVE) }
                )
            }
        }
    }
}

@Composable
fun StatusButton(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    color: Color,
    displayStyle: StatusDisplayStyle,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = if (displayStyle == StatusDisplayStyle.ICON) modifier.size(36.dp) else modifier.height(36.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) color else color.copy(alpha = 0.1f),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 4.dp)) {
            if (displayStyle == StatusDisplayStyle.ICON) {
                Icon(
                    icon,
                    contentDescription = label,
                    modifier = Modifier.size(20.dp),
                    tint = if (selected) Color.White else color
                )
            } else {
                Text(
                    label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) Color.White else color,
                    maxLines = 1
                )
            }
        }
    }
}


