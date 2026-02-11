package com.qiaosheng.rollcallassistant.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qiaosheng.rollcallassistant.R
import com.qiaosheng.rollcallassistant.data.models.*
import com.qiaosheng.rollcallassistant.ui.components.DetailStatItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    sessions: List<RollCallSession>,
    showExportDialog: Boolean,
    onDeleteSession: (RollCallSession) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedSession by remember { mutableStateOf<RollCallSession?>(null) }

    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sessions) { session ->
                val presentCount = session.results.count { it.status == RollCallStatus.PRESENT }
                val total = session.results.size
                val percent = if (total > 0) (presentCount.toFloat() / total * 100).toInt() else 0
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedSession = session },
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    ListItem(
                        headlineContent = {
                            Text(
                                session.course,
                                fontWeight = FontWeight.ExtraBold
                            )
                        },
                        supportingContent = { Text(session.date) },
                        trailingContent = {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "$percent%",
                                    color = if (percent > 80) Color(0xFF4CAF50) else Color.Red,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp
                                )
                                Text(
                                    "$presentCount/$total",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        },
                        leadingContent = { Icon(Icons.Default.History, null) }
                    )
                }
            }
        }

        selectedSession?.let { session ->
            SessionDetailSheet(
                session = session,
                onDelete = { onDeleteSession(it); selectedSession = null },
                onDismiss = { selectedSession = null })
        }
        if (showExportDialog) {
            ExportPreviewDialog(sessions, onDismiss)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailSheet(session: RollCallSession, onDelete: (RollCallSession) -> Unit, onDismiss: () -> Unit) {
    var showPreview by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { onDelete(session) }) {
                    Text(stringResource(R.string.delete), color = Color.Red)
                }
                Button(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
                    Text(stringResource(R.string.confirm))
                }
            }
        },
        title = {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    Text(session.course, fontWeight = FontWeight.ExtraBold)
                    Text(
                        session.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.outline
                    )
                }
                IconButton(onClick = { showPreview = true }) {
                    Icon(
                        Icons.Default.FileDownload,
                        null,
                        tint = colorScheme.primary
                    )
                }
            }
        },
        text = {
            Column(Modifier.height(450.dp)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DetailStatItem(
                        stringResource(R.string.total_count),
                        session.results.size.toString()
                    )
                    val present = session.results.count { it.status == RollCallStatus.PRESENT }
                    DetailStatItem(
                        stringResource(R.string.attendance_rate),
                        "${if (session.results.isNotEmpty()) (present * 100 / session.results.size) else 0}%"
                    )
                }
                val outlineColor = MaterialTheme.colorScheme.outline
                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val grouped = session.results.groupBy { it.status }
                    RollCallStatus.entries.forEach { status ->
                        val list = grouped[status] ?: emptyList()
                        if (list.isNotEmpty()) {
                            val statusColor = when (status) {
                                RollCallStatus.PRESENT -> Color(0xFF4CAF50)
                                RollCallStatus.ABSENT -> Color(0xFFF44336)
                                RollCallStatus.LEAVE -> Color(0xFFFF9800)
                                RollCallStatus.NONE -> outlineColor
                            }
                            item {
                                Surface(
                                    color = statusColor.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        stringResource(status.resId),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = statusColor,
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp,
                                            vertical = 2.dp
                                        ),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            items(list) { s ->
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = colorScheme.surfaceVariant.copy(
                                            alpha = 0.3f
                                        )
                                    ), shape = RoundedCornerShape(8.dp)
                                ) {
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                s.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        },
                                        supportingContent = { Text(s.id, fontSize = 12.sp) },
                                        leadingContent = {
                                            Icon(
                                                Icons.Default.Person,
                                                null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        },
                                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        shape = RoundedCornerShape(28.dp)
    )
    if (showPreview) {
        ExportPreviewDialog(listOf(session)) { showPreview = false }
    }
}

@Composable
fun ExportPreviewDialog(sessions: List<RollCallSession>, onDismiss: () -> Unit) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val copiedMessage = stringResource(R.string.copied_to_clipboard)
    val previewText = remember(sessions) {
        val sb = StringBuilder()
        sessions.forEach { s ->
            sb.append("【点名记录】\n")
            sb.append("课程：${s.course}\n")
            sb.append("时间：${s.date}\n")
            sb.append("总人数：${s.results.size}\n")
            val grouped = s.results.groupBy { it.status }
            RollCallStatus.entries.filter { it != RollCallStatus.NONE }.forEach { status ->
                val list = grouped[status] ?: emptyList()
                if (list.isNotEmpty()) {
                    sb.append("● ${status.name} (${list.size}人)：")
                    sb.append(list.joinToString(", ") { it.name })
                    sb.append("\n")
                }
            }
            sb.append("--------------------\n")
        }
        sb.toString()
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.export_preview), fontWeight = FontWeight.Bold) },
        text = {
            Surface(
                modifier = Modifier
                    .height(300.dp)
                    .fillMaxWidth(),
                color = colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            ) {
                LazyColumn(contentPadding = PaddingValues(12.dp)) {
                    item {
                        Text(
                            previewText,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                clipboard.setText(AnnotatedString(previewText))
                Toast.makeText(context, copiedMessage, Toast.LENGTH_SHORT).show()
                onDismiss()
            }) {
                Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.size(8.dp))
                Text(stringResource(R.string.copy_all))
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}
