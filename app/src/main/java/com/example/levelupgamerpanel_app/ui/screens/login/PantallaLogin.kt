package com.example.levelupgamerpanel_app.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.levelupgamerpanel_app.R
import com.example.levelupgamerpanel_app.ui.components.AlertaErrorLogin
import com.example.levelupgamerpanel_app.ui.components.BotonPrincipal
import com.example.levelupgamerpanel_app.ui.components.BotonSecundario
import com.example.levelupgamerpanel_app.ui.components.CampoContrasena
import com.example.levelupgamerpanel_app.ui.components.CampoUsuario
import com.example.levelupgamerpanel_app.ui.components.SubtituloLogin
import com.example.levelupgamerpanel_app.ui.components.TituloLogin
import com.example.levelupgamerpanel_app.ui.navigation.Routes

@Composable
fun PantallaLogin(
    navController: NavController,
    vm: LoginViewModel = viewModel()
) {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 5.dp,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(horizontal = 30.dp)
                    .offset(y = (-40).dp)
            ) {
                Column(
                    modifier = Modifier.padding(40.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    TituloLogin(modifier = Modifier.align(Alignment.CenterHorizontally))
                    SubtituloLogin()

                    // Usa parámetros con nombre para que no importe el orden del composable
                    CampoUsuario(
                        valor = vm.usuario.value,
                        onChange = vm::onUsuarioChange,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CampoContrasena(
                        valor = vm.contraseña.value,
                        onChange = vm::onContraseñaChange,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    BotonPrincipal(
                        texto = "Ingresar",
                        onClick = {
                            if (vm.validarLogin()) {
                                navController.navigate(Routes.Home)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                    )

                    if (vm.intentoLogin.value && !vm.error.value.isNullOrEmpty()) {
                        AlertaErrorLogin(
                            mensaje = vm.error.value ?: "",
                            onDismiss = { vm.error.value = null },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    BotonSecundario(
                        texto = "Registrarse",
                        onClick = { navController.navigate("registro") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                    )
                }
            }
        }
    }
}
