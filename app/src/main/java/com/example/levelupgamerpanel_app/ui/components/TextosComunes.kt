package com.example.levelupgamerpanel_app.ui.components



import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.levelupgamerpanel_app.ui.theme.*

@Composable
fun TituloLogin(modifier: Modifier=Modifier) {
    Text(
        text = "Iniciar sesión",
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}

@Composable
fun SubtituloLogin() {
    Text(
        text = "Accedé a la gestión según tu rol.",
        style = MaterialTheme.typography.bodyMedium,
        color = OnPrimary
    )
}
