package com.example.levelupgamerpanel_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.levelupgamerpanel_app.AppViewModel
import com.example.levelupgamerpanel_app.ui.navigation.Routes

@Composable
fun LoginScreen(nav: NavController, vm: AppViewModel = viewModel()){
    var correo by remember { mutableStateOf("admin@duoc.cl") }
    var pass by remember { mutableStateOf("1234") }
    var err by remember { mutableStateOf<String?>(null) }
    val haptics = LocalHapticFeedback.current

    fun validar(): Boolean {
        if (!correo.contains("@")) { err = "Correo inválido"; return false }
        if (pass.length < 4) { err = "La contraseña debe tener al menos 4 caracteres"; return false }
        return true
    }

    Scaffold { pv ->
        Column(Modifier.fillMaxSize().padding(pv).padding(16.dp)) {
            OutlinedTextField(value = correo, onValueChange = { correo = it }, label = { Text("Correo") }, singleLine = true)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = pass, onValueChange = { pass = it }, label = { Text("Contraseña") },
                singleLine = true, visualTransformation = PasswordVisualTransformation())
            if (err != null) { Spacer(Modifier.height(8.dp)); Text(text = err!!, color = MaterialTheme.colorScheme.error) }
            Spacer(Modifier.height(12.dp))
            Button(onClick = {
                if (!validar()) return@Button
                vm.login(correo, pass, onError = {
                    err = it; haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                }){
                    nav.navigate(Routes.Home){ popUpTo(Routes.Login){ inclusive = true } }
                }
            }) { Text("Ingresar") }
        }
    }
}
