package com.example.levelupgamerpanel_app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.levelupgamerpanel_app.R

// Familias
val Orbitron = FontFamily(
    Font(R.font.orbitron_regular, weight = FontWeight.Normal),
    Font(R.font.orbitron_bold,    weight = FontWeight.Bold)
)

val Roboto = FontFamily(
    Font(R.font.roboto_regular, weight = FontWeight.Normal),
    Font(R.font.roboto_bold,    weight = FontWeight.Bold)
)

// Base de Material3 y override de estilos
private val Base = Typography()

val AppTypography = Base.copy(
    // Títulos grandes con Orbitron
    displayLarge  = Base.displayLarge.copy (fontFamily = Orbitron, fontWeight = FontWeight.Bold),
    headlineLarge = Base.headlineLarge.copy(fontFamily = Orbitron, fontWeight = FontWeight.SemiBold),
    titleLarge    = Base.titleLarge.copy   (fontFamily = Orbitron, fontWeight = FontWeight.SemiBold),


    // Texto de párrafo con Roboto
    bodyLarge   = Base.bodyLarge.copy  (fontFamily = Roboto),
    bodyMedium  = Base.bodyMedium.copy (fontFamily = Orbitron, fontWeight = FontWeight.Normal),
    bodySmall   = Base.bodySmall.copy  (fontFamily = Roboto),

    // Labels / chips / botones con Roboto
    labelLarge  = Base.labelLarge.copy (fontFamily = Roboto, fontWeight = FontWeight.Medium),
    labelMedium = Base.labelMedium.copy(fontFamily = Roboto),
    labelSmall  = Base.labelSmall.copy (fontFamily = Roboto)
)
