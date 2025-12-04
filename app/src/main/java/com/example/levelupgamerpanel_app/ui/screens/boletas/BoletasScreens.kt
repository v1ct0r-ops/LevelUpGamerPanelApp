@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.levelupgamerpanel_app.ui.screens.boletas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
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
import com.example.levelupgamerpanel_app.data.models.Boleta
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// Formatear numeros a pesos chilenos (CLP)
private fun formatCLP(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}

// Formatear fecha ISO (dd/MM/yyyy HH:mm:ss)
private fun formatFechaHora(fechaIso: String): String {
    return try {
        val instant = java.time.Instant.parse(fechaIso)
        val formatter = java.time.format.DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm:ss")
            .withZone(java.time.ZoneId.systemDefault())
        formatter.format(instant)
    } catch (e: Exception) {
        try {
            val sdfInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = sdfInput.parse(fechaIso)
            val sdfOutput = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("es", "CL"))
            sdfOutput.format(date ?: Date())
        } catch (e2: Exception) {
            fechaIso
        }
    }
}

// Pantalla principal que muestra la lista de boletas emitidas
@Composable
fun BoletasScreen(nav: NavController, vm: AppViewModel = viewModel()) {
    val boletas by vm.boletas.collectAsState()
    val usuarioActual by vm.usuarioActual.collectAsState()
    val errorMessage by vm.errorMessage.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    
    var showErrorDialog by remember { mutableStateOf(false) }
    var currentError by remember { mutableStateOf<String?>(null) }
    
    // Cargar todas las boletas desde el backend al abrir la pantalla
    LaunchedEffect(Unit) {
        vm.cargarBoletas()
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
                        Text("Boletas Emitidas")
                    }
                }
            )
        }
    ) { pv ->
        Column(Modifier.padding(pv).padding(12.dp)) {
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(8.dp))
                        Text("Cargando boletas desde el backend...")
                    }
                }
            } else if (boletas.isEmpty()) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "📄 No hay boletas emitidas.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(16.dp))
                        OutlinedButton(onClick = { nav.navigate("pedidos") }) {
                            Text("Ir a Pedidos")
                        }
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(boletas) { boleta ->
                        BoletaCard(
                            boleta = boleta,
                            onVerBoleta = {
                                nav.navigate("detalle_boleta/${java.net.URLEncoder.encode(boleta.numero, "UTF-8")}")
                            },
                            onVerPedido = {
                                nav.navigate("detalle_pedido/${boleta.pedidoId}")
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

// Tarjeta individual que muestra informacion de una boleta
@Composable
private fun BoletaCard(
    boleta: Boleta,
    onVerBoleta: () -> Unit,
    onVerPedido: () -> Unit
) {
    // Obtener el nombre del cliente desde el pedido o campo cliente
    val nombreCliente = when {
        boleta.pedido?.usuario != null -> {
            val u = boleta.pedido.usuario
            "${u.nombres} ${u.apellidos}".trim().ifEmpty { u.correo }
        }
        !boleta.cliente.isNullOrEmpty() -> boleta.cliente
        else -> "Cliente no disponible"
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Número de boleta
            Text(
                text = boleta.numero,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(Modifier.height(8.dp))
            
            // Fecha
            Text(
                text = "Fecha: ${formatFechaHora(boleta.fecha)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Cliente
            Text(
                text = "Cliente: $nombreCliente",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Pedido relacionado
            Text(
                text = "Pedido: PED-${boleta.pedidoId}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(Modifier.height(8.dp))
            
            // Total
            Text(
                text = "Total: ${formatCLP(boleta.total)}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(Modifier.height(12.dp))
            
            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onVerPedido,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Ver Pedido")
                }
                
                Button(
                    onClick = onVerBoleta,
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

// Pantalla que muestra el detalle completo de una boleta especifica
@Composable
fun DetalleBoletaScreen(nav: NavController, vm: AppViewModel = viewModel()) {
    val usuarioActual by vm.usuarioActual.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    
    // Obtener numero de boleta desde la navegacion (URL decode)
    val numeroString = nav.currentBackStackEntry?.arguments?.getString("numero")
    val numero = numeroString?.let { java.net.URLDecoder.decode(it, "UTF-8") }
    
    // Estados para manejar la carga de la boleta
    var boleta by remember { mutableStateOf<Boleta?>(null) }
    var loadingBoleta by remember { mutableStateOf(true) }
    var errorCarga by remember { mutableStateOf<String?>(null) }
    
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    
    val tipo = usuarioActual?.tipoUsuario ?: ""
    val isAdmin = tipo.uppercase() == "ADMIN"
    
    // Cargar boleta especifica desde el backend usando el numero
    LaunchedEffect(numero) {
        android.util.Log.d("DetalleBoleta", "=== INICIANDO CARGA DE BOLETA ===")
        android.util.Log.d("DetalleBoleta", "Número: $numero")
        
        if (numero == null) {
            android.util.Log.e("DetalleBoleta", "Número inválido")
            errorCarga = "Número de boleta inválido"
            loadingBoleta = false
            return@LaunchedEffect
        }
        
        loadingBoleta = true
        errorCarga = null
        
        vm.cargarBoletaByNumero(
            numero = numero,
            onSuccess = { boletaCargada ->
                android.util.Log.d("DetalleBoleta", "✅ Boleta cargada exitosamente: ${boletaCargada.numero}")
                boleta = boletaCargada
                loadingBoleta = false
            },
            onError = { error ->
                android.util.Log.e("DetalleBoleta", "❌ Error al cargar boleta: $error")
                errorCarga = error
                loadingBoleta = false
                boleta = null
            }
        )
    }
    
    // Mostrar pantalla de carga mientras se obtiene la boleta
    if (loadingBoleta) {
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
                    Text("Cargando boleta desde el backend...")
                }
            }
        }
        return
    }
    
    // Mostrar error si no se pudo cargar la boleta
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
                        text = "Boleta no encontrada",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Número: ${numeroString ?: "inválido"}",
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
                        Text("Volver a boletas")
                    }
                }
            }
        }
        return
    }
    
    // Si no hay boleta disponible (caso raro)
    if (boleta == null) {
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
                        text = "Boleta no encontrada",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { nav.popBackStack() }) {
                        Text("Volver a boletas")
                    }
                }
            }
        }
        return
    }
    
    // Aqui boleta ya tiene datos, mostrar el detalle completo
    val boletaActual = boleta!!
    
    // Obtener el nombre del cliente
    val nombreCliente = when {
        boletaActual.pedido?.usuario != null -> {
            val u = boletaActual.pedido.usuario
            "${u.nombres} ${u.apellidos}".trim().ifEmpty { u.correo }
        }
        !boletaActual.cliente.isNullOrEmpty() -> boletaActual.cliente
        else -> "Cliente no disponible"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painterResource(R.drawable.logo), null, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Boleta ${boletaActual.numero}")
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
            // Tarjeta con informacion general de la boleta
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Información de la Boleta",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(Modifier.height(12.dp))
                        
                        DetailRow("Número:", boletaActual.numero)
                        DetailRow("Fecha Emisión:", formatFechaHora(boletaActual.fecha))
                        DetailRow("Cliente:", nombreCliente)
                        DetailRow("Pedido:", "PED-${boletaActual.pedidoId}")
                        DetailRow("Total:", formatCLP(boletaActual.total))
                    }
                }
            }
            
            // Lista de productos incluidos en la boleta
            if (boletaActual.pedido != null && boletaActual.pedido.items.isNotEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "Detalle de Productos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(Modifier.height(12.dp))
                            
                            boletaActual.pedido.items.forEach { item ->
                                ItemBoletaRow(
                                    nombre = item.nombreProducto,
                                    codigo = item.productoId ?: "",
                                    precio = item.precio,
                                    cantidad = item.cantidad
                                )
                                Spacer(Modifier.height(8.dp))
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
                            
                            // Mostrar subtotal si existe
                            if (boletaActual.pedido.subtotal != null && boletaActual.pedido.subtotal > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Subtotal:", style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        formatCLP(boletaActual.pedido.subtotal),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                            }
                            
                            // Mostrar descuento Duoc si existe
                            if (boletaActual.pedido.descuentoDuoc != null && boletaActual.pedido.descuentoDuoc > 0) {
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
                                        "-${formatCLP(boletaActual.pedido.descuentoDuoc)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                            }
                            
                            // Mostrar descuento por puntos si existe
                            if (boletaActual.pedido.descuentoPuntos != null && boletaActual.pedido.descuentoPuntos > 0) {
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
                                        "-${formatCLP(boletaActual.pedido.descuentoPuntos)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                            
                            // Linea divisora
                            HorizontalDivider()
                            Spacer(Modifier.height(8.dp))
                            
            // Mostrar total final de la boleta
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
                                    formatCLP(boletaActual.total),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
            
            // Botones para navegar a otras pantallas
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Ir al pedido relacionado con esta boleta
                    OutlinedButton(
                        onClick = { nav.navigate("detalle_pedido/${boletaActual.pedidoId}") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ver Pedido Relacionado")
                    }
                    
                    // Volver a la lista de boletas
                    OutlinedButton(
                        onClick = { nav.popBackStack() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Volver a Boletas")
                    }
                }
            }
        }
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

// Componente para mostrar un producto dentro de la boleta
@Composable
private fun ItemBoletaRow(
    nombre: String,
    codigo: String,
    precio: Double,
    cantidad: Int
) {
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
                    text = nombre,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (codigo.isNotEmpty()) {
                    Text(
                        text = "Código: $codigo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(Modifier.width(8.dp))
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatCLP(precio),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "x$cantidad",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatCLP(precio * cantidad),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
