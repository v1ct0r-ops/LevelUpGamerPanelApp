@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.levelupgamerpanel_app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
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
import com.example.levelupgamerpanel_app.data.models.ItemPedido
import com.example.levelupgamerpanel_app.data.models.Producto
import com.example.levelupgamerpanel_app.ui.navigation.Routes
import kotlinx.coroutines.launch

// Pantalla principal del dashboard con metricas y navegacion
@Composable
fun HomeScreen(nav: NavController, vm: AppViewModel = viewModel()) {
    val usuarios  by vm.usuarios.collectAsState()
    val productos by vm.productos.collectAsState()
    val pedidos   by vm.pedidos.collectAsState()
    val usuarioActual by vm.usuarioActual.collectAsState()

    // Calcular metricas principales del negocio
    val totalUsuarios   = usuarios.size
    val totalProductos  = productos.size
    val inventarioTotal = productos.sumOf { it.stock }
    val pendientes      = pedidos.count { it.estado.equals("pendiente", true) }

    // Contar cuantas veces se ha vendido cada producto
    val conteoPorProducto: Map<String, Int> = remember(pedidos) {
        pedidos.flatMap { it.items }
            .groupBy { it.productoId ?: "" }  // Cambiar de ItemPedido::idProducto a lambda con productoId
            .mapValues { (_, items) -> items.sumOf { it.cantidad } }
    }
    // Obtener los 5 productos mas vendidos
    val topVendidos: List<Pair<Producto, Int>> = remember(productos, conteoPorProducto) {
        conteoPorProducto.entries
            .sortedByDescending { it.value }
            .mapNotNull { (id, cant) -> productos.firstOrNull { it.codigo == id }?.let { it to cant } }  // Cambiar .id a .codigo
            .take(5)
    }
    // Producto con menor cantidad de stock
    val menosStock: Producto? = remember(productos) { productos.minByOrNull { it.stock } }
    // Productos con stock bajo (1 a 4 unidades)
    val stockBajo: List<Producto> = remember(productos) { productos.filter { it.stock in 1..4 }.take(5) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isOpen = drawerState.isOpen

    // Verificar si el usuario actual es administrador
    val isAdmin = usuarioActual?.tipoUsuario == "ADMIN"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(8.dp))
                DrawerHeader()
                // Navegacion a pantalla de productos
                NavigationDrawerItem(
                    label = { Text("Productos", style = MaterialTheme.typography.titleLarge) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; nav.navigate(Routes.Productos) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                // Navegacion a pantalla de pedidos
                NavigationDrawerItem(
                    label = { Text("Pedidos", style = MaterialTheme.typography.titleLarge) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; nav.navigate(Routes.Pedidos) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                // Solo admin y vendedores pueden ver boletas
                if (isAdmin || usuarioActual?.tipoUsuario == "VENDEDOR") {
                    NavigationDrawerItem(
                        label = { Text("Boletas", style = MaterialTheme.typography.titleLarge) },
                        selected = false,
                        onClick = { scope.launch { drawerState.close() }; nav.navigate(Routes.Boletas) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }

                // Solo administradores pueden ver usuarios
                if (isAdmin) {
                    NavigationDrawerItem(
                        label = { Text("Usuarios", style = MaterialTheme.typography.titleLarge) },
                        selected = false,
                        onClick = { scope.launch { drawerState.close() }; nav.navigate(Routes.Usuarios) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }

                // Navegacion a pantalla de Pokemon (API externa)
                NavigationDrawerItem(
                    label = { Text("Pokémon", style = MaterialTheme.typography.titleLarge) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; nav.navigate(Routes.Games) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Spacer(Modifier.height(12.dp), )
                HorizontalDivider()
                // Cerrar sesion y volver al login
                NavigationDrawerItem(
                    label = { Text("Salir", style = MaterialTheme.typography.titleLarge) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        vm.logout()
                        nav.navigate(Routes.Login) { popUpTo(0) }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "Logo",
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text("Panel")
                        }
                    },
                    actions = {
                        // Boton para abrir/cerrar el menu lateral
                        IconButton(onClick = {
                            scope.launch { if (drawerState.isOpen) drawerState.close() else drawerState.open() }
                        }) {
                            Icon(if (isOpen) Icons.Default.Close else Icons.Default.Menu, null)
                        }
                    }
                )
            }
        ) { pv ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                contentPadding = PaddingValues(
                    top = pv.calculateTopPadding() + 16.dp,
                    bottom = pv.calculateBottomPadding() + 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Mensaje de bienvenida con nombre del usuario
                item {
                    val nombre = listOfNotNull(usuarioActual?.nombres, usuarioActual?.apellidos)
                        .filter { it.isNotBlank() }
                        .joinToString(" ")
                        .ifBlank { usuarioActual?.correo ?: "Usuario" }
                    Text("Bienvenid@, $nombre!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                }
                // Primera fila de metricas (Usuarios y Productos)
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("Usuarios", totalUsuarios.toString(), Modifier.weight(1f, ))
                        StatCard("Productos", totalProductos.toString(), Modifier.weight(1f))
                    }
                }
                // Segunda fila de metricas (Inventario y Pedidos Pendientes)
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("Inventario", inventarioTotal.toString(), Modifier.weight(1f))
                        StatCard("Pendientes", pendientes.toString(), Modifier.weight(1f))
                    }
                }
                // Seccion de productos mas vendidos
                item {
                    SectionCard("Productos más vendidos") {
                        if (topVendidos.isEmpty()) {
                            Text("Sin ventas aún", style = MaterialTheme.typography.bodyMedium)
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                topVendidos.forEach { (p, cant) ->
                                    ListItem(
                                        headlineContent = { Text(p.nombre, style = MaterialTheme.typography.bodyMedium) },
                                        supportingContent = { Text("Vendidos: $cant • Stock: ${p.stock} • \$${p.precio}") }
                                    )
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
                // Seccion del producto con menor stock
                item {
                    SectionCard("Producto con menos stock") {
                        val ms = menosStock
                        if (ms == null) {
                            Text("Sin productos", style = MaterialTheme.typography.bodyMedium)
                        } else {
                            ListItem(
                                headlineContent = { Text(ms.nombre, style = MaterialTheme.typography.bodyMedium) },
                                supportingContent = { Text("Stock: ${ms.stock} • \$${ms.precio} • ${ms.categoria}") }
                            )
                        }
                    }
                }
                // Seccion de productos con stock bajo (alerta de reposicion)
                item {
                    SectionCard("Stock bajo (≤ 4)") {
                        if (stockBajo.isEmpty()) {
                            Text("Todo OK en inventario", style = MaterialTheme.typography.bodyMedium)
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                stockBajo.forEach { p ->
                                    ListItem(
                                        headlineContent = { Text(p.nombre, style = MaterialTheme.typography.bodyMedium) },
                                        supportingContent = { Text("Stock: ${p.stock} • \$${p.precio}") },
                                        trailingContent = { AssistChip(onClick = {}, label = { Text("Reponer") }) }
                                    )
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Componente del encabezado del menu lateral
@Composable private fun DrawerHeader() {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text("Navegación", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
    }
}

// Componente de tarjeta para mostrar metricas estadisticas
@Composable private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.Start) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        }
    }
}

// Componente de tarjeta para secciones con titulo y contenido personalizado
@Composable private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}
