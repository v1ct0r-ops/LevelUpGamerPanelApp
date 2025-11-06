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

@Composable
fun HomeScreen(nav: NavController, vm: AppViewModel = viewModel()) {
    val usuarios  by vm.usuarios.collectAsState()
    val productos by vm.productos.collectAsState()
    val pedidos   by vm.pedidos.collectAsState()
    val usuarioActual by vm.usuarioActual.collectAsState()

    val totalUsuarios   = usuarios.size
    val totalProductos  = productos.size
    val inventarioTotal = productos.sumOf { it.stock }
    val pendientes      = pedidos.count { it.estado.equals("pendiente", true) }

    val conteoPorProducto: Map<String, Int> = remember(pedidos) {
        pedidos.flatMap { it.items }
            .groupBy(ItemPedido::idProducto)
            .mapValues { (_, items) -> items.sumOf { it.cantidad } }
    }
    val topVendidos: List<Pair<Producto, Int>> = remember(productos, conteoPorProducto) {
        conteoPorProducto.entries
            .sortedByDescending { it.value }
            .mapNotNull { (id, cant) -> productos.firstOrNull { it.id == id }?.let { it to cant } }
            .take(5)
    }
    val menosStock: Producto? = remember(productos) { productos.minByOrNull { it.stock } }
    val stockBajo: List<Producto> = remember(productos) { productos.filter { it.stock in 1..4 }.take(5) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isOpen = drawerState.isOpen

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(8.dp))
                DrawerHeader()
                NavigationDrawerItem(
                    label = { Text("Productos", style = MaterialTheme.typography.titleLarge) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; nav.navigate(Routes.Productos) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Pedidos", style = MaterialTheme.typography.titleLarge) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; nav.navigate(Routes.Pedidos) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Usuarios", style = MaterialTheme.typography.titleLarge) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; nav.navigate(Routes.Usuarios) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(Modifier.height(12.dp), )
                HorizontalDivider()
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
                // Bienvenida
                item {
                    val nombre = listOfNotNull(usuarioActual?.nombres, usuarioActual?.apellidos)
                        .filter { it.isNotBlank() }
                        .joinToString(" ")
                        .ifBlank { usuarioActual?.correo ?: "Usuario" }
                    Text("Bienvenid@, $nombre!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                }
                // Métricas fila 1
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("Usuarios", totalUsuarios.toString(), Modifier.weight(1f, ))
                        StatCard("Productos", totalProductos.toString(), Modifier.weight(1f))
                    }
                }
                // Métricas fila 2
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("Inventario", inventarioTotal.toString(), Modifier.weight(1f))
                        StatCard("Pendientes", pendientes.toString(), Modifier.weight(1f))
                    }
                }
                // Top vendidos
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
                // Menor stock
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
                // Stock bajo
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

@Composable private fun DrawerHeader() {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text("Navegación", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.Start) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}
