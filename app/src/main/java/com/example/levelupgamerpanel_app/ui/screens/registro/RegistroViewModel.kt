package com.example.levelupgamerpanel_app.ui.screens.registro



import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlin.compareTo
import kotlin.run

data class RegistroUiState(
    val run: String = "",
    val nombres: String = "",
    val apellidos: String = "",
    val correo: String = "",
    val fechaNacimiento: String = "",
    val tipoUsuario: String = "Administrador",
    val region: String = "",
    val comuna: String = "",
    val direccion: String = "",
    val password: String = "",
    val confirmar: String = "",
    val referido: String = "",
    val error: String? = null,
    val runError: String? = null,
    val nombresError: String? = null,
    val apellidosError: String? = null,
    val correoError: String? = null,
    val fechaError: String? = null,
    val regionError: String? = null,
    val comunaError: String? = null,
    val direccionError: String? = null,
    val passwordError: String? = null,
    val confirmarError: String? = null,

    )

class RegistroViewModel : ViewModel() {
    val ui = mutableStateOf(RegistroUiState())

    private fun actualizar(transform: (RegistroUiState) -> RegistroUiState) {
        ui.value = transform(ui.value).copy(error = null)  // limpia error al editar
    }

    fun onRun(v: String)          = actualizar { it.copy(run = v.uppercase()) }
    fun onNombres(v: String)      = actualizar { it.copy(nombres = v) }
    fun onApellidos(v: String)    = actualizar { it.copy(apellidos = v) }
    fun onCorreo(v: String)       = actualizar { it.copy(correo = v.lowercase()) }
    fun onFecha(v: String)        = actualizar { it.copy(fechaNacimiento = v) }
    fun onTipo(v: String)         = actualizar { it.copy(tipoUsuario = v) }
    fun onRegion(v: String)       = actualizar { it.copy(region = v) }
    fun onComuna(v: String)       = actualizar { it.copy(comuna = v) }
    fun onDireccion(v: String)    = actualizar { it.copy(direccion = v) }
    fun onPassword(v: String)     = actualizar { it.copy(password = v) }
    fun onConfirmar(v: String)    = actualizar { it.copy(confirmar = v) }
    fun onReferido(v: String)     = actualizar { it.copy(referido = v) }

    // Validaciones MUY básicas (ajústalas a tu backend)
    private fun correoValido(c: String) =
        c.endsWith("@duoc.cl") || c.endsWith("@profesor.duoc.cl") || c.endsWith("@gmail.com")

    private fun runValido(r: String) =
        r.isNotBlank() && r.matches(Regex("""\d{7,8}[0-9K]""", RegexOption.IGNORE_CASE))

    fun puedeCrear(): Boolean = with(ui.value) {
        runValido(run) &&
                nombres.isNotBlank() &&
                apellidos.isNotBlank() &&
                correoValido(correo) &&
                fechaNacimiento.isNotBlank() &&
                region.isNotBlank() &&
                comuna.isNotBlank() &&
                password.length >= 6 &&
                password == confirmar
    }

    fun validarCampos(): RegistroUiState {
        val s = ui.value
        return s.copy(
            runError = if (s.run.isBlank()) "El RUN es obligatorio" else null,
            nombresError = if (s.nombres.isBlank()) "Ingresa tus nombres" else null,
            apellidosError = if (s.apellidos.isBlank()) "Ingresa tus apellidos" else null,
            correoError = if (!s.correo.contains("@")) "Correo inválido" else null,
            passwordError = if (s.password.length < 6) "Mínimo 6 caracteres" else null,
            confirmarError = if (s.password != s.confirmar) "No coincide" else null
        )
    }

    fun crearCuenta(
        onOk: () -> Unit,
        onError: (String) -> Unit
    ) {
        val errores = validarCampos() //  llamamos a las validaciones campo a campo
        ui.value = errores

        // Si hay algún campo con error, mostramos mensaje general
        if (
            errores.runError != null ||
            errores.nombresError != null ||
            errores.apellidosError != null ||
            errores.correoError != null ||
            errores.passwordError != null ||
            errores.confirmarError != null
        ) {
            onError("Hay campos incorrectos o incompletos.") //  mensaje general
            return
        }


        onOk()
    }




}




