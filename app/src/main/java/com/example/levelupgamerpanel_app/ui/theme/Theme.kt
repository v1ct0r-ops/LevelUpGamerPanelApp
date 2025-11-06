package com.example.levelupgamerpanel_app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.levelupgamerpanel_app.ui.theme.AppTypography
import com.example.levelupgamerpanel_app.ui.theme.Azul
import com.example.levelupgamerpanel_app.ui.theme.Borde
import com.example.levelupgamerpanel_app.ui.theme.Fondo
import com.example.levelupgamerpanel_app.ui.theme.Panel
import com.example.levelupgamerpanel_app.ui.theme.Texto
import com.example.levelupgamerpanel_app.ui.theme.Verde

private val DarkColors = darkColorScheme(
    primary = Azul,
    secondary = Verde,
    background = Fondo,
    surface = Panel,
    onSurface = Texto,
    onPrimary = Color.White,
    outline = Borde
)

@Composable
fun LevelUpTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = AppTypography,
        content = content
    )
}