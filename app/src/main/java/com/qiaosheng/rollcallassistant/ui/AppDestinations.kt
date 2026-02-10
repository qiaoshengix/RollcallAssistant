package com.qiaosheng.rollcallassistant.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.qiaosheng.rollcallassistant.R

enum class AppDestinations(val resId: Int, val icon: ImageVector) {
    ROLL_CALL(R.string.roll_call, Icons.Filled.PersonSearch),
    STATISTICS(R.string.statistics, Icons.Filled.PieChart),
    SETTING(R.string.setting, Icons.Filled.Settings),
}
