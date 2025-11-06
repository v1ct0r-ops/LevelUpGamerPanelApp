package com.example.levelupgamerpanel_app.ui.components



import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.levelupgamerpanel_app.ui.theme.*
import com.example.levelupgamerpanel_app.ui.theme.SurfaceDark
import com.example.levelupgamerpanel_app.ui.theme.TextPrimary

@Composable
fun CampoUsuario(
    valor: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        label = { Text("Usuario") },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SurfaceDark,
            unfocusedContainerColor = SurfaceDark,
            focusedIndicatorColor = PrimaryBlue,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = PrimaryBlue,
            cursorColor = PrimaryBlue,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun CampoContrasena(
    valor: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        label = { Text("Contraseña") },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SurfaceDark,
            unfocusedContainerColor = SurfaceDark,
            focusedIndicatorColor = PrimaryBlue,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = PrimaryBlue,
            cursorColor = PrimaryBlue,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
        ),
        modifier = modifier.fillMaxWidth()
    )
}



