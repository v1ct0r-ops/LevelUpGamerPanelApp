package com.example.levelupgamerpanel_app.ui.screens.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel(){
        val usuario = mutableStateOf("")
        val contraseña = mutableStateOf("")
        val error = mutableStateOf<String?>(null)

        val intentoLogin = mutableStateOf(false)



    fun onUsuarioChange(nuevoUsuario: String) {
        usuario.value = nuevoUsuario
        error.value = null              // me permite limpiar el error al teclear
    }

    fun onContraseñaChange(nuevaContraseña: String) {
        contraseña.value = nuevaContraseña
        error.value = null
    }


    fun validarLogin(): Boolean {
        intentoLogin.value = true   // para verificar si se intento iniciar sesion

        return if (usuario.value == "admin" && contraseña.value == "1234"){
            true
        } else {
            error.value = "Usuario o contraseña incorrectas reintentelo."
            false
        }
    }
}