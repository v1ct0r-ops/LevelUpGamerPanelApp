@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.levelupgamerpanel_app.ui.screens.pokemon

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.levelupgamerpanel_app.data.models.Pokemon
import com.example.levelupgamerpanel_app.data.repository.PokemonRepository
import kotlinx.coroutines.launch

// Pantalla que muestra una lista de 50 Pokemon aleatorios desde la PokeAPI
@Composable
fun PokemonScreen(nav: NavController) {
    val repo = remember { PokemonRepository() }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var pokemonList by remember { mutableStateOf<List<Pokemon>>(emptyList()) }
    val scope = rememberCoroutineScope()

    // Cargar Pokemon desde la API externa al abrir la pantalla
    LaunchedEffect(Unit) {
        isLoading = true
        error = null
        repo.getRandomPokemonWithDetails().onSuccess { 
            pokemonList = it
            isLoading = false
        }.onFailure { 
            error = it.message
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pokémon (API externa)") },
                // Boton para volver a la pantalla anterior
                navigationIcon = {
                    IconButton(onClick = { nav.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { pv ->
        Box(Modifier.fillMaxSize().padding(pv)) {
            when {
                // Mostrar mensaje de error si falla la conexion con PokeAPI
                error != null -> {
                    Column(
                        Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Error de conexión", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        Text(error ?: "Error desconocido", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(16.dp))
                        // Boton para volver a intentar cargar los Pokemon
                        Button(onClick = {
                            scope.launch {
                                isLoading = true
                                error = null
                                repo.getRandomPokemonWithDetails().onSuccess { 
                                    pokemonList = it
                                    isLoading = false
                                }.onFailure { 
                                    error = it.message
                                    isLoading = false
                                }
                            }
                        }) {
                            Text("Reintentar")
                        }
                    }
                }
                // Mostrar indicador de carga mientras se obtienen los datos
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                // Mostrar lista de Pokemon una vez cargados
                else -> {
                    if (pokemonList.isEmpty()) {
                        Text("No se encontraron pokémon", Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            Modifier.fillMaxSize().padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Mostrar cada Pokemon en una fila
                            items(pokemonList) { pokemon ->
                                PokemonRow(pokemon)
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

// Componente que muestra la informacion de un Pokemon individual
@Composable
private fun PokemonRow(pokemon: Pokemon) {
    Row(
        Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Lado izquierdo: nombre y tipos del Pokemon
        Column(Modifier.weight(1f)) {
            // Capitalizar primera letra del nombre
            Text(
                pokemon.name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            // Mostrar tipos traducidos al espanol
            Text(
                pokemon.typesString,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        // Lado derecho: numero de Pokedex y estadisticas base
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "#${pokemon.id}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(Modifier.height(4.dp))
            // Mostrar suma de estadisticas base o generacion
            if (pokemon.baseStats != null) {
                Text(
                    "⚡ ${pokemon.baseStats}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Medium
                )
            } else {
                Text(
                    "Gen ${pokemon.generation}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
