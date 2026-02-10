package com.qiaosheng.rollcallassistant.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qiaosheng.rollcallassistant.R
import com.qiaosheng.rollcallassistant.model.ReminderSetting
import com.qiaosheng.rollcallassistant.model.Course
import com.qiaosheng.rollcallassistant.model.RollCallStatus
import com.qiaosheng.rollcallassistant.model.StatusDisplayStyle
import com.qiaosheng.rollcallassistant.model.Student
import com.qiaosheng.rollcallassistant.ui.components.AboutItem
import com.qiaosheng.rollcallassistant.ui.components.ManagementItem
import com.qiaosheng.rollcallassistant.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingMainScreen(
    style: StatusDisplayStyle, onStyleChange: (StatusDisplayStyle) -> Unit,
    def: RollCallStatus, onDefChange: (RollCallStatus) -> Unit,
    currentTheme: ThemeMode, onThemeChange: (ThemeMode) -> Unit,
    onNavigateToCourse: () -> Unit, onNavigateToStudent: () -> Unit,
    onNavigateToTheme: () -> Unit, onNavigateToAbout: () -> Unit, onNavigateToReminder: () -> Unit
) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                stringResource(R.string.ui_preference),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )
            Spacer(Modifier.height(12.dp))
            OutlinedCard(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.button_style_title)) },
                        supportingContent = {
                            Column(Modifier.padding(top = 12.dp)) {
                                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                                    SegmentedButton(
                                        selected = style == StatusDisplayStyle.TEXT,
                                        onClick = { onStyleChange(StatusDisplayStyle.TEXT) },
                                        shape = SegmentedButtonDefaults.itemShape(0, 2)
                                    ) { Text(stringResource(R.string.style_text)) }
                                    SegmentedButton(
                                        selected = style == StatusDisplayStyle.ICON,
                                        onClick = { onStyleChange(StatusDisplayStyle.ICON) },
                                        shape = SegmentedButtonDefaults.itemShape(1, 2)
                                    ) { Text(stringResource(R.string.style_icon)) }
                                }
                            }
                        }
                    )
                    HorizontalDivider(Modifier.padding(horizontal = 20.dp), color = colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.default_status_title)) },
                        supportingContent = {
                            Column(Modifier.padding(top = 12.dp)) {
                                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                                    val statuses = RollCallStatus.entries
                                    statuses.forEachIndexed { index, s ->
                                        SegmentedButton(
                                            selected = def == s,
                                            onClick = { onDefChange(s) },
                                            shape = SegmentedButtonDefaults.itemShape(index, statuses.size)
                                        ) {
                                            Text(stringResource(s.resId), fontSize = 12.sp, maxLines = 1)
                                        }
                                    }
                                }
                            }
                        })
                }
            }
        }
        item {
            Text(
                stringResource(R.string.automation_reminders),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )
            Spacer(Modifier.height(12.dp))
            OutlinedCard(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                ManagementItem(
                    stringResource(R.string.reminder_settings),
                    Icons.Default.Alarm,
                    onNavigateToReminder
                )
            }
        }
        item {
            Text(
                stringResource(R.string.appearance_theme),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )
            Spacer(Modifier.height(12.dp))
            OutlinedCard(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(Modifier.padding(vertical = 8.dp)) {
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.theme_mode_title)) },
                        leadingContent = {
                            Icon(
                                Icons.Default.Palette,
                                null,
                                tint = colorScheme.primary
                            )
                        },
                        supportingContent = {
                            Column(Modifier.padding(top = 12.dp)) {
                                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                                    val modes = listOf(
                                        ThemeMode.SYSTEM,
                                        ThemeMode.LIGHT,
                                        ThemeMode.DARK,
                                        ThemeMode.DYNAMIC
                                    )
                                    val labels = listOf(
                                        stringResource(R.string.theme_system_short),
                                        stringResource(R.string.theme_light_short),
                                        stringResource(R.string.theme_dark_short),
                                        stringResource(R.string.theme_dynamic_short)
                                    )
                                    val isPreset = currentTheme in listOf(
                                        ThemeMode.DEEP_SEA,
                                        ThemeMode.FOREST,
                                        ThemeMode.SUNSET
                                    )
                                    modes.forEachIndexed { i, mode ->
                                        SegmentedButton(
                                            selected = currentTheme == mode,
                                            onClick = { onThemeChange(mode) },
                                            shape = SegmentedButtonDefaults.itemShape(i, 5)
                                        ) {
                                            Text(labels[i], fontSize = 11.sp, maxLines = 1)
                                        }
                                    }
                                    SegmentedButton(
                                        selected = isPreset,
                                        onClick = onNavigateToTheme,
                                        shape = SegmentedButtonDefaults.itemShape(4, 5)
                                    ) {
                                        Text(
                                            stringResource(R.string.theme_preset_short),
                                            fontSize = 11.sp,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
        item {
            Text(
                stringResource(R.string.others),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )
            Spacer(Modifier.height(12.dp))
            OutlinedCard(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column {
                    ManagementItem(
                        stringResource(R.string.course_management),
                        Icons.Default.LibraryBooks,
                        onNavigateToCourse
                    )
                    HorizontalDivider(
                        Modifier.padding(horizontal = 20.dp),
                        color = colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                    ManagementItem(
                        stringResource(R.string.class_management),
                        Icons.Default.Class,
                        onNavigateToStudent
                    )
                    HorizontalDivider(
                        Modifier.padding(horizontal = 20.dp),
                        color = colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                    ManagementItem(
                        stringResource(R.string.about_app),
                        Icons.Default.Info,
                        onNavigateToAbout
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderManagementScreen(
    reminders: List<ReminderSetting>,
    courses: List<String>,
    onAddReminder: (ReminderSetting) -> Unit,
    onUpdateReminder: (ReminderSetting) -> Unit,
    onDeleteReminder: (ReminderSetting) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxSize()) {
        if (reminders.isEmpty()) {
            Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Alarm,
                    null,
                    Modifier.size(64.dp),
                    tint = colorScheme.outline.copy(alpha = 0.3f)
                )
                Text(stringResource(R.string.no_reminders), color = colorScheme.outline)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reminders) { reminder ->
                    Card(shape = RoundedCornerShape(12.dp)) {
                        ListItem(
                            headlineContent = { Text("${reminder.courseName} - ${reminder.time}") },
                            supportingContent = {
                                val days = listOf(
                                    stringResource(R.string.day_mon_full),
                                    stringResource(R.string.day_tue_full),
                                    stringResource(R.string.day_wed_full),
                                    stringResource(R.string.day_thu_full),
                                    stringResource(R.string.day_fri_full),
                                    stringResource(R.string.day_sat_full),
                                    stringResource(R.string.day_sun_full)
                                )
                                // Convert comma-separated string to list of ints for mapping
                                val repeatDaysList = reminder.repeatDays.split(",")
                                    .filter { it.isNotBlank() }
                                    .map { it.toInt() }
                                
                                Text(
                                    stringResource(R.string.repeat_label) + ": ${
                                        repeatDaysList.joinToString(
                                            ", "
                                        ) { days[it - 1] }
                                    }"
                                )
                            },
                            trailingContent = {
                                IconButton(onClick = { onDeleteReminder(reminder) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        null,
                                        tint = Color.Red
                                    )
                                }
                            },
                            leadingContent = {
                                Checkbox(
                                    checked = reminder.isEnabled,
                                    onCheckedChange = { onUpdateReminder(reminder.copy(isEnabled = it)) })
                            }
                        )
                    }
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            icon = { Icon(Icons.Default.Add, null) },
            text = { Text(stringResource(R.string.add_reminder)) })
    }

    if (showAddDialog) {
        var selectedCourse by remember { mutableStateOf(courses.firstOrNull() ?: "") }
        val timePickerState = rememberTimePickerState()
        val repeatDays = remember { mutableStateListOf<Int>() }

        // TODO: Notification functionality will be implemented here (e.g., scheduling with AlarmManager).

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(stringResource(R.string.add_reminder)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    var exp by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = exp, onExpandedChange = { exp = it }) {
                        OutlinedTextField(
                            value = selectedCourse,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.select_course_label)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = exp) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = exp,
                            onDismissRequest = { exp = false }) {
                            courses.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c) },
                                    onClick = { selectedCourse = c; exp = false })
                            }
                        }
                    }
                    Text(
                        stringResource(R.string.select_reminder_time),
                        style = MaterialTheme.typography.labelMedium
                    )
                    TimePicker(state = timePickerState, modifier = Modifier.fillMaxWidth())
                    Text(
                        stringResource(R.string.repeat_cycle),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val days = listOf(
                            stringResource(R.string.day_mon),
                            stringResource(R.string.day_tue),
                            stringResource(R.string.day_wed),
                            stringResource(R.string.day_thu),
                            stringResource(R.string.day_fri),
                            stringResource(R.string.day_sat),
                            stringResource(R.string.day_sun)
                        )
                        days.forEachIndexed { i, day ->
                            val dayNum = i + 1
                            val isSelected = dayNum in repeatDays
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (dayNum in repeatDays) repeatDays.remove(dayNum)
                                    else repeatDays.add(dayNum)
                                },
                                label = {
                                    Text(
                                        day,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = colorScheme.primary,
                                    selectedLabelColor = colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val timeStr =
                        String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                    onAddReminder(
                        ReminderSetting(
                            courseName = selectedCourse,
                            time = timeStr,
                            repeatDays = repeatDays.joinToString(",")
                        )
                    )
                    showAddDialog = false
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(
                        stringResource(
                            R.string.cancel
                        )
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelectionScreen(currentTheme: ThemeMode, onThemeChange: (ThemeMode) -> Unit) {
    val themes = listOf(
        ThemeMode.SYSTEM to stringResource(R.string.theme_system),
        ThemeMode.LIGHT to stringResource(R.string.theme_light),
        ThemeMode.DARK to stringResource(R.string.theme_dark),
        ThemeMode.DYNAMIC to stringResource(R.string.theme_dynamic),
        ThemeMode.DEEP_SEA to stringResource(R.string.theme_deep_sea),
        ThemeMode.FOREST to stringResource(R.string.theme_forest),
        ThemeMode.SUNSET to stringResource(R.string.theme_sunset)
    )

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(themes) { (mode, label) ->
            val isSelected = currentTheme == mode
            val themeColors = getThemePreviewColors(mode)

            Card(
                onClick = { onThemeChange(mode) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) colorScheme.primaryContainer else colorScheme.surfaceVariant
                ),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(
                    3.dp,
                    colorScheme.primary
                ) else null,
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isSelected) 6.dp else 2.dp
                )
            ) {
                Column(Modifier.padding(20.dp)) {
                    // Header with theme name and check icon
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            label,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) colorScheme.primary else colorScheme.onSurface
                        )
                        if (isSelected) {
                            Surface(
                                shape = CircleShape,
                                color = colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Check,
                                        null,
                                        tint = colorScheme.onPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Color preview palette
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Primary color
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = themeColors.first
                        ) {}
                        // Secondary color
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = themeColors.second
                        ) {}
                        // Tertiary color
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = themeColors.third
                        ) {}
                    }
                }
            }
        }
    }
}

