package com.example.levelupgamerpanel_app.data.models

import com.google.gson.annotations.SerializedName

// Modelos de datos para la PokeAPI
// Estas clases representan la estructura JSON que devuelve la API

// Respuesta que trae la lista de Pokemon con paginacion
data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonBasic>
) {
    // Convierte la lista basica a objetos Pokemon completos
    val pokemons: List<Pokemon>
        get() = results.map { Pokemon(it.name, it.url) }
}

// Datos basicos de un Pokemon en la lista
data class PokemonBasic(
    val name: String,
    val url: String
)

// Modelo principal de un Pokemon
data class Pokemon(
    val name: String,
    val url: String,
    var types: List<PokemonType>? = null,
    var baseStats: Int? = null
) {
    // Extraer el ID del Pokemon desde la URL
    val id: Int
        get() = url.trimEnd('/').split("/").last().toIntOrNull() ?: 0
    
    // Numero de Pokedex formateado
    val pokedexNumber: String?
        get() = "Pokédex #${id}"
    
    // URL de la imagen del Pokemon
    val spriteUrl: String?
        get() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${id}.png"
    
    // Rating del Pokemon basado en sus stats
    val rating: Double
        get() = baseStats?.toDouble()?.div(100) ?: 0.0
    
    // Tipos del Pokemon en formato de texto
    val typesString: String
        get() = types?.joinToString(" / ") { 
            translateType(it.type.name)
        } ?: "Cargando..."
    
    // Traducir tipos de ingles a espanol
    private fun translateType(type: String): String {
        return when(type.lowercase()) {
            "normal" -> "Normal"
            "fire" -> "Fuego"
            "water" -> "Agua"
            "grass" -> "Planta"
            "electric" -> "Eléctrico"
            "ice" -> "Hielo"
            "fighting" -> "Lucha"
            "poison" -> "Veneno"
            "ground" -> "Tierra"
            "flying" -> "Volador"
            "psychic" -> "Psíquico"
            "bug" -> "Bicho"
            "rock" -> "Roca"
            "ghost" -> "Fantasma"
            "dark" -> "Siniestro"
            "dragon" -> "Dragón"
            "steel" -> "Acero"
            "fairy" -> "Hada"
            else -> type.replaceFirstChar { it.uppercase() }
        }
    }
    
    // Determinar la generacion del Pokemon segun su ID
    val generation: Int
        get() = when {
            id <= 151 -> 1
            id <= 251 -> 2
            id <= 386 -> 3
            id <= 493 -> 4
            id <= 649 -> 5
            id <= 721 -> 6
            id <= 809 -> 7
            id <= 905 -> 8
            else -> 9
        }
}

// Tipo de Pokemon (Fuego, Agua, Planta, etc)
data class PokemonType(
    val slot: Int,
    val type: NamedResource
)

// Recurso generico con nombre y URL
data class NamedResource(
    val name: String,
    val url: String
)

// Detalles completos de un Pokemon
data class PokemonDetail(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val sprites: Sprites,
    val types: List<PokemonType>,
    val abilities: List<AbilitySlot>,
    val stats: List<StatSlot>
) {
    // Suma total de todas las estadisticas base
    val baseStatsTotal: Int
        get() = stats.sumOf { it.baseStat }
    
    // Descripcion con altura, peso y stats
    val description: String
        get() = "Altura: ${height/10.0}m | Peso: ${weight/10.0}kg | Stats: ${baseStatsTotal}"
    
    // URL de la imagen del Pokemon
    val spriteUrl: String?
        get() = sprites.frontDefault
    
    // Rating calculado desde las stats
    val rating: Double
        get() = baseStatsTotal.toDouble() / 100
}

// Sprites del Pokemon (imagenes)
data class Sprites(
    @SerializedName("front_default") val frontDefault: String?
)

// Habilidad del Pokemon
data class AbilitySlot(
    val slot: Int,
    val ability: NamedResource
)

// Estadistica del Pokemon (HP, Ataque, Defensa, etc)
data class StatSlot(
    @SerializedName("base_stat") val baseStat: Int,
    val stat: NamedResource
)
