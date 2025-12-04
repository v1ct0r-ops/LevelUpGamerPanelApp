@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.levelupgamerpanel_app.ui.screens.pedidos

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
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
import com.example.levelupgamerpanel_app.data.models.Pedido
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// Formatear numeros a pesos chilenos (CLP)
private fun formatCLP(amount: Double): String {  // Cambiar de Int a Double
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}

// Formatear fecha ISO a formato chileno (dd/MM/yyyy HH:mm:ss)
private fun formatFechaHora(fechaIso: String): String {
    return try {
        // El backend envia LocalDateTime en formato ISO: "2025-10-24T10:30:00"
        val instant = java.time.Instant.parse(fechaIso)
        val formatter = java.time.format.DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm:ss")
            .withZone(java.time.ZoneId.systemDefault())
        formatter.format(instant)
    } catch (e: Exception) {
        // Si falla el parsing, intentar con SimpleDateFormat legacy
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(fechaIso)
            outputFormat.format(date ?: Date())
        } catch (e2: Exception) {
            fechaIso
        }
    }
}

// Obtener colores segun el estado del pedido
private fun getEstadoColor(estado: String, colorScheme: ColorScheme): Pair<androidx.compose.ui.graphics.Color, androidx.compose.ui.graphics.Color> {
    return when (estado.uppercase()) {
        "PENDIENTE" -> Pair(colorScheme.primaryContainer, colorScheme.onPrimaryContainer)
        "DESPACHADO" -> Pair(colorScheme.secondaryContainer, colorScheme.onSecondaryContainer)
        "CANCELADO" -> Pair(colorScheme.errorContainer, colorScheme.onErrorContainer)
        else -> Pair(colorScheme.surfaceVariant, colorScheme.onSurfaceVariant)
    }
}

// Formatear la etiqueta del estado del pedido
private fun getEstadoLabel(estado: String): String {
    return when (estado.uppercase()) {
        "PENDIENTE" -> "Pendiente"
        "DESPACHADO" -> "Despachado"
        "CANCELADO" -> "Cancelado"
        else -> estado
    }
}

