@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.levelupgamerpanel_app.ui.screens.usuarios

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
    val usuarioActual by vm.usuarioActual.collectAsState()
    val errorMessage by vm.errorMessage.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    
    var query by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var currentError by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        vm.cargarUsuarios()
    }
    
    val list = usuarios.filter { u ->
        query.isEmpty() || 
        u.run.contains(query, true) ||
        u.nombres.contains(query, true) ||
        u.apellidos.contains(query, true) ||
        u.correo.contains(query, true) ||
        u.tipoUsuario.contains(query, true)
    }
    
    val isAdmin = usuarioActual?.tipoUsuario == "ADMIN"

    LaunchedEffect(usuarioActual) {
        val usuario = usuarioActual
        if (usuario != null && usuario.tipoUsuario.uppercase() != "ADMIN") {
            nav.popBackStack()
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            currentError = errorMessage
            showErrorDialog = true
            vm.clearError()
        }
    }

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
                    if (isAdmin) {
                        IconButton(onClick = { nav.navigate(Routes.NuevoUsuario) }) {
                            Icon(Icons.Default.Add, contentDescription = "Nuevo usuario")
                        }
                    }
                }
            )
        }
    ) { pv ->
        Column(Modifier.padding(pv).padding(12.dp)) {
            OutlinedTextField(
                value = query, 
                onValueChange = { query = it }, 
                label = { Text("Buscar por RUN, nombre, correo o tipo") }, 
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (list.isEmpty()) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (query.isEmpty()) "No hay usuarios cargados." else "No se encontraron usuarios.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn {
                    items(list) { u ->
                        UsuarioRow(
                            u = u,
                            isAdmin = isAdmin,
                            onDelete = {
                                vm.removeUsuario(
                                    correo = u.correo,
                                    onSuccess = {
                                        android.util.Log.d("UsuariosScreen", "Usuario ${u.run} eliminado exitosamente")
                                    },
                                    onError = { error ->
                                        android.util.Log.e("UsuariosScreen", "Error al eliminar: $error")
                                        currentError = error
                                        showErrorDialog = true
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    if (showErrorDialog && currentError != null) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text(currentError ?: "Ha ocurrido un error") },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false; currentError = null }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun UsuarioRow(u: Usuario, isAdmin: Boolean, onDelete: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${u.nombres} ${u.apellidos}".trim().ifEmpty { "Sin nombre" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "RUN: ${u.run.ifEmpty { "—" }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                AssistChip(
                    onClick = {},
                    label = { Text(u.tipoUsuario) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (u.tipoUsuario.uppercase()) {
                            "ADMIN" -> MaterialTheme.colorScheme.errorContainer
                            "VENDEDOR" -> MaterialTheme.colorScheme.secondaryContainer
                            else -> MaterialTheme.colorScheme.tertiaryContainer
                        },
                        labelColor = when (u.tipoUsuario.uppercase()) {
                            "ADMIN" -> MaterialTheme.colorScheme.onErrorContainer
                            "VENDEDOR" -> MaterialTheme.colorScheme.onSecondaryContainer
                            else -> MaterialTheme.colorScheme.onTertiaryContainer
                        }
                    )
                )
            }
            
            Spacer(Modifier.height(8.dp))
            
            Text(
                text = "Correo: ${u.correo}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            if (u.puntosLevelUp > 0) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Puntos LevelUp: ${u.puntosLevelUp}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Text(
                text = "Estado: ${if (u.activo) "Activo" else "Inactivo"}",
                style = MaterialTheme.typography.bodySmall,
                color = if (u.activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Eliminar usuario") },
            text = { 
                Text("¿Estás seguro de eliminar al usuario ${u.nombres} ${u.apellidos} (RUN: ${u.run})? Esta acción no se puede deshacer.") 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDialog = false
                    }
                ) {
                    Text("Estoy seguro", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun NuevoUsuarioScreen(nav: NavController, vm: AppViewModel = viewModel()){
    val usuarioActual by vm.usuarioActual.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    
    var run by remember { mutableStateOf("") }
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var tipoUsuario by remember { mutableStateOf("CLIENTE") }
    var region by remember { mutableStateOf("") }
    var comuna by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    
    var expandedRegion by remember { mutableStateOf(false) }
    var expandedComuna by remember { mutableStateOf(false) }
    
    val regiones = remember {
        mapOf(
            "Región Metropolitana" to listOf("El Bosque", "San Bernardo", "Santiago", "Providencia", "Las Condes", "Maipú", "Puente Alto"),
            "Valparaíso" to listOf("Valparaíso", "Viña del Mar", "Quilpué", "Villa Alemana"),
            "Biobío" to listOf("Concepción", "Talcahuano", "Chiguayante", "San Pedro de la Paz")
        )
    }
    val comunasDisponibles = regiones[region] ?: emptyList()
    
    var err by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    
    val isAdmin = usuarioActual?.tipoUsuario == "ADMIN"

    LaunchedEffect(usuarioActual) {
        val usuario = usuarioActual
        if (usuario != null && usuario.tipoUsuario.uppercase() != "ADMIN") {
            nav.popBackStack()
        }
    }

    fun validar(): Boolean {
        when {
            run.trim().isEmpty() -> {
                err = "El RUN es obligatorio"
                return false
            }
            run.trim().length < 8 -> {
                err = "El RUN debe tener al menos 8 caracteres (sin puntos, con guión)"
                return false
            }
            nombres.trim().isEmpty() -> {
                err = "Los nombres son obligatorios"
                return false
            }
            nombres.trim().length < 2 -> {
                err = "Los nombres deben tener al menos 2 caracteres"
                return false
            }
            apellidos.trim().isEmpty() -> {
                err = "Los apellidos son obligatorios"
                return false
            }
            apellidos.trim().length < 2 -> {
                err = "Los apellidos deben tener al menos 2 caracteres"
                return false
            }
            correo.trim().isEmpty() -> {
                err = "El correo es obligatorio"
                return false
            }
            !correo.contains("@") || !correo.contains(".") -> {
                err = "El correo debe ser válido"
                return false
            }
            password.trim().isEmpty() -> {
                err = "La contraseña es obligatoria"
                return false
            }
            password.length < 4 -> {
                err = "La contraseña debe tener al menos 4 caracteres"
                return false
            }
            region.trim().isEmpty() -> {
                err = "La región es obligatoria"
                return false
            }
            comuna.trim().isEmpty() -> {
                err = "La comuna es obligatoria"
                return false
            }
            direccion.trim().isEmpty() -> {
                err = "La dirección es obligatoria"
                return false
            }
            else -> {
                err = null
                return true
            }
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painterResource(R.drawable.logo), null, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Nuevo Usuario", style = MaterialTheme.typography.titleLarge)
                }
            }
        )
    }) { pv ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(pv)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { 
                Text(
                    "Información Personal",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item { 
                OutlinedTextField(
                    value = run, 
                    onValueChange = { run = it }, 
                    label = { Text("RUN *") },
                    supportingText = { Text("Formato: 12345678-9 (sin puntos, con guión)") },
                    placeholder = { Text("12345678-9") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true,
                    maxLines = 1
                ) 
            }
            
            item { 
                OutlinedTextField(
                    value = nombres, 
                    onValueChange = { nombres = it }, 
                    label = { Text("Nombres *") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true
                ) 
            }
            
            item { 
                OutlinedTextField(
                    value = apellidos, 
                    onValueChange = { apellidos = it }, 
                    label = { Text("Apellidos *") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true
                ) 
            }
            
            item { 
                OutlinedTextField(
                    value = correo, 
                    onValueChange = { correo = it }, 
                    label = { Text("Correo *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true
                ) 
            }
            
            item { 
                OutlinedTextField(
                    value = password, 
                    onValueChange = { password = it }, 
                    label = { Text("Contraseña *") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true
                ) 
            }
            
            item { 
                Text(
                    "Tipo de Usuario",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = tipoUsuario == "CLIENTE",
                            onClick = { tipoUsuario = "CLIENTE" },
                            label = { Text("Cliente") },
                            enabled = !isLoading,
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = tipoUsuario == "VENDEDOR",
                            onClick = { tipoUsuario = "VENDEDOR" },
                            label = { Text("Vendedor") },
                            enabled = !isLoading,
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = tipoUsuario == "ADMIN",
                            onClick = { tipoUsuario = "ADMIN" },
                            label = { Text("Administrador") },
                            enabled = !isLoading,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            item { 
                Text(
                    "Dirección",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item { 
                ExposedDropdownMenuBox(
                    expanded = expandedRegion,
                    onExpandedChange = { expandedRegion = !expandedRegion }
                ) {
                    OutlinedTextField(
                        value = region,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Región *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRegion) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                        enabled = !isLoading
                    )
                    ExposedDropdownMenu(
                        expanded = expandedRegion,
                        onDismissRequest = { expandedRegion = false }
                    ) {
                        regiones.keys.forEach { regionName ->
                            DropdownMenuItem(
                                text = { Text(regionName) },
                                onClick = {
                                    region = regionName
                                    comuna = ""
                                    expandedRegion = false
                                }
                            )
                        }
                    }
                }
            }
            
            item { 
                ExposedDropdownMenuBox(
                    expanded = expandedComuna,
                    onExpandedChange = { expandedComuna = !expandedComuna }
                ) {
                    OutlinedTextField(
                        value = comuna,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Comuna *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedComuna) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                        enabled = !isLoading && region.isNotEmpty()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedComuna,
                        onDismissRequest = { expandedComuna = false }
                    ) {
                        comunasDisponibles.forEach { comunaName ->
                            DropdownMenuItem(
                                text = { Text(comunaName) },
                                onClick = {
                                    comuna = comunaName
                                    expandedComuna = false
                                }
                            )
                        }
                    }
                }
            }
            
            item { 
                OutlinedTextField(
                    value = direccion, 
                    onValueChange = { direccion = it }, 
                    label = { Text("Dirección *") },
                    supportingText = { Text("Calle, número, departamento, etc.") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    minLines = 2,
                    maxLines = 3
                ) 
            }
            
            item { 
                if (err != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            err!!, 
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
            
            item {
                Button(
                    onClick = {
                        if (!validar()) return@Button
                        
                        val nuevoUsuario = Usuario(
                            run = run.trim(),
                            nombres = nombres.trim(),
                            apellidos = apellidos.trim(),
                            correo = correo.trim(),
                            password = password,
                            tipoUsuario = tipoUsuario,
                            region = region.trim(),
                            comuna = comuna.trim(),
                            direccion = direccion.trim(),
                            puntosLevelUp = 0,
                            activo = true
                        )
                        
                        vm.addUsuario(
                            u = nuevoUsuario,
                            onSuccess = {
                                showSuccessDialog = true
                            },
                            onError = { error ->
                                errorMsg = error
                                showErrorDialog = true
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) { 
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(if (isLoading) "Guardando..." else "Guardar Usuario") 
                }
            }
            
            item {
                OutlinedButton(
                    onClick = { nav.popBackStack() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
    
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("¡Éxito!") },
            text = { Text("El usuario ha sido creado correctamente en el backend.") },
            confirmButton = {
                TextButton(onClick = { 
                    showSuccessDialog = false
                    nav.popBackStack()
                }) {
                    Text("OK")
                }
            }
        )
    }
    
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text(errorMsg.ifEmpty { "No se pudo crear el usuario. Revisa el backend (RUN/correo duplicado, etc.)." }) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}
