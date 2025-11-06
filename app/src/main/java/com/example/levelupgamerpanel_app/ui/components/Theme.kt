package com.example.levelupgamerpanel_app.ui.components

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import com.example.levelupgamerpanel_app.ui.theme.AppTypography
import com.example.levelupgamerpanel_app.ui.theme.BgDark
import com.example.levelupgamerpanel_app.ui.theme.ErrorRed
import com.example.levelupgamerpanel_app.ui.theme.OnPrimary
import com.example.levelupgamerpanel_app.ui.theme.PrimaryBlue
import com.example.levelupgamerpanel_app.ui.theme.SurfaceDark
import com.example.levelupgamerpanel_app.ui.theme.TextPrimary


private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimary,
    background = BgDark,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    // adicionales utiles
    secondary = Color(0xFF2CC4FF),
    onSecondary = OnPrimary,
    error = ErrorRed,
    outline = Color(0xFF223046)
)

@Composable
fun AdminLevelUpTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        shapes = Shapes(),
        content = content
    )
}