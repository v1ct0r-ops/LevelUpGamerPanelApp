package com.example.level_up_movil.ui.theme.pantallas.login.dashboard.registro

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.level_up_movil.ui.theme.componentes.*
import com.google.android.gms.location.LocationServices
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager


@Composable
fun PantallaRegistro(
    navController: NavController,
    vm: RegistroViewModel = viewModel()
) {
    val ui = vm.ui.value
    val mostrarDialogoExito = rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    //  Lanzador para pedir permiso de ubicación
    val launcherPermisoUbicacion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Si el usuario acepta, obtenemos ubicación
            obtenerUbicacion(context, fusedLocationClient, vm)
        } else {
            //  Si rechaza, mostramos mensaje de error
            vm.ui.value = ui.copy(error = "Debes conceder permiso de ubicación para usar esta función.")
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 5.dp,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .heightIn(max = 550.dp)
                    .padding(horizontal = 11.dp)
                    .align(Alignment.Center)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 13.dp, vertical = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        "Crear cuenta",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    CampoTexto(
                        label = "RUN (sin puntos ni guión)",
                        valor = ui.run,
                        onChange = vm::onRun,
                        supportingText = "Ej: 19011022K",
                        isError = ui.runError != null,
                        errorText = ui.runError,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )

                    CampoTexto("Nombres", ui.nombres, vm::onNombres, modifier = Modifier.fillMaxWidth(0.95f))
                    CampoTexto("Apellidos", ui.apellidos, vm::onApellidos, modifier = Modifier.fillMaxWidth(0.95f))
                    CampoTexto(
                        label = "Correo",
                        valor = ui.correo,
                        onChange = vm::onCorreo,
                        supportingText = "Permitidos: @duoc.cl, @profesor.duoc.cl, @gmail.com",
                        isError = ui.correoError != null,
                        errorText = ui.correoError,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )
                    CampoTexto("Fecha de Nacimiento (dd-mm-aaaa)", ui.fechaNacimiento, vm::onFecha, modifier = Modifier.fillMaxWidth(0.95f))
                    SelectorSimple(
                        label = "Tipo de Usuario",
                        opciones = listOf("Administrador", "Cliente"),
                        seleccionado = ui.tipoUsuario,
                        onSelect = vm::onTipo,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )
                    CampoTexto("Región", ui.region, vm::onRegion, modifier = Modifier.fillMaxWidth(0.95f))
                    CampoTexto("Comuna", ui.comuna, vm::onComuna, modifier = Modifier.fillMaxWidth(0.95f))

                    //  BOTÓN PARA DETECTAR UBICACIÓN
                    Button(
                        onClick = {
                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED
                            ) {
                                obtenerUbicacion(context, fusedLocationClient, vm)
                            } else {
                                launcherPermisoUbicacion.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text("📍 Detectar mi ubicación")
                    }

                    CampoTexto("Dirección", ui.direccion, vm::onDireccion, modifier = Modifier.fillMaxWidth(0.95f))

                    CampoPassword("Contraseña", ui.password, vm::onPassword, isError = ui.passwordError != null, errorText = ui.passwordError, modifier = Modifier.fillMaxWidth(0.95f))
                    CampoPassword("Confirmar", ui.confirmar, vm::onConfirmar, isError = ui.confirmarError != null, errorText = ui.confirmarError, modifier = Modifier.fillMaxWidth(0.95f))

                    ui.error?.let {
                        AlertaErrorLogin(
                            mensaje = it,
                            onDismiss = { vm.ui.value = ui.copy(error = null) },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        )
                    }

                    TercerBoton(
                        texto = "Crear cuenta",
                        onClick = {
                            vm.crearCuenta(
                                onOk = {vibrarDispositivo(context)
                                    mostrarDialogoExito.value = true },
                                onError = { msg -> vm.ui.value = ui.copy(error = msg) }
                            )
                        },
                        enabled = true,
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(40.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    if (mostrarDialogoExito.value) {
                        DialogExitoRegistro(
                            onOk = {
                                mostrarDialogoExito.value = false
                                navController.navigate("login")
                            }
                        )
                    }

                    Text("¿Ya tienes cuenta?", modifier = Modifier.align(Alignment.CenterHorizontally))
                    TextButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("Ingresar")
                    }
                }
            }
        }
    }
}

//  FUNCIÓN GLOBAL DE OBTENER UBICACIÓN
fun obtenerUbicacion(context: android.content.Context, fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient, vm: RegistroViewModel) {
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let {
            vm.onRegion("Metropolitana")
            vm.onComuna("Padre hurtado")
        }
    }
}

fun vibrarDispositivo(context: android.content.Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12 o superior
            val vibratorManager = context.getSystemService(VibratorManager::class.java)
            val vibrator = vibratorManager?.defaultVibrator
            vibrator?.vibrate(
                VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            // Versiones anteriores
            val vibrator = context.getSystemService(Vibrator::class.java)
            vibrator?.vibrate(
                VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        // No hace nada si el hardware o permiso no existe
    }
}


