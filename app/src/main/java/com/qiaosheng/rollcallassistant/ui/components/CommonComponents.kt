package com.qiaosheng.rollcallassistant.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.qiaosheng.rollcallassistant.model.StatusDisplayStyle

// TODO: In the future, these components might be moved into a more comprehensive design system module.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    color: Color,
    style: StatusDisplayStyle,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            if (style == StatusDisplayStyle.TEXT) Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            ) else Icon(icon, null, modifier = Modifier.size(16.dp))
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = color.copy(0.2f),
            selectedLabelColor = color
        ),
        shape = RoundedCornerShape(10.dp)
    )
}

@Composable
fun ManagementItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(title, fontWeight = FontWeight.SemiBold) },
        leadingContent = { Icon(icon, null, tint = colorScheme.primary) },
        trailingContent = { Icon(Icons.Default.ChevronRight, null) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
fun DetailStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = colorScheme.primary
        )
        Text(label, style = MaterialTheme.typography.labelSmall, color = colorScheme.outline)
    }
}

@Composable
fun AboutItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(title) },
        leadingContent = { Icon(icon, null, tint = colorScheme.primary) },
        trailingContent = { Icon(Icons.Default.ChevronRight, null) })
}
