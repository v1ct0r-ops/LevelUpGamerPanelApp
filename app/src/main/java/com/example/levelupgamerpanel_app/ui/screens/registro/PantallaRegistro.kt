package com.example.levelupgamerpanel_app.ui.screens.registro

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
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
import com.example.levelupgamerpanel_app.ui.components.AlertaErrorLogin
import com.example.levelupgamerpanel_app.ui.components.CampoPassword
import com.example.levelupgamerpanel_app.ui.components.CampoTexto
import com.example.levelupgamerpanel_app.ui.components.DialogExitoRegistro
import com.example.levelupgamerpanel_app.ui.components.SelectorSimple
import com.example.levelupgamerpanel_app.ui.components.TercerBoton
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PantallaRegistro(
    navController: NavController,
    vm: RegistroViewModel = viewModel()
) {
    val ui = vm.ui.value
    val mostrarDialogoExito = rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Lanzador para pedir permiso de ubicación
    val launcherPermisoUbicacion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            obtenerUbicacion(context, fusedLocationClient, vm)
        } else {
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

                    CampoTexto(
                        label = "Nombres",
                        valor = ui.nombres,
                        onChange = vm::onNombres,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )

                    CampoTexto(
                        label = "Apellidos",
                        valor = ui.apellidos,
                        onChange = vm::onApellidos,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )

                    CampoTexto(
                        label = "Correo",
                        valor = ui.correo,
                        onChange = vm::onCorreo,
                        supportingText = "Permitidos: @duoc.cl, @profesor.duoc.cl, @gmail.com",
                        isError = ui.correoError != null,
                        errorText = ui.correoError,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )

                    CampoTexto(
                        label = "Fecha de Nacimiento (dd-mm-aaaa)",
                        valor = ui.fechaNacimiento,
                        onChange = vm::onFecha,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )

                    SelectorSimple(
                        label = "Tipo de Usuario",
                        opciones = listOf("Administrador", "Cliente"),
                        seleccionado = ui.tipoUsuario,
                        onSelect = vm::onTipo,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )

                    CampoTexto(
                        label = "Región",
                        valor = ui.region,
                        onChange = vm::onRegion,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )

                    CampoTexto(
                        label = "Comuna",
                        valor = ui.comuna,
                        onChange = vm::onComuna,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )

                    // Botón para detectar ubicación
                    Button(
                        onClick = {
                            if (ActivityCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
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

                    CampoTexto(
                        label = "Dirección",
                        valor = ui.direccion,
                        onChange = vm::onDireccion,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )

                    CampoPassword(
                        label = "Contraseña",
                        valor = ui.password,
                        onChange = vm::onPassword,
                        isError = ui.passwordError != null,
                        errorText = ui.passwordError,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )

                    CampoPassword(
                        label = "Confirmar",
                        valor = ui.confirmar,
                        onChange = vm::onConfirmar,
                        isError = ui.confirmarError != null,
                        errorText = ui.confirmarError,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )

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
                                onOk = {
                                    vibrarDispositivo(context)
                                    mostrarDialogoExito.value = true
                                },
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
                    TextButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Ingresar")
                    }
                }
            }
        }
    }
}

@RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
fun obtenerUbicacion(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    vm: RegistroViewModel
) {
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let {
            // Aquí puedes mapear reverse geocoding si quieres, por ahora dejamos valores de ejemplo
            vm.onRegion("Metropolitana")
            vm.onComuna("Padre hurtado")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@RequiresPermission(Manifest.permission.VIBRATE)
fun vibrarDispositivo(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(VibratorManager::class.java)
            val vibrator = vibratorManager?.defaultVibrator
            vibrator?.vibrate(
                VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            val vibrator = context.getSystemService(Vibrator::class.java)
            vibrator?.vibrate(
                VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        }
    } catch (_: Exception) {
        // Silencioso si el hardware/permiso no existe
    }
}