// Pantalla principal que muestra la lista de pedidos con filtros
@Composable
fun PedidosScreen(nav: NavController, vm: AppViewModel = viewModel()) {
    val pedidos by vm.pedidos.collectAsState()
    val usuarioActual by vm.usuarioActual.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val errorMessage by vm.errorMessage.collectAsState()
    
    var filtroEstado by remember { mutableStateOf("") }
    var currentError by remember { mutableStateOf<String?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }
    
    // Cargar todos los pedidos desde el backend al abrir la pantalla
    LaunchedEffect(Unit) {
        vm.cargarPedidos()
    }
    
    // Filtrar pedidos segun el estado seleccionado
    val pedidosFiltrados = remember(pedidos, filtroEstado) {
        if (filtroEstado.isEmpty()) {
            pedidos
        } else {
            pedidos.filter { it.estado.equals(filtroEstado, ignoreCase = true) }
        }
    }
    
    val tipo = usuarioActual?.tipoUsuario ?: ""
    val isAdmin = tipo.uppercase() == "ADMIN"
    val canSeeBoletas = tipo.uppercase() == "ADMIN" || tipo.uppercase() == "VENDEDOR"

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
                        Text("Pedidos")
                    }
                }
            )
        }
    ) { pv ->
        Column(Modifier.padding(pv).padding(12.dp)) {
            // Chips para filtrar pedidos por estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filtroEstado.isEmpty(),
                    onClick = { filtroEstado = "" },
                    label = { Text("Todos") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = filtroEstado.equals("PENDIENTE", ignoreCase = true),
                    onClick = { filtroEstado = "PENDIENTE" },
                    label = { Text("Pendientes") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = filtroEstado.equals("DESPACHADO", ignoreCase = true),
                    onClick = { filtroEstado = "DESPACHADO" },
                    label = { Text("Despachados") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = filtroEstado.equals("CANCELADO", ignoreCase = true),
                    onClick = { filtroEstado = "CANCELADO" },
                    label = { Text("Cancelados") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(Modifier.height(12.dp))
            
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(8.dp))
                        Text("Cargando pedidos desde el backend...")
                    }
                }
            } else if (pedidosFiltrados.isEmpty()) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📦 No hay pedidos para mostrar.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(pedidosFiltrados) { pedido -> 
                        PedidoCard(
                            pedido = pedido,
                            canSeeBoletas = canSeeBoletas,
                            onVerDetalle = { 
                                nav.navigate("detalle_pedido/${pedido.id}")
                            },
                            onVerBoleta = {
                                // Intentar obtener boleta existente o generar nueva
                                android.util.Log.d("PedidosScreen", "Intentando obtener boleta para pedido ${pedido.id}")
                                
                                // Primero intentar cargar boleta existente
                                vm.cargarBoletaByPedido(
                                    pedidoId = pedido.id,
                                    onSuccess = { boleta ->
                                        if (boleta != null) {
                                            // La boleta ya existe, navegar a ella
                                            android.util.Log.d("PedidosScreen", "Boleta encontrada: ${boleta.numero}")
                                            val numeroEncoded = java.net.URLEncoder.encode(boleta.numero, "UTF-8")
                                            nav.navigate("detalle_boleta/$numeroEncoded")
                                        } else {
                                            // No existe boleta, generarla
                                            android.util.Log.d("PedidosScreen", "No existe boleta, generando nueva para pedido ${pedido.id}")
                                            vm.generarBoleta(
                                                pedidoId = pedido.id,
                                                onSuccess = { boletaNueva ->
                                                    android.util.Log.d("PedidosScreen", "Boleta generada: ${boletaNueva.numero}")
                                                    val numeroEncoded = java.net.URLEncoder.encode(boletaNueva.numero, "UTF-8")
                                                    nav.navigate("detalle_boleta/$numeroEncoded")
                                                },
                                                onError = { errorGen ->
                                                    android.util.Log.e("PedidosScreen", "Error al generar boleta: $errorGen")
                                                    currentError = errorGen
                                                    showErrorDialog = true
                                                }
                                            )
                                        }
                                    },
                                    onError = { error ->
                                        // Solo se llama para errores reales de conexión
                                        android.util.Log.e("PedidosScreen", "Error de conexión: $error")
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

// Tarjeta individual que muestra informacion de un pedido
@Composable 
private fun PedidoCard(
    pedido: Pedido,
    canSeeBoletas: Boolean,
    onVerDetalle: () -> Unit,
    onVerBoleta: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val (containerColor, contentColor) = getEstadoColor(pedido.estado, colorScheme)
    
    // Formato del codigo del pedido
    val codigo = "PED-${pedido.id}"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Cabecera con codigo y estado del pedido
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = codigo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                AssistChip(
                    onClick = { },
                    label = { Text(getEstadoLabel(pedido.estado)) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = containerColor,
                        labelColor = contentColor
                    )
                )
            }
            
            Spacer(Modifier.height(8.dp))
            
            // Fecha de creacion del pedido y datos del cliente
            Text(
                text = formatFechaHora(pedido.fecha),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (pedido.usuario != null && pedido.usuario.correo.isNotEmpty()) {
                Text(
                    text = "Cliente: ${pedido.usuario.correo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(Modifier.height(8.dp))
            
            // Total
            Text(
                text = "Total: ${formatCLP(pedido.total)}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(Modifier.height(12.dp))
            
            // Botones para ver detalle y generar boleta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onVerDetalle,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Ver detalle")
                }
                
                if (canSeeBoletas) {
                    Button(
                        onClick = {
                            // Obtener boleta existente o generar una nueva
                            android.util.Log.d("PedidosScreen", "Generando/obteniendo boleta para pedido ${pedido.id}")
                            // Navegar directamente a generar boleta
                            onVerBoleta()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Receipt,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Ver boleta")
                    }
                }
            }
        }
    }
}

// Pantalla que muestra el detalle completo de un pedido especifico
@Composable
fun DetallePedidoScreen(nav: NavController, vm: AppViewModel = viewModel()) {
    val usuarioActual by vm.usuarioActual.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    
    // Obtener ID del pedido desde la navegacion y convertir a Long
    val idString = nav.currentBackStackEntry?.arguments?.getString("id")
    val id = idString?.toLongOrNull()
    
    // Estados para manejar la carga del pedido
    var pedido by remember { mutableStateOf<Pedido?>(null) }
    var loadingPedido by remember { mutableStateOf(true) }
    var errorCarga by remember { mutableStateOf<String?>(null) }
    
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    var successMsg by remember { mutableStateOf("") }
    
    val tipo = usuarioActual?.tipoUsuario ?: ""
    val isAdmin = tipo.uppercase() == "ADMIN"
    val canChangeStatus = isAdmin || tipo.uppercase() == "VENDEDOR"
    
    // Cargar pedido especifico desde el backend usando el ID
    LaunchedEffect(id) {
        android.util.Log.d("DetallePedido", "=== INICIANDO CARGA DE PEDIDO ===")
        android.util.Log.d("DetallePedido", "ID String: $idString")
        android.util.Log.d("DetallePedido", "ID Long: $id")
        
        if (id == null) {
            android.util.Log.e("DetallePedido", "ID inválido")
            errorCarga = "ID inválido: $idString"
            loadingPedido = false
            return@LaunchedEffect
        }
        
        loadingPedido = true
        errorCarga = null
        
        vm.cargarPedidoById(
            id = id,
            onSuccess = { pedidoCargado ->
                android.util.Log.d("DetallePedido", "✅ Pedido cargado exitosamente: ${pedidoCargado.id}")
                android.util.Log.d("DetallePedido", "Estado: ${pedidoCargado.estado}")
                android.util.Log.d("DetallePedido", "Total: ${pedidoCargado.total}")
                android.util.Log.d("DetallePedido", "Items: ${pedidoCargado.items.size}")
                pedido = pedidoCargado
                loadingPedido = false
            },
            onError = { error ->
                android.util.Log.e("DetallePedido", "❌ Error al cargar pedido: $error")
                errorCarga = error
                loadingPedido = false
                pedido = null
            }
        )
    }
    
    // Mostrar pantalla de carga mientras se obtiene el pedido
    if (loadingPedido) {
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(8.dp))
                    Text("Cargando pedido desde el backend...")
                }
            }
        }
        return
    }
    
    // Mostrar error si no se pudo cargar el pedido
    if (errorCarga != null) {
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
                        text = "Pedido no encontrado",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "ID: ${idString ?: "inválido"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = errorCarga ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { nav.popBackStack() }) {
                        Text("Volver a pedidos")
                    }
                }
            }
        }
        return
    }
    
    // Si no hay pedido disponible (caso raro)
    if (pedido == null) {
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
                        text = "Pedido no encontrado",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { nav.popBackStack() }) {
                        Text("Volver a pedidos")
                    }
                }
            }
        }
        return
    }
    
    // Aqui pedido ya tiene datos, mostrar el detalle completo
    val pedidoActual = pedido!! // Safe porque ya verificamos arriba

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painterResource(R.drawable.logo), null, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("PED-${pedidoActual.id} — ${getEstadoLabel(pedidoActual.estado).uppercase()}")
                    }
                }
            )
        }
    ) { pv ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta con informacion general del pedido
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Información del Pedido",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(Modifier.height(12.dp))
                        
                        DetailRow("Fecha:", formatFechaHora(pedidoActual.fecha))
                        DetailRow("Cliente:", pedidoActual.usuario?.correo?.ifEmpty { "—" } ?: "—")
                        
                        // Construir direccion completa de envio
                        val direccionCompleta = buildString {
                            val dir = pedidoActual.direccion?.trim()
                            val com = pedidoActual.comuna?.trim()
                            val reg = pedidoActual.region?.trim()
                            
                            if (!dir.isNullOrEmpty()) append(dir)
                            if (!com.isNullOrEmpty()) {
                                if (isNotEmpty()) append(", ")
                                append(com)
                            }
                            if (!reg.isNullOrEmpty()) {
                                if (isNotEmpty()) append(", ")
                                append(reg)
                            }
                        }.ifEmpty { "—" }
                        
                        DetailRow("Dirección:", direccionCompleta)
                        DetailRow("Estado:", getEstadoLabel(pedidoActual.estado))
                    }
                }
            }
            
            // Lista de productos incluidos en el pedido
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Ítems del Pedido",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(Modifier.height(12.dp))
                        
                        if (pedidoActual.items.isEmpty()) {
                            Text(
                                "Sin ítems encontrados",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            pedidoActual.items.forEach { item ->
                                ItemPedidoRow(item)
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
            
            // Tarjeta con el resumen de totales (subtotal, descuentos, total)
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Resumen de Totales",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(Modifier.height(12.dp))
                        
                        // Subtotal
                        if (pedidoActual.subtotal != null && pedidoActual.subtotal > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Subtotal:",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    formatCLP(pedidoActual.subtotal),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                        }
                        
                        // Descuento Duoc (si existe)
                        if (pedidoActual.descuentoDuoc != null && pedidoActual.descuentoDuoc > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Descuento Duoc (20%):",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                Text(
                                    "-${formatCLP(pedidoActual.descuentoDuoc)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                        }
                        
                        // Descuento por puntos (si existe)
                        if (pedidoActual.descuentoPuntos != null && pedidoActual.descuentoPuntos > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Descuento con puntos:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                Text(
                                    "-${formatCLP(pedidoActual.descuentoPuntos)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                        
                        // Divisor
                        HorizontalDivider()
                        Spacer(Modifier.height(8.dp))
                        
                        // Total final
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Total Final:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                formatCLP(pedidoActual.total),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
            
            // Botones de accion (solo si tiene permisos para cambiar estado)
            if (canChangeStatus) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Marcar pedido como despachado
                        Button(
                            onClick = {
                                if (pedidoActual.estado.uppercase() == "PENDIENTE") {
                                    vm.setEstadoPedido(
                                        id = pedidoActual.id,
                                        estado = "DESPACHADO",
                                        onSuccess = {
                                            successMsg = "Pedido marcado como despachado"
                                            showSuccessDialog = true
                                            // Recargar pedido
                                            vm.cargarPedidoById(
                                                id = pedidoActual.id,
                                                onSuccess = { pedido = it },
                                                onError = { }
                                            )
                                        },
                                        onError = { error ->
                                            errorMsg = error
                                            showErrorDialog = true
                                        }
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = pedidoActual.estado.uppercase() == "PENDIENTE" && !isLoading,
                            colors = if (pedidoActual.estado.uppercase() == "PENDIENTE") {
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            } else {
                                ButtonDefaults.buttonColors()
                            }
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(Modifier.width(8.dp))
                            }
                            Text(
                                when (pedidoActual.estado.uppercase()) {
                                    "PENDIENTE" -> "Marcar como despachado"
                                    "DESPACHADO" -> "Pedido despachado"
                                    else -> "Pedido cancelado"
                                }
                            )
                        }
                        
                        // Generar boleta para el pedido (solo pendientes o despachados)
                        if (pedidoActual.estado.uppercase() in listOf("PENDIENTE", "DESPACHADO")) {
                            OutlinedButton(
                                onClick = {
                                    android.util.Log.d("DetallePedido", "Generando/obteniendo boleta para pedido ${pedidoActual.id}")
                                    vm.generarBoleta(
                                        pedidoId = pedidoActual.id,
                                        onSuccess = { boletaNueva ->
                                            android.util.Log.d("DetallePedido", "Boleta obtenida: ${boletaNueva.numero}")
                                            val numeroEncoded = java.net.URLEncoder.encode(boletaNueva.numero, "UTF-8")
                                            nav.navigate("detalle_boleta/$numeroEncoded")
                                        },
                                        onError = { genError ->
                                            android.util.Log.e("DetallePedido", "Error al generar/obtener boleta: $genError")
                                            errorMsg = genError
                                            showErrorDialog = true
                                        }
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading
                            ) {
                                Icon(Icons.Default.Receipt, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Generar boleta")
                            }
                        }
                        
                        // Volver a la lista de pedidos
                        OutlinedButton(
                            onClick = { nav.popBackStack() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Volver a pedidos")
                        }
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
            text = { Text(errorMsg.ifEmpty { "Error al actualizar el estado del pedido" }) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

// Componente para mostrar una fila con etiqueta y valor
@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.6f)
        )
    }
}

// Componente para mostrar un producto dentro del pedido
@Composable
private fun ItemPedidoRow(item: com.example.levelupgamerpanel_app.data.models.ItemPedido) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.nombreProducto,  // Campo actualizado del modelo
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (item.productoId != null) {
                    Text(
                        text = "Código: ${item.productoId}",  // Campo actualizado del modelo
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(Modifier.width(8.dp))
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatCLP(item.precio),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "x${item.cantidad}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatCLP(item.precio * item.cantidad),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}