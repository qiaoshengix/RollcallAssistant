package com.qiaosheng.rollcallassistant.ui.screens

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qiaosheng.rollcallassistant.R
import com.qiaosheng.rollcallassistant.data.models.*
import com.qiaosheng.rollcallassistant.ui.navigation.*
import com.qiaosheng.rollcallassistant.ui.theme.ThemeMode
import com.qiaosheng.rollcallassistant.ui.viewmodel.RollCallViewModel
import kotlinx.coroutines.CancellationException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RollCallAssistantApp(
    currentTheme: ThemeMode = ThemeMode.SYSTEM,
    onThemeChange: (ThemeMode) -> Unit = {},
    viewModel: RollCallViewModel = viewModel()
) {
    val context = LocalContext.current
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.ROLL_CALL) }
    var displayStyle by rememberSaveable { mutableStateOf(StatusDisplayStyle.TEXT) }
    var defaultStatus by rememberSaveable { mutableStateOf(RollCallStatus.NONE) }
    var rollCallSubPage by rememberSaveable { mutableStateOf(RollCallSubPage.SETUP) }

    // States from ViewModel
    val sessions by viewModel.sessions.collectAsState()
    val courses by viewModel.courses.collectAsState()
    val students by viewModel.students.collectAsState()
    val leaveAppointments by viewModel.leaveAppointments.collectAsState()
    val reminderSettings by viewModel.reminders.collectAsState()
    
    var activeSession by remember { mutableStateOf<RollCallSession?>(null) }
    var settingSubPage by remember { mutableStateOf(SettingSubPage.MAIN) }
    var showExportDialog by remember { mutableStateOf(false) }

    // --- Predictive Back Handling ---
    var backProgress by remember { mutableStateOf(0f) }
    if (settingSubPage != SettingSubPage.MAIN || (currentDestination == AppDestinations.ROLL_CALL && rollCallSubPage != RollCallSubPage.SETUP)) {
        PredictiveBackHandler { progress ->
            try {
                progress.collect { backEvent ->
                    backProgress = backEvent.progress
                }
                if (currentDestination == AppDestinations.ROLL_CALL) rollCallSubPage =
                    RollCallSubPage.SETUP
                else settingSubPage = SettingSubPage.MAIN
                backProgress = 0f
            } catch (e: CancellationException) {
                backProgress = 0f
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxSize().padding(end = 16.dp)
                    ) {
                        val title = when {
                            currentDestination == AppDestinations.ROLL_CALL && rollCallSubPage == RollCallSubPage.LEAVE_APPOINTMENT -> stringResource(
                                R.string.leave_appointment
                            )

                            currentDestination == AppDestinations.ROLL_CALL && activeSession != null -> activeSession?.course
                                ?: ""

                            currentDestination == AppDestinations.SETTING && settingSubPage == SettingSubPage.COURSE -> stringResource(
                                R.string.course_management
                            )

                            currentDestination == AppDestinations.SETTING && settingSubPage == SettingSubPage.STUDENT -> stringResource(
                                R.string.class_management
                            )

                            currentDestination == AppDestinations.SETTING && settingSubPage == SettingSubPage.THEME -> stringResource(
                                R.string.theme_selection_title
                            )

                            currentDestination == AppDestinations.SETTING && settingSubPage == SettingSubPage.ABOUT -> stringResource(
                                R.string.about_app
                            )

                            currentDestination == AppDestinations.SETTING && settingSubPage == SettingSubPage.REMINDER -> stringResource(
                                R.string.reminder_settings
                            )

                            else -> stringResource(currentDestination.resId)
                        }
                        Text(text = title, fontWeight = FontWeight.Bold)
                        if (currentDestination == AppDestinations.ROLL_CALL && activeSession != null) {
                            Text(
                                text = activeSession?.date ?: "",
                                style = MaterialTheme.typography.titleMedium,
                                color = colorScheme.outline
                            )
                        }
                    }
                },
                navigationIcon = {
                    if ((currentDestination == AppDestinations.ROLL_CALL && rollCallSubPage != RollCallSubPage.SETUP) ||
                        (currentDestination == AppDestinations.SETTING && settingSubPage != SettingSubPage.MAIN)
                    ) {
                        IconButton(onClick = {
                            if (currentDestination == AppDestinations.ROLL_CALL) rollCallSubPage =
                                RollCallSubPage.SETUP
                            else settingSubPage = SettingSubPage.MAIN
                        }) {
                            Icon(Icons.Default.ArrowBack, null)
                        }
                    }
                },
                actions = {
                    if (currentDestination == AppDestinations.STATISTICS && sessions.isNotEmpty()) {
                        IconButton(onClick = { showExportDialog = true }) {
                            Icon(Icons.Default.FileDownload, null)
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                AppDestinations.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = currentDestination == destination,
                        onClick = {
                            currentDestination = destination
                            settingSubPage = SettingSubPage.MAIN
                            rollCallSubPage = RollCallSubPage.SETUP
                        },
                        icon = { Icon(destination.icon, null) },
                        label = { Text(stringResource(destination.resId)) }
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentDestination == AppDestinations.ROLL_CALL && activeSession != null) {
                ExtendedFloatingActionButton(
                    onClick = {
                        viewModel.saveSession(activeSession!!)
                        activeSession = null
                    },
                    icon = { Icon(Icons.Default.Done, null) },
                    text = { Text(stringResource(R.string.finish_roll_call)) },
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .graphicsLayer {
                    val scale = 1f - (backProgress * 0.05f)
                    scaleX = scale
                    scaleY = scale
                }
        ) {
            when (currentDestination) {
                AppDestinations.ROLL_CALL -> {
                    AnimatedContent(
                        targetState = rollCallSubPage,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = ""
                    ) { subPage ->
                        when (subPage) {
                            RollCallSubPage.SETUP -> {
                                if (activeSession == null) {
                                    SessionSetupScreen(
                                        courses = courses.map { it.name },
                                        students = students,
                                        defaultStatus = defaultStatus,
                                        leaveAppointments = leaveAppointments,
                                        onStart = { activeSession = it },
                                        onNavigateToLeaveAppointment = {
                                            rollCallSubPage = RollCallSubPage.LEAVE_APPOINTMENT
                                        }
                                    )
                                } else {
                                    RollCallScreen(activeSession!!, displayStyle) {
                                        activeSession = it
                                    }
                                }
                            }

                            RollCallSubPage.LEAVE_APPOINTMENT -> LeaveAppointmentScreen(
                                leaveAppointments = leaveAppointments,
                                courses = courses.map { it.name },
                                students = students,
                                onAddAppointment = { viewModel.addLeaveAppointment(it) },
                                onDeleteAppointment = { viewModel.deleteLeaveAppointment(it) }
                            )
                        }
                    }
                }

                AppDestinations.STATISTICS -> StatisticsScreen(
                    sessions = sessions,
                    showExportDialog = showExportDialog,
                    onDeleteSession = { viewModel.deleteSession(it) }
                ) { showExportDialog = false }

                AppDestinations.SETTING -> {
                    AnimatedContent(
                        targetState = settingSubPage,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = ""
                    ) { page ->
                        when (page) {
                            SettingSubPage.MAIN -> SettingMainScreen(
                                style = displayStyle, onStyleChange = { displayStyle = it },
                                def = defaultStatus, onDefChange = { defaultStatus = it },
                                currentTheme = currentTheme, onThemeChange = onThemeChange,
                                onNavigateToCourse = { settingSubPage = SettingSubPage.COURSE },
                                onNavigateToStudent = { settingSubPage = SettingSubPage.STUDENT },
                                onNavigateToTheme = { settingSubPage = SettingSubPage.THEME },
                                onNavigateToAbout = { settingSubPage = SettingSubPage.ABOUT },
                                onNavigateToReminder = { settingSubPage = SettingSubPage.REMINDER }
                            )

                            SettingSubPage.COURSE -> CourseManagementScreen(
                                courses = courses,
                                onAddCourse = { viewModel.addCourse(it) },
                                onUpdateCourse = { viewModel.updateCourse(it) },
                                onDeleteCourse = { viewModel.deleteCourse(it) },
                                onImportCourses = { viewModel.importCourses(it, context) },
                                onExportCourses = { viewModel.exportCourses(it, context) }
                            )
                            SettingSubPage.STUDENT -> StudentManagementScreen(
                                students = students,
                                onAddStudent = { viewModel.addStudent(it) },
                                onDeleteStudent = { viewModel.deleteStudent(it) },
                                onImportStudents = { viewModel.importStudents(it, context) },
                                onExportStudents = { viewModel.exportStudents(it, context) },
                                onImportFromExcel = { viewModel.importStudentsFromExcel(it, context) }
                            )
                            SettingSubPage.THEME -> ThemeSelectionScreen(
                                currentTheme,
                                onThemeChange
                            )

                            SettingSubPage.ABOUT -> AboutScreen()
                            SettingSubPage.REMINDER -> ReminderManagementScreen(
                                reminders = reminderSettings,
                                courses = courses.map { it.name },
                                onAddReminder = { viewModel.addReminder(it) },
                                onUpdateReminder = { viewModel.updateReminder(it) },
                                onDeleteReminder = { viewModel.deleteReminder(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}
