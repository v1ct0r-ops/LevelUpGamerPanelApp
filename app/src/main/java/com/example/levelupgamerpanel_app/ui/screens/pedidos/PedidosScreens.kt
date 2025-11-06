@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.levelupgamerpanel_app.ui.screens.pedidos

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
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

@Composable
fun PedidosScreen(nav: NavController, vm: AppViewModel = viewModel()){
    val pedidos by vm.pedidos.collectAsState()
    var tab by remember { mutableStateOf(0) }
    val estados = listOf("todos","pendiente","despachado","cancelado")
    val list = pedidos.filter { if (tab==0) true else it.estado == estados[tab] }

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
    ){ pv ->
        Column(Modifier.padding(pv)) {
            TabRow(selectedTabIndex = tab) {
                estados.forEachIndexed { i, s ->
                    Tab(selected = tab==i, onClick = { tab = i }, text = { Text(s.replaceFirstChar{ it.uppercase() }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold) })
                }
            }
            LazyColumn(Modifier.padding(12.dp)) {
                items(list){ p -> PedidoRow(p, onOpen = { nav.navigate("detalle_pedido/${p.id}") }) }
            }
        }
    }
}

@Composable private fun PedidoRow(p: Pedido, onOpen:()->Unit){
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        onClick = onOpen
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("Pedido ${p.id}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text("Total $${p.total} • ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(java.util.Date(p.fecha))}", style = MaterialTheme.typography.bodyMedium)
            AssistChip(onClick = {}, label = { Text(p.estado) })
        }
    }
}

@Composable
fun DetallePedidoScreen(nav: NavController, vm: AppViewModel = viewModel()) {
    val pedidos by vm.pedidos.collectAsState()
    val id = nav.currentBackStackEntry?.arguments?.getString("id") ?: return
    val p = pedidos.firstOrNull { it.id == id } ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painterResource(R.drawable.logo), null, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Pedido ${p.id}")
                    }
                }
            )
        }
    ) { pv ->
        LazyColumn(
            modifier = Modifier
                .padding(pv)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Lista de productos en el pedido
            items(p.items) { item ->
                ListItem(
                    headlineContent = {
                        Text(
                            "${item.nombre} × ${item.cantidad}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    supportingContent = { Text("$${item.precio}") }
                )
            }

            // Total
            item {
                ListItem(
                    headlineContent = {
                        Text(
                            "Total",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    supportingContent = { Text("$${p.total}") }
                )
            }

            // Botones de acción
            item {
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            vm.setEstadoPedido(p.id, "despachado")
                            nav.popBackStack()
                        }
                    ) { Text("Marcar despachado") }

                    OutlinedButton(
                        onClick = {
                            vm.setEstadoPedido(p.id, "cancelado")
                            nav.popBackStack()
                        }
                    ) { Text("Cancelar") }
                }
            }
        }
    }
}