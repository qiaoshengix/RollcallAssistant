package com.qiaosheng.rollcallassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.qiaosheng.rollcallassistant.ui.screens.RollCallAssistantApp
import com.qiaosheng.rollcallassistant.ui.theme.RollCallAssistantTheme
import com.qiaosheng.rollcallassistant.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var themeMode by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
            RollCallAssistantTheme(themeMode = themeMode) {
                RollCallAssistantApp(
                    currentTheme = themeMode,
                    onThemeChange = { themeMode = it }
                )
            }
        }
    }
}
