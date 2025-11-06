package com.example.levelupgamerpanel_app.ui.components


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DialogExitoRegistro(
    mensaje: String = "Tu cuenta fue creada correctamente.",
    onOk: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onOk, // si toca fuera, hacemos lo mismo que OK
        icon = {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = { Text("¡Cuenta creada de forma exitosa!") },
        text = { Text(mensaje) },
        confirmButton = {
            Button(onClick = onOk) {
                Text("OK")
            }
        }
    )
}