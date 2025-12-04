package com.example.levelupgamerpanel_app.data.repository

import com.example.levelupgamerpanel_app.data.api.PokeApiClient
import com.example.levelupgamerpanel_app.data.models.Pokemon

// Repositorio para obtener datos de Pokemon desde PokeAPI
class PokemonRepository {
    
    private val apiService = PokeApiClient.apiService
    
    // Obtener lista de Pokemon con paginacion (20 por pagina)
    suspend fun getPokemonList(page: Int = 1): Result<List<Pokemon>> {
        return try {
            // Calcular desde donde empezar a traer datos segun la pagina
            val offset = (page - 1) * 20
            val response = apiService.getPokemons(limit = 20, offset = offset)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.pokemons)
            } else {
                val errorMsg = when (response.code()) {
                    404 -> "Endpoint no encontrado"
                    500 -> "Error del servidor PokeAPI"
                    else -> "Error ${response.code()}: ${response.message()}"
                }
                android.util.Log.e("PokemonRepository", errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            android.util.Log.e("PokemonRepository", "Error de conexión: ${e.message}", e)
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
    
    // Obtener 50 Pokemon aleatorios con todos sus detalles
    suspend fun getRandomPokemonWithDetails(page: Int = 1): Result<List<Pokemon>> {
        return try {
            // Generar 50 IDs aleatorios entre 1 y 1025 (todos los Pokemon que existen)
            val totalPokemon = 1025
            val randomIds = (1..totalPokemon).shuffled().take(50)
            
            // Cargar los detalles completos de cada Pokemon aleatorio
            val pokemonWithDetails = randomIds.mapNotNull { id ->
                try {
                    val detailResponse = apiService.getPokemonDetail(id.toString())
                    if (detailResponse.isSuccessful && detailResponse.body() != null) {
                        val detail = detailResponse.body()!!
                        // Crear objeto Pokemon con nombre, tipos y estadisticas
                        Pokemon(
                            name = detail.name,
                            url = "https://pokeapi.co/api/v2/pokemon/${detail.id}/",
                            types = detail.types,
                            baseStats = detail.baseStatsTotal
                        )
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    android.util.Log.w("PokemonRepository", "Error cargando Pokemon #${id}: ${e.message}")
                    null
                }
            }
            
            Result.success(pokemonWithDetails)
        } catch (e: Exception) {
            android.util.Log.e("PokemonRepository", "Error general: ${e.message}", e)
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}
