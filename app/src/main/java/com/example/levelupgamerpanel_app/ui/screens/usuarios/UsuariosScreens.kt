@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.levelupgamerpanel_app.ui.screens.usuarios

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.levelupgamerpanel_app.R
import com.example.levelupgamerpanel_app.AppViewModel
import com.example.levelupgamerpanel_app.data.models.Usuario
import com.example.levelupgamerpanel_app.ui.navigation.Routes

@Composable
fun UsuariosScreen(nav: NavController, vm: AppViewModel = viewModel()){
    val usuarios by vm.usuarios.collectAsState()
    var query by remember { mutableStateOf("") }
    val list = usuarios.filter { (it.nombres + it.apellidos + it.correo).contains(query, true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painterResource(R.drawable.logo), null, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Usuarios")
                    }
                },
                actions = {
                    IconButton(onClick = { nav.navigate(Routes.NuevoUsuario) }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            )
        }
    ){ pv ->
        Column(Modifier.padding(pv).padding(12.dp)) {
            OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Buscar") }, modifier = Modifier.fillMaxWidth())
            LazyColumn {
                items(list){ u -> UsuarioRow(u, onLongPress = { vm.removeUsuario(u.correo) }) }
            }
        }
    }
}

@Composable
private fun UsuarioRow(u: Usuario, onLongPress: () -> Unit) {
    Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        ListItem(
            headlineContent = { Text("${u.nombres} ${u.apellidos}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold) },
            supportingContent = { Text(u.correo) },
            trailingContent = { AssistChip(onClick = {}, label = { Text(u.tipoUsuario) }) },
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
fun NuevoUsuarioScreen(nav: NavController, vm: AppViewModel = viewModel()){
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("admin") }
    var err by remember { mutableStateOf<String?>(null) }

    fun validar(): Boolean {
        if (nombres.length < 2 || apellidos.length < 2) { err = "Nombre y apellido requeridos"; return false }
        if (!correo.contains("@")) { err = "Correo inválido"; return false }
        if (pass.length < 4) { err = "Contraseña mínima 4"; return false }
        return true
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painterResource(R.drawable.logo), null, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Nuevo usuario")
                }
            })
    }){ pv ->
        Column(Modifier.padding(pv).padding(16.dp)) {
            OutlinedTextField(nombres, { nombres = it }, label = { Text("Nombres") })
            OutlinedTextField(apellidos, { apellidos = it }, label = { Text("Apellidos") })
            OutlinedTextField(correo, { correo = it }, label = { Text("Correo") })
            OutlinedTextField(pass, { pass = it }, label = { Text("Contraseña") })
            Row(Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = tipo=="admin", onClick = { tipo="admin" }, label = { Text("Admin") })
                FilterChip(selected = tipo=="vendedor", onClick = { tipo="vendedor" }, label = { Text("Vendedor") })
                FilterChip(selected = tipo=="cliente", onClick = { tipo="cliente" }, label = { Text("Cliente") })
            }
            if (err != null) { Text(err!!, color = MaterialTheme.colorScheme.error) }
            Spacer(Modifier.height(12.dp))
            Button(onClick = {
                if (!validar()) return@Button
                vm.addUsuario(Usuario(correo, pass, tipo, nombres, apellidos, 0))
                nav.popBackStack()
            }) { Text("Crear usuario") }
        }
    }
}