@Composable
fun getThemePreviewColors(mode: ThemeMode): Triple<Color, Color, Color> {
    return when (mode) {
        ThemeMode.SYSTEM, ThemeMode.LIGHT -> Triple(
            Color(0xFF6750A4),
            Color(0xFF625B71),
            Color(0xFF7D5260)
        )

        ThemeMode.DARK -> Triple(
            Color(0xFFD0BCFF),
            Color(0xFFCCC2DC),
            Color(0xFFEFB8C8)
        )

        ThemeMode.DYNAMIC -> Triple(
            colorScheme.primary,
            colorScheme.secondary,
            colorScheme.tertiary
        )

        ThemeMode.DEEP_SEA -> Triple(
            Color(0xFF0077BE),
            Color(0xFF004D7A),
            Color(0xFF00A8E8)
        )

        ThemeMode.FOREST -> Triple(
            Color(0xFF2E7D32),
            Color(0xFF1B5E20),
            Color(0xFF4CAF50)
        )

        ThemeMode.SUNSET -> Triple(
            Color(0xFFFF6F00),
            Color(0xFFE65100),
            Color(0xFFFF9800)
        )
    }
}

@Composable
fun AboutScreen() {
    val uriHandler = LocalUriHandler.current
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = colorScheme.primaryContainer,
            modifier = Modifier.size(100.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.PersonSearch,
                    null,
                    modifier = Modifier.size(56.dp),
                    tint = colorScheme.primary
                )
            }
        }
        Text(
            stringResource(R.string.app_title_full),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            stringResource(R.string.version_info),
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.outline
        )
        Text(
            stringResource(R.string.about_desc),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )
        Card(shape = RoundedCornerShape(24.dp)) {
            Column {
                AboutItem(
                    stringResource(R.string.open_source_repo),
                    Icons.Default.Code
                ) { uriHandler.openUri("https://github.com/qiaoshengix/RollCallAssistant") }; HorizontalDivider(
                Modifier.padding(
                    horizontal = 16.dp
                )
            ); AboutItem(
                stringResource(R.string.sponsor_developer),
                Icons.Default.Coffee
            ) { uriHandler.openUri("https://afdian.com/a/qiaoshengix") }; HorizontalDivider(
                Modifier.padding(
                    horizontal = 16.dp
                )
            ); AboutItem(
                stringResource(R.string.feedback_issue),
                Icons.Default.ErrorOutline
            ) { uriHandler.openUri("https://github.com/qiaoshengix/RollCallAssistant/issues") }; HorizontalDivider(
                Modifier.padding(
                    horizontal = 16.dp
                )
            ); AboutItem(stringResource(R.string.mit_license), Icons.Default.History) { uriHandler.openUri("https://www.gnu.org/licenses/gpl-3.0.html") }
            }
        }
        Spacer(Modifier.weight(1f))
        Text(
            stringResource(R.string.made_by),
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.outline
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseManagementScreen(
    courses: List<Course>,
    onAddCourse: (String) -> Unit,
    onUpdateCourse: (Course) -> Unit,
    onDeleteCourse: (Course) -> Unit,
    onImportCourses: (Uri) -> Unit,
    onExportCourses: (Uri) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCourse by remember { mutableStateOf<Course?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let { onImportCourses(it) } }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri -> uri?.let { onExportCourses(it) } }

    Box(Modifier.fillMaxSize()) {
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { importLauncher.launch(arrayOf("application/json", "text/*")) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.FileUpload, null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.import_label))
                }
                Button(
                    onClick = { exportLauncher.launch("courses_backup.json") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.FileDownload, null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.export_label))
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.search_course_hint)) },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(16.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(courses) { course ->
                    if (course.name.contains(searchQuery, true)) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            onClick = { editingCourse = course }) {
                            ListItem(
                                headlineContent = { Text(course.name, fontWeight = FontWeight.Bold) },
                                trailingContent = {
                                    IconButton(onClick = { onDeleteCourse(course) }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            null,
                                            tint = Color.Red
                                        )
                                    }
                                },
                                leadingContent = {
                                    Icon(
                                        Icons.Default.LibraryBooks,
                                        null,
                                        tint = colorScheme.primary
                                    )
                                })
                        }
                    }
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            icon = { Icon(Icons.Default.Add, null) },
            text = { Text(stringResource(R.string.add_course)) })
    }
    if (showAddDialog || editingCourse != null) {
        var text by remember { mutableStateOf(editingCourse?.name ?: "") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false; editingCourse = null },
            title = {
                Text(
                    if (editingCourse != null) stringResource(R.string.modify_course) else stringResource(
                        R.string.add_course
                    )
                )
            },
            text = {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (text.isNotBlank()) {
                        if (editingCourse != null) {
                            onUpdateCourse(editingCourse!!.copy(name = text))
                        } else {
                            onAddCourse(text)
                        }
                        showAddDialog = false
                        editingCourse = null
                    } else {
                        showAddDialog = false
                        editingCourse = null
                    }
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false; editingCourse = null
                }) { Text(stringResource(R.string.cancel)) }
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentManagementScreen(
    students: List<Student>,
    onAddStudent: (Student) -> Unit,
    onDeleteStudent: (Student) -> Unit,
    onImportStudents: (Uri) -> Unit,
    onExportStudents: (Uri) -> Unit,
    onImportFromExcel: (Uri) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingStudent by remember { mutableStateOf<Student?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let { onImportStudents(it) } }

    val excelLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let { onImportFromExcel(it) } }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri -> uri?.let { onExportStudents(it) } }

    Box(Modifier.fillMaxSize()) {
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { importLauncher.launch(arrayOf("application/json", "text/*")) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.FileUpload, null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.import_label))
                }
                Button(
                    onClick = { excelLauncher.launch(arrayOf("application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.TableChart, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Excel")
                }
                Button(
                    onClick = { exportLauncher.launch("students_backup.json") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.FileDownload, null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.export_label))
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.search_student_hint)) },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(16.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(students) { student ->
                    if (student.name.contains(searchQuery) || student.id.contains(
                            searchQuery
                        )
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            onClick = { editingStudent = student }) {
                            ListItem(
                                headlineContent = {
                                    Text(
                                        student.name,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                supportingContent = { Text(student.id) },
                                leadingContent = {
                                    Surface(
                                        shape = CircleShape,
                                        color = colorScheme.primaryContainer,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                student.name.take(
                                                    1
                                                )
                                            )
                                        }
                                    }
                                },
                                trailingContent = {
                                    IconButton(onClick = { onDeleteStudent(student) }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            null,
                                            tint = Color.Red
                                        )
                                    }
                                })
                        }
                    }
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            icon = { Icon(Icons.Default.Add, null) },
            text = { Text(stringResource(R.string.add_student)) })
    }
    if (showAddDialog || editingStudent != null) {
        var name by remember { mutableStateOf(editingStudent?.name ?: "") }
        var id by remember { mutableStateOf(editingStudent?.id ?: "") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false; editingStudent = null },
            title = {
                Text(
                    if (editingStudent != null) stringResource(R.string.edit_student) else stringResource(
                        R.string.add_student
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(stringResource(R.string.student_name)) }); OutlinedTextField(
                    value = id,
                    onValueChange = { id = it },
                    label = { Text(stringResource(R.string.student_id)) })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank()) {
                        val s = Student(id, name)
                        if (editingStudent != null) {
                            onDeleteStudent(editingStudent!!)
                            onAddStudent(s)
                        } else {
                            onAddStudent(s)
                        }
                        showAddDialog = false
                        editingStudent = null
                    }
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false; editingStudent = null
                }) { Text(stringResource(R.string.cancel)) }
            })
    }
}
