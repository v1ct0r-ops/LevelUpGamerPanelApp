@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.levelupgamerpanel_app.ui.screens.productos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.levelupgamerpanel_app.R
import com.example.levelupgamerpanel_app.AppViewModel
import com.example.levelupgamerpanel_app.data.models.Producto
import com.example.levelupgamerpanel_app.ui.navigation.Routes

// Pantalla principal que muestra la lista de productos con busqueda y filtros
@Composable
fun ProductosScreen(nav: NavController, vm: AppViewModel = viewModel()){
    val productos by vm.productos.collectAsState()
    val usuarioActual by vm.usuarioActual.collectAsState()
    val errorMessage by vm.errorMessage.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    
    var query by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var currentError by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMsg by remember { mutableStateOf("") }
    
    // Cargar productos desde el backend al abrir la pantalla
    LaunchedEffect(Unit) {
        vm.cargarProductos()
    }
    
    // Filtrar productos por nombre, codigo o categoria
    val list = productos.filter { 
        it.nombre.contains(query, true) || 
        it.codigo.contains(query, true) ||  // Cambiar de .id a .codigo
        it.categoria.contains(query, true)
    }
    // Verificar si el usuario actual es administrador
    val isAdmin = usuarioActual?.tipoUsuario == "ADMIN"

    // Mostrar dialogo de error cuando hay un mensaje de error
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
                        Text("Productos")
                    }
                },
                actions = {
                    // Solo los administradores pueden agregar productos
                    if (isAdmin) {
                        IconButton(onClick = { nav.navigate(Routes.NuevoProducto) }){
                            Icon(Icons.Default.Add, contentDescription = "Agregar producto")
                        }
                    }
                }
            )
        }
    ){ pv ->
        Column(Modifier.padding(pv).padding(12.dp)) {
            OutlinedTextField(
                value = query, 
                onValueChange = { query = it }, 
                label = { Text("Buscar por nombre, código o categoría") }, 
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            
            // Mostrar indicador de carga mientras se obtienen los productos
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn {
                    items(list){ p -> 
                        ProductoRow(
                            p = p, 
                            isAdmin = isAdmin,
                            // Navegar a la pantalla de edicion del producto
                            onEdit = { nav.navigate("editar_producto/${p.codigo}") },
                            // Eliminar producto del backend
                            onDelete = {
                                vm.removeProducto(
                                    id = p.codigo,
                                    onSuccess = {
                                        android.util.Log.d("ProductosScreen", "Producto ${p.codigo} eliminado exitosamente")
                                    },
                                    onError = { error ->
                                        android.util.Log.e("ProductosScreen", "Error al eliminar: $error")
                                        currentError = error
                                        showErrorDialog = true
                                    }
                                )
                            },
                            // Activar o desactivar producto
                            onActivate = {
                                vm.activarProducto(
                                    codigo = p.codigo,
                                    onSuccess = {
                                        successMsg = if (p.activo) "Producto desactivado" else "Producto activado"
                                        showSuccessDialog = true
                                    },
                                    onError = { error ->
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

    // Dialogo de confirmacion de exito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("¡Éxito!") },
            text = { Text(successMsg) },
            confirmButton = {
                TextButton(onClick = { showSuccessDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    // Dialogo para mostrar errores
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

// Tarjeta individual que muestra informacion de un producto
@Composable private fun ProductoRow(
    p: Producto, 
    isAdmin: Boolean, 
    onEdit:()->Unit, 
    onDelete:()->Unit,
    onActivate:()->Unit
){
    var showDialog by remember { mutableStateOf(false) }
    val stockCritico = p.stockCritico ?: 5

    Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(p.nombre, style = MaterialTheme.typography.titleLarge)
                    // Mostrar descripcion si existe
                    if (!p.descripcion.isNullOrEmpty()) {  // Agregar null-check con ?. operator
                        Text(p.descripcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                // Mostrar chip de alerta si el stock esta bajo
                if (p.stock <= stockCritico) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Stock crítico") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            labelColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Text("Código: ${p.codigo}", style = MaterialTheme.typography.bodySmall)  // Cambiar de .id a .codigo
            Text("Precio: $${p.precio} CLP", style = MaterialTheme.typography.bodyMedium)
            Text("Stock: ${p.stock} (Crítico: $stockCritico)", style = MaterialTheme.typography.bodyMedium)
            Text("Categoría: ${p.categoria}", style = MaterialTheme.typography.bodyMedium)
            // Mostrar detalles adicionales si existen
            if (!p.detalles.isNullOrEmpty()) {  // Agregar null-check con ?. operator
                Text("Detalles: ${p.detalles}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("Estado: ${if (p.activo) "Activo" else "Inactivo"}", style = MaterialTheme.typography.bodySmall)
            
            // Mostrar imagen del producto si existe
            AnimatedVisibility(p.imagen != null && p.imagen.isNotEmpty()) {  // Cambiar imagenUri a imagen
                Image(
                    painter = rememberAsyncImagePainter(p.imagen),  // Cambiar imagenUri a imagen
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(120.dp).padding(top = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }
            // Botones de accion (solo para administradores)
            if (isAdmin) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    // Mostrar boton Activar unicamente cuando el producto esta inactivo
                    if (!p.activo) {
                        TextButton(
                            onClick = onActivate,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Activar")
                        }
                    } else {
                        // Cuando esta activo no mostramos boton de activacion
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Row {
                        TextButton(onClick = onEdit) { Text("Editar") }
                        TextButton(onClick = { showDialog = true }) { Text("Eliminar") }
                    }
                }
            }
        }
    }

    // Dialogo de confirmacion antes de eliminar producto
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar el producto '${p.nombre}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDialog = false
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
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

// Pantalla para crear un nuevo producto en el sistema
@Composable
fun NuevoProductoScreen(nav: NavController, vm: AppViewModel = viewModel()) {
    var codigo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var detalles by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var stockCritico by remember { mutableStateOf("5") }
    var categoria by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf("") }
    var activo by remember { mutableStateOf(true) }
    
    var err by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    
    val isLoading by vm.isLoading.collectAsState()

    // Validar campos del formulario antes de guardar
    fun validar(): Boolean {
        when {
            codigo.trim().isEmpty() -> {
                err = "El código es obligatorio"
                return false
            }
            codigo.trim().length < 3 -> {
                err = "El código debe tener al menos 3 caracteres"
                return false
            }
            nombre.trim().isEmpty() -> {
                err = "El nombre es obligatorio"
                return false
            }
            nombre.trim().length < 3 -> {
                err = "El nombre debe tener al menos 3 caracteres"
                return false
            }
            precio.trim().isEmpty() -> {
                err = "El precio es obligatorio"
                return false
            }
            precio.toDoubleOrNull() == null || precio.toDouble() <= 0 -> {
                err = "El precio debe ser un número mayor a 0"
                return false
            }
            stock.trim().isEmpty() -> {
                err = "El stock es obligatorio"
                return false
            }
            stock.toIntOrNull() == null || stock.toInt() < 0 -> {
                err = "El stock debe ser un número mayor o igual a 0"
                return false
            }
            stockCritico.toIntOrNull() == null || stockCritico.toInt() < 0 -> {
                err = "El stock crítico debe ser un número mayor o igual a 0"
                return false
            }
            categoria.trim().isEmpty() -> {
                err = "La categoría es obligatoria"
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
                    Text("Nuevo Producto", style = MaterialTheme.typography.titleLarge)
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
                    "Información del Producto",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item { 
                OutlinedTextField(
                    value = codigo, 
                    onValueChange = { codigo = it }, 
                    label = { Text("Código *") },
                    supportingText = { Text("Identificador único del producto") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) 
            }
            
            item { 
                OutlinedTextField(
                    value = nombre, 
                    onValueChange = { nombre = it }, 
                    label = { Text("Nombre *") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) 
            }
            
            item { 
                OutlinedTextField(
                    value = descripcion, 
                    onValueChange = { descripcion = it }, 
                    label = { Text("Descripción") },
                    supportingText = { Text("Descripción breve del producto") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                    enabled = !isLoading
                ) 
            }
            
            item { 
                OutlinedTextField(
                    value = detalles, 
                    onValueChange = { detalles = it }, 
                    label = { Text("Detalles") },
                    supportingText = { Text("Información adicional del producto") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    enabled = !isLoading
                ) 
            }
            
            item { 
                Text(
                    "Precio y Stock",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item { 
                OutlinedTextField(
                    value = precio, 
                    onValueChange = { precio = it }, 
                    label = { Text("Precio (CLP) *") }, 
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    prefix = { Text("$") }
                ) 
            }
            
            item { 
                OutlinedTextField(
                    value = stock, 
                    onValueChange = { stock = it }, 
                    label = { Text("Stock *") }, 
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) 
            }
            
            item { 
                OutlinedTextField(
                    value = stockCritico, 
                    onValueChange = { stockCritico = it }, 
                    label = { Text("Stock Crítico") },
                    supportingText = { Text("Nivel mínimo antes de alerta") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) 
            }
            
            item { 
                Text(
                    "Categoría e Imagen",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item { 
                OutlinedTextField(
                    value = categoria, 
                    onValueChange = { categoria = it }, 
                    label = { Text("Categoría *") },
                    supportingText = { Text("Ej: Consolas, Juegos, Accesorios") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) 
            }
            
            item { 
                OutlinedTextField(
                    value = imagenUri, 
                    onValueChange = { imagenUri = it }, 
                    label = { Text("URL de Imagen") },
                    supportingText = { Text("URL de la imagen del producto (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) 
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Producto activo")
                    Switch(
                        checked = activo,
                        onCheckedChange = { activo = it },
                        enabled = !isLoading
                    )
                }
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
                        
                        // Crear objeto Producto con todos los datos ingresados
                        val nuevoProducto = Producto(
                            codigo = codigo.trim(),
                            nombre = nombre.trim(),
                            descripcion = descripcion.trim(),
                            detalles = detalles.trim(),
                            precio = precio.toDouble(),  // Cambiar de toInt() a toDouble()
                            stock = stock.toInt(),
                            stockCritico = stockCritico.toIntOrNull() ?: 5,
                            categoria = categoria.trim(),
                            imagen = if (imagenUri.trim().isEmpty()) null else imagenUri.trim(),  // Cambiar imagenUri a imagen
                            activo = activo
                        )
                        
                        // Enviar producto al backend para guardarlo
                        vm.addProducto(
                            p = nuevoProducto,
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
                    Text(if (isLoading) "Guardando..." else "Guardar Producto") 
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
    
    // Dialogo de confirmacion de exito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("¡Éxito!") },
            text = { Text("El producto ha sido creado correctamente") },
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
    
    // Dialogo para mostrar errores
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text(errorMsg) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

// Pantalla para editar un producto existente
@Composable
fun EditarProductoScreen(nav: NavController, vm: AppViewModel = viewModel()) {
    val productos by vm.productos.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    
    val id = nav.currentBackStackEntry?.arguments?.getString("id")
    
    // Cargar productos desde el backend si la lista esta vacia
    LaunchedEffect(Unit) {
        if (productos.isEmpty()) {
            vm.cargarProductos()
        }
    }
    
    // Buscar el producto por codigo en la lista
    val p = productos.firstOrNull { it.codigo == id }
    
    if (p == null) {
        // Mostrar loading si está cargando
        if (isLoading) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(painterResource(R.drawable.logo), null, modifier = Modifier.size(28.dp))
                                Spacer(Modifier.width(10.dp))
                                Text("Cargando...")
                            }
                        }
                    )
                }
            ) { pv ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(pv),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else {
            // Producto no encontrado en la lista
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(painterResource(R.drawable.logo), null, modifier = Modifier.size(28.dp))
                                Spacer(Modifier.width(10.dp))
                                Text("Error")
                            }
                        }
                    )
                }
            ) { pv ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(pv),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Producto no encontrado",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { nav.popBackStack() }) {
                            Text("Volver a productos")
                        }
                    }
                }
            }
        }
        return
    }

    // Inicializar campos del formulario con los datos del producto
    var codigo by remember { mutableStateOf(p.codigo) }
    var nombre by remember { mutableStateOf(p.nombre) }
    var descripcion by remember { mutableStateOf(p.descripcion ?: "") }
    var detalles by remember { mutableStateOf(p.detalles ?: "") }
    var precio by remember { mutableStateOf(p.precio.toString()) }
    var stock by remember { mutableStateOf(p.stock.toString()) }
    var stockCritico by remember { mutableStateOf((p.stockCritico ?: 5).toString()) }
    var categoria by remember { mutableStateOf(p.categoria) }
    var imagenUri by remember { mutableStateOf(p.imagen ?: "") }
    
    var err by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    // Validar campos del formulario antes de guardar cambios
    fun validar(): Boolean {
        when {
            nombre.trim().isEmpty() -> {
                err = "El nombre es obligatorio"
                return false
            }
            nombre.trim().length < 3 -> {
                err = "El nombre debe tener al menos 3 caracteres"
                return false
            }
            precio.trim().isEmpty() -> {
                err = "El precio es obligatorio"
                return false
            }
            precio.toDoubleOrNull() == null || precio.toDouble() <= 0 -> {
                err = "El precio debe ser un número mayor a 0"
                return false
            }
            stock.trim().isEmpty() -> {
                err = "El stock es obligatorio"
                return false
            }
            stock.toIntOrNull() == null || stock.toInt() < 0 -> {
                err = "El stock debe ser un número mayor o igual a 0"
                return false
            }
            stockCritico.toIntOrNull() == null || stockCritico.toInt() < 0 -> {
                err = "El stock crítico debe ser un número mayor o igual a 0"
                return false
            }
            categoria.trim().isEmpty() -> {
                err = "La categoría es obligatoria"
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
                    Text("Editar Producto", style = MaterialTheme.typography.titleLarge)
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
                    "Información del Producto",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item { 
                OutlinedTextField(
                    value = codigo, 
                    onValueChange = { }, 
                    label = { Text("Código") },
                    supportingText = { Text("El código no se puede modificar") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) 
            }
            
            item { 
                OutlinedTextField(
                    value = nombre, 
                    onValueChange = { nombre = it }, 
                    label = { Text("Nombre *") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) 
            }
            
            item { 
                OutlinedTextField(
                    value = descripcion, 
                    onValueChange = { descripcion = it }, 
                    label = { Text("Descripción") },
                    supportingText = { Text("Descripción breve del producto") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                    enabled = !isLoading
                ) 
            }
            
            item { 
                OutlinedTextField(
                    value = detalles, 
                    onValueChange = { detalles = it }, 
                    label = { Text("Detalles") },
                    supportingText = { Text("Información adicional del producto") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    enabled = !isLoading
                ) 
            }
            
            item { 
                Text(
                    "Precio y Stock",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item { 
                OutlinedTextField(
                    value = precio, 
                    onValueChange = { precio = it }, 
                    label = { Text("Precio (CLP) *") }, 
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    prefix = { Text("$") }
                ) 
            }
            
            item { 
                OutlinedTextField(
                    value = stock, 
                    onValueChange = { stock = it }, 
                    label = { Text("Stock *") }, 
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) 
            }
            
            item { 
                OutlinedTextField(
                    value = stockCritico, 
                    onValueChange = { stockCritico = it }, 
                    label = { Text("Stock Crítico") },
                    supportingText = { Text("Nivel mínimo antes de alerta") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) 
            }
            
            item { 
                Text(
                    "Categoría e Imagen",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item { 
                OutlinedTextField(
                    value = categoria, 
                    onValueChange = { categoria = it }, 
                    label = { Text("Categoría *") },
                    supportingText = { Text("Ej: Consolas, Juegos, Accesorios") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) 
            }
            
            item { 
                OutlinedTextField(
                    value = imagenUri, 
                    onValueChange = { imagenUri = it }, 
                    label = { Text("URL de Imagen") },
                    supportingText = { Text("URL de la imagen del producto (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
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
                        
                        // Crear copia del producto con los datos actualizados
                        val productoActualizado = p.copy(
                            nombre = nombre.trim(),
                            descripcion = if (descripcion.trim().isEmpty()) null else descripcion.trim(),
                            detalles = if (detalles.trim().isEmpty()) null else detalles.trim(),
                            precio = precio.toDouble(),
                            stock = stock.toInt(),
                            stockCritico = stockCritico.toIntOrNull() ?: 5,
                            categoria = categoria.trim(),
                            imagen = if (imagenUri.trim().isEmpty()) null else imagenUri.trim()
                        )
                        
                        android.util.Log.d("EditarProducto", "Guardando producto: codigo=${productoActualizado.codigo}, activo=${productoActualizado.activo}")
                        
                        // Enviar producto actualizado al backend
                        vm.updateProducto(
                            p = productoActualizado,
                            onSuccess = {
                                android.util.Log.d("EditarProducto", "Producto actualizado exitosamente")
                                showSuccessDialog = true
                            },
                            onError = { error ->
                                android.util.Log.e("EditarProducto", "Error al actualizar: $error")
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
                    Text(if (isLoading) "Guardando..." else "Guardar Cambios") 
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
    
    // Dialogo de confirmacion de exito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("¡Éxito!") },
            text = { Text("El producto ha sido actualizado correctamente") },
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
    
    // Dialogo para mostrar errores
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text(errorMsg) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}