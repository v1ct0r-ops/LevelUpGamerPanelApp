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

@Composable
fun ProductosScreen(nav: NavController, vm: AppViewModel = viewModel()){
    val productos by vm.productos.collectAsState()
    var query by remember { mutableStateOf("") }
    val list = productos.filter { it.nombre.contains(query, true) }

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
                    IconButton(onClick = { nav.navigate(Routes.NuevoProducto) }){
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            )
        }
    ){ pv ->
        Column(Modifier.padding(pv).padding(12.dp)) {
            OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Buscar") }, modifier = Modifier.fillMaxWidth())
            LazyColumn {
                items(list){ p -> ProductoRow(p, onEdit = { nav.navigate("editar_producto/${p.id}") }, onDelete = { vm.removeProducto(p.id) }) }
            }
        }
    }
}

@Composable private fun ProductoRow(p: Producto, onEdit:()->Unit, onDelete:()->Unit){
    Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(p.nombre, style = MaterialTheme.typography.titleLarge)
                if (p.stock < 5) AssistChip(onClick = {}, label = { Text("Bajo stock") })
            }
            Text("$${p.precio} | stock ${p.stock} | ${p.categoria}", style = MaterialTheme.typography.bodyMedium)
            AnimatedVisibility(p.imagenUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(p.imagenUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(120.dp).padding(top = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onEdit) { Text("Editar") }
                TextButton(onClick = onDelete) { Text("Eliminar") }
            }
        }
    }
}

// ---------------------------------------------------------
// Pantalla para nuevo producto
// ---------------------------------------------------------
@Composable
fun NuevoProductoScreen(nav: NavController, vm: AppViewModel = viewModel()) {
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var err by remember { mutableStateOf<String?>(null) }

    fun validar(): Boolean {
        if (nombre.length < 3) { err = "Nombre mínimo 3 caracteres"; return false }
        val p = precio.toIntOrNull() ?: return false.also { err = "Precio inválido" }
        err = null
        return true
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painterResource(R.drawable.logo), null, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Nuevo producto", style = MaterialTheme.typography.titleLarge)
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
            item { OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(precio, { precio = it }, label = { Text("Precio (CLP)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(stock, { stock = it }, label = { Text("Stock") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(categoria, { categoria = it }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth()) }
            item { if (err != null) Text(err!!, color = MaterialTheme.colorScheme.error) }
            item {
                Button(
                    onClick = {
                        if (!validar()) return@Button
                        vm.addProducto(
                            Producto(
                                id = "P${System.currentTimeMillis()}",
                                nombre = nombre,
                                precio = precio.toInt(),
                                stock = stock.toIntOrNull() ?: 0,
                                categoria = categoria
                            )
                        )
                        nav.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Guardar") }
            }
        }
    }
}

// ---------------------------------------------------------
// Pantalla para editar producto
// ---------------------------------------------------------
@Composable
fun EditarProductoScreen(nav: NavController, vm: AppViewModel = viewModel()) {
    val productos by vm.productos.collectAsState()
    val id = nav.currentBackStackEntry?.arguments?.getString("id") ?: return
    val p = productos.firstOrNull { it.id == id } ?: return

    var nombre by remember { mutableStateOf(p.nombre) }
    var precio by remember { mutableStateOf(p.precio.toString()) }
    var stock by remember { mutableStateOf(p.stock.toString()) }
    var categoria by remember { mutableStateOf(p.categoria) }
    var err by remember { mutableStateOf<String?>(null) }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painterResource(R.drawable.logo), null, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Editar producto", style = MaterialTheme.typography.titleLarge)
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
            item { OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(precio, { precio = it }, label = { Text("Precio (CLP)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(stock, { stock = it }, label = { Text("Stock") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(categoria, { categoria = it }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth()) }
            item { if (err != null) Text(err!!, color = MaterialTheme.colorScheme.error) }
            item {
                Button(
                    onClick = {
                        val pr = precio.toIntOrNull() ?: return@Button.also { err = "Precio inválido" }
                        vm.updateProducto(
                            p.copy(
                                nombre = nombre,
                                precio = pr,
                                stock = stock.toIntOrNull() ?: 0,
                                categoria = categoria
                            )
                        )
                        nav.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Guardar cambios") }
            }
        }
    }
}