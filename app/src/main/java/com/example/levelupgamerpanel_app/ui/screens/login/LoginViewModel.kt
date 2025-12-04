package com.example.levelupgamerpanel_app.ui.screens.login

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupgamerpanel_app.data.repository.ApiRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ViewModel que maneja el login conectando con el backend real
class LoginViewModel(app: Application) : AndroidViewModel(app) {

    // Repositorio para hacer las peticiones HTTP al backend
    private val repository = ApiRepository(app)

    // Estados del formulario de login
    val usuario = mutableStateOf("")
    val contraseña = mutableStateOf("")
    val error = mutableStateOf<String?>(null)
    val isLoading = mutableStateOf(false)
    val intentoLogin = mutableStateOf(false)
    val loginExitoso = mutableStateOf(false)

    // Actualizar el campo de usuario (correo)
    fun onUsuarioChange(nuevoUsuario: String) {
        usuario.value = nuevoUsuario
        error.value = null
    }

    // Actualizar el campo de contrasena
    fun onContraseñaChange(nuevaContraseña: String) {
        contraseña.value = nuevaContraseña
        error.value = null
    }

    // Validar credenciales usando la API del backend
    fun validarLogin(): Boolean {
        intentoLogin.value = true
        isLoading.value = true
        error.value = null

        // Validar que los campos no esten vacios
        if (usuario.value.isBlank()) {
            error.value = "El correo es obligatorio"
            isLoading.value = false
            return false
        }

        if (contraseña.value.isBlank()) {
            error.value = "La contraseña es obligatoria"
            isLoading.value = false
            return false
        }

        // Hacer peticion HTTP al backend para autenticar
        viewModelScope.launch {
            try {
                android.util.Log.d("LoginViewModel", "Intentando login con API: ${usuario.value}")

                val result = repository.login(usuario.value, contraseña.value)

                isLoading.value = false

                if (result.isSuccess) {
                    val usuario = repository.obtenerUsuarioActual()

                    // Bloquear acceso si el tipo de usuario es CLIENTE
                    if (usuario?.tipoUsuario == "CLIENTE") {
                        android.util.Log.d("LoginViewModel", "Login bloqueado: usuario tipo CLIENTE")
                        error.value = "Acceso denegado. Esta aplicación es solo para administradores y vendedores."
                        loginExitoso.value = false
                        repository.logout()
                    } else {
                        // Login exitoso para ADMIN o VENDEDOR
                        android.util.Log.d("LoginViewModel", "Login exitoso! Tipo: ${usuario?.tipoUsuario}")
                        loginExitoso.value = true
                        error.value = null
                    }
                } else {
                    // Manejar error de autenticacion
                    val errorMsg = result.exceptionOrNull()?.message ?: "Error al iniciar sesión"
                    android.util.Log.e("LoginViewModel", "Login fallido: $errorMsg")
                    error.value = errorMsg
                    loginExitoso.value = false
                }
            } catch (e: Exception) {
                // Manejar errores de conexion o excepciones inesperadas
                android.util.Log.e("LoginViewModel", "Exception: ${e.message}", e)
                isLoading.value = false
                error.value = "Error de conexión: ${e.message}"
                loginExitoso.value = false
            }
        }

        // Retornar false porque la validacion es asincrona (resultado llega despues)
        return false
    }
}