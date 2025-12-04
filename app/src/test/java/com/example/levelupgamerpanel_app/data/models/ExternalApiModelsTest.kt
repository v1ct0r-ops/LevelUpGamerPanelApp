package com.example.levelupgamerpanel_app.data.models

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests para ExternalApiModels (modelos de PokeAPI)
 * 
 * NOTA: Los nombres de las clases (Pokemon, PokemonDetail, PokemonListResponse) son alias
 * de compatibilidad pero en realidad representan datos de Pokémon de la PokeAPI.
 * 
 * Cobertura: propiedades computadas, traducciones, parsing de datos
 */
class ExternalApiModelsTest {
    
    // ===== TESTS DE POKEMON (clase Pokemon - 40% cobertura) =====
    
    @Test
    fun `Pokemon (Pokemon) extrae ID correctamente de la URL`() {
        val game1 = Pokemon("bulbasaur", "https://pokeapi.co/api/v2/pokemon/1/")
        val game25 = Pokemon("pikachu", "https://pokeapi.co/api/v2/pokemon/25/")
        val game151 = Pokemon("mew", "https://pokeapi.co/api/v2/pokemon/151/")
        val game905 = Pokemon("enamorus", "https://pokeapi.co/api/v2/pokemon/905/")
        
        assertEquals(1, game1.id)
        assertEquals(25, game25.id)
        assertEquals(151, game151.id)
        assertEquals(905, game905.id)
    }
    
    @Test
    fun `Pokemon extrae ID de URL sin slash final`() {
        val game = Pokemon("charmander", "https://pokeapi.co/api/v2/pokemon/4")
        assertEquals(4, game.id)
    }
    
    @Test
    fun `Pokemon con URL invalida retorna ID 0`() {
        val game = Pokemon("invalid", "https://example.com/invalid")
        assertEquals(0, game.id)
    }
    
    @Test
    fun `Pokemon genera backgroundImage correctamente`() {
        val game = Pokemon("pikachu", "https://pokeapi.co/api/v2/pokemon/25/")
        
        val expectedUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png"
        assertEquals(expectedUrl, game.spriteUrl)
    }
    
    @Test
    fun `Pokemon calcula rating desde baseStats`() {
        val gameConStats = Pokemon(
            name = "charizard",
            url = "https://pokeapi.co/api/v2/pokemon/6/",
            baseStats = 534
        )
        
        val gameSinStats = Pokemon(
            name = "magikarp",
            url = "https://pokeapi.co/api/v2/pokemon/129/",
            baseStats = null
        )
        
        assertEquals(5.34, gameConStats.rating, 0.01)
        assertEquals(0.0, gameSinStats.rating, 0.01)
    }
    
    @Test
    fun `Pokemon typesString traduce tipos correctamente al español`() {
        val game = Pokemon(
            name = "bulbasaur",
            url = "https://pokeapi.co/api/v2/pokemon/1/",
            types = listOf(
                PokemonType(1, NamedResource("grass", "url")),
                PokemonType(2, NamedResource("poison", "url"))
            )
        )
        
        val typesString = game.typesString
        
        assertTrue(typesString.contains("Planta"))
        assertTrue(typesString.contains("Veneno"))
        assertTrue(typesString.contains(" / "))
    }
    
    @Test
    fun `Pokemon typesString retorna Cargando cuando types es null`() {
        val game = Pokemon(
            name = "pikachu",
            url = "https://pokeapi.co/api/v2/pokemon/25/",
            types = null
        )
        
        assertEquals("Cargando...", game.typesString)
    }
    
    @Test
    fun `Pokemon typesString traduce todos los tipos de Pokemon`() {
        val tiposEsperados = mapOf(
            "fire" to "Fuego",
            "water" to "Agua",
            "grass" to "Planta",
            "electric" to "Eléctrico",
            "ice" to "Hielo",
            "fighting" to "Lucha",
            "poison" to "Veneno",
            "ground" to "Tierra",
            "flying" to "Volador",
            "psychic" to "Psíquico",
            "bug" to "Bicho",
            "rock" to "Roca",
            "ghost" to "Fantasma",
            "dark" to "Siniestro",
            "dragon" to "Dragón",
            "steel" to "Acero",
            "fairy" to "Hada",
            "normal" to "Normal"
        )
        
        tiposEsperados.forEach { (tipoIngles, tipoEspañol) ->
            val game = Pokemon(
                name = "test",
                url = "https://pokeapi.co/api/v2/pokemon/1/",
                types = listOf(PokemonType(1, NamedResource(tipoIngles, "url")))
            )
            
            assertEquals(tipoEspañol, game.typesString, "Tipo $tipoIngles debe traducirse a $tipoEspañol")
        }
    }
    
    @Test
    fun `Pokemon ratingsCount retorna generacion correcta Gen 1`() {
        val bulbasaur = Pokemon("bulbasaur", "https://pokeapi.co/api/v2/pokemon/1/")
        val mew = Pokemon("mew", "https://pokeapi.co/api/v2/pokemon/151/")
        
        assertEquals(1, bulbasaur.generation)
        assertEquals(1, mew.generation)
    }
    
    @Test
    fun `Pokemon ratingsCount retorna generacion correcta Gen 2`() {
        val chikorita = Pokemon("chikorita", "https://pokeapi.co/api/v2/pokemon/152/")
        val celebi = Pokemon("celebi", "https://pokeapi.co/api/v2/pokemon/251/")
        
        assertEquals(2, chikorita.generation)
        assertEquals(2, celebi.generation)
    }
    
    @Test
    fun `Pokemon ratingsCount retorna generacion correcta Gen 3`() {
        val treecko = Pokemon("treecko", "https://pokeapi.co/api/v2/pokemon/252/")
        val deoxys = Pokemon("deoxys", "https://pokeapi.co/api/v2/pokemon/386/")
        
        assertEquals(3, treecko.generation)
        assertEquals(3, deoxys.generation)
    }
    
    @Test
    fun `Pokemon ratingsCount retorna generacion correcta Gen 4`() {
        val turtwig = Pokemon("turtwig", "https://pokeapi.co/api/v2/pokemon/387/")
        val arceus = Pokemon("arceus", "https://pokeapi.co/api/v2/pokemon/493/")
        
        assertEquals(4, turtwig.generation)
        assertEquals(4, arceus.generation)
    }
    
    @Test
    fun `Pokemon ratingsCount retorna generacion correcta Gen 5`() {
        val victini = Pokemon("victini", "https://pokeapi.co/api/v2/pokemon/494/")
        val genesect = Pokemon("genesect", "https://pokeapi.co/api/v2/pokemon/649/")
        
        assertEquals(5, victini.generation)
        assertEquals(5, genesect.generation)
    }
    
    @Test
    fun `Pokemon ratingsCount retorna generacion correcta Gen 6`() {
        val chespin = Pokemon("chespin", "https://pokeapi.co/api/v2/pokemon/650/")
        val volcanion = Pokemon("volcanion", "https://pokeapi.co/api/v2/pokemon/721/")
        
        assertEquals(6, chespin.generation)
        assertEquals(6, volcanion.generation)
    }
    
    @Test
    fun `Pokemon ratingsCount retorna generacion correcta Gen 7`() {
        val rowlet = Pokemon("rowlet", "https://pokeapi.co/api/v2/pokemon/722/")
        val melmetal = Pokemon("melmetal", "https://pokeapi.co/api/v2/pokemon/809/")
        
        assertEquals(7, rowlet.generation)
        assertEquals(7, melmetal.generation)
    }
    
    @Test
    fun `Pokemon ratingsCount retorna generacion correcta Gen 8`() {
        val grookey = Pokemon("grookey", "https://pokeapi.co/api/v2/pokemon/810/")
        val enamorus = Pokemon("enamorus", "https://pokeapi.co/api/v2/pokemon/905/")
        
        assertEquals(8, grookey.generation)
        assertEquals(8, enamorus.generation)
    }
    
    @Test
    fun `Pokemon ratingsCount retorna generacion correcta Gen 9 y superiores`() {
        val sprigatito = Pokemon("sprigatito", "https://pokeapi.co/api/v2/pokemon/906/")
        val futuro = Pokemon("future-pokemon", "https://pokeapi.co/api/v2/pokemon/1500/")
        
        assertEquals(9, sprigatito.generation)
        assertEquals(9, futuro.generation)
    }
    
    @Test
    fun `Pokemon released retorna numero de Pokedex formateado`() {
        val game = Pokemon("pikachu", "https://pokeapi.co/api/v2/pokemon/25/")
        
        assertEquals("Pokédex #25", game.pokedexNumber)
    }
    
    // ===== TESTS DE DETALLE DE POKEMON (clase PokemonDetail - 20% cobertura) =====
    
    @Test
    fun `PokemonDetail (PokemonDetail) calcula baseStatsTotal correctamente`() {
        val stats = listOf(
            StatSlot(45, NamedResource("hp", "url")),
            StatSlot(49, NamedResource("attack", "url")),
            StatSlot(49, NamedResource("defense", "url")),
            StatSlot(65, NamedResource("special-attack", "url")),
            StatSlot(65, NamedResource("special-defense", "url")),
            StatSlot(45, NamedResource("speed", "url"))
        )
        
        val gameDetail = PokemonDetail(
            id = 1,
            name = "bulbasaur",
            height = 7,
            weight = 69,
            sprites = Sprites(frontDefault = "url"),
            types = emptyList(),
            abilities = emptyList(),
            stats = stats
        )
        
        assertEquals(318, gameDetail.baseStatsTotal)
    }
    
    @Test
    fun `PokemonDetail genera description correctamente`() {
        val gameDetail = PokemonDetail(
            id = 25,
            name = "pikachu",
            height = 4,
            weight = 60,
            sprites = Sprites(frontDefault = "url"),
            types = emptyList(),
            abilities = emptyList(),
            stats = listOf(
                StatSlot(35, NamedResource("hp", "url")),
                StatSlot(55, NamedResource("attack", "url")),
                StatSlot(40, NamedResource("defense", "url")),
                StatSlot(50, NamedResource("special-attack", "url")),
                StatSlot(50, NamedResource("special-defense", "url")),
                StatSlot(90, NamedResource("speed", "url"))
            )
        )
        
        val description = gameDetail.description
        
        assertTrue(description.contains("Altura: 0.4m"))
        assertTrue(description.contains("Peso: 6.0kg"))
        assertTrue(description.contains("Stats: 320"))
    }
    
    @Test
    fun `PokemonDetail backgroundImage retorna sprite frontal`() {
        val gameDetail = PokemonDetail(
            id = 1,
            name = "bulbasaur",
            height = 7,
            weight = 69,
            sprites = Sprites(frontDefault = "https://example.com/bulbasaur.png"),
            types = emptyList(),
            abilities = emptyList(),
            stats = emptyList()
        )
        
        assertEquals("https://example.com/bulbasaur.png", gameDetail.spriteUrl)
    }
    
    @Test
    fun `PokemonDetail rating se calcula desde baseStatsTotal`() {
        val stats = listOf(
            StatSlot(50, NamedResource("hp", "url")),
            StatSlot(50, NamedResource("attack", "url")),
            StatSlot(50, NamedResource("defense", "url")),
            StatSlot(50, NamedResource("special-attack", "url")),
            StatSlot(50, NamedResource("special-defense", "url")),
            StatSlot(50, NamedResource("speed", "url"))
        )
        
        val gameDetail = PokemonDetail(
            id = 1,
            name = "test",
            height = 10,
            weight = 100,
            sprites = Sprites(frontDefault = "url"),
            types = emptyList(),
            abilities = emptyList(),
            stats = stats
        )
        
        assertEquals(3.0, gameDetail.rating, 0.01)
    }
    
    // ===== TESTS DE LISTA DE POKEMON (clase PokemonListResponse - 10% cobertura) =====
    
    @Test
    fun `PokemonListResponse (PokemonListResponse) convierte results a games correctamente`() {
        val results = listOf(
            PokemonBasic("bulbasaur", "https://pokeapi.co/api/v2/pokemon/1/"),
            PokemonBasic("ivysaur", "https://pokeapi.co/api/v2/pokemon/2/"),
            PokemonBasic("venusaur", "https://pokeapi.co/api/v2/pokemon/3/")
        )
        
        val response = PokemonListResponse(
            count = 1025,
            next = "https://pokeapi.co/api/v2/pokemon?offset=20&limit=20",
            previous = null,
            results = results
        )
        
        val games = response.pokemons
        
        assertEquals(3, games.size)
        assertEquals("bulbasaur", games[0].name)
        assertEquals("ivysaur", games[1].name)
        assertEquals("venusaur", games[2].name)
        assertEquals(1, games[0].id)
        assertEquals(2, games[1].id)
        assertEquals(3, games[2].id)
    }
    
    @Test
    fun `PokemonListResponse (PokemonListResponse) con lista vacia retorna lista vacia`() {
        val response = PokemonListResponse(
            count = 0,
            next = null,
            previous = null,
            results = emptyList()
        )
        
        assertEquals(0, response.pokemons.size)
    }
    
    // ===== TESTS DE POKEMONBASIC (5% cobertura) =====
    
    @Test
    fun `PokemonBasic se crea correctamente`() {
        val pokemon = PokemonBasic(
            name = "pikachu",
            url = "https://pokeapi.co/api/v2/pokemon/25/"
        )
        
        assertEquals("pikachu", pokemon.name)
        assertEquals("https://pokeapi.co/api/v2/pokemon/25/", pokemon.url)
    }
    
    // ===== TESTS DE POKEMONTYPE Y NAMEDRESOURCE (5% cobertura) =====
    
    @Test
    fun `PokemonType se crea correctamente`() {
        val type = PokemonType(
            slot = 1,
            type = NamedResource("electric", "https://pokeapi.co/api/v2/type/13/")
        )
        
        assertEquals(1, type.slot)
        assertEquals("electric", type.type.name)
    }
    
    @Test
    fun `NamedResource se crea correctamente`() {
        val resource = NamedResource(
            name = "thunderbolt",
            url = "https://pokeapi.co/api/v2/move/85/"
        )
        
        assertEquals("thunderbolt", resource.name)
        assertEquals("https://pokeapi.co/api/v2/move/85/", resource.url)
    }
    
    // ===== TESTS DE SPRITES (5% cobertura) =====
    
    @Test
    fun `Sprites se crea correctamente`() {
        val sprites = Sprites(
            frontDefault = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png"
        )
        
        assertEquals("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png", sprites.frontDefault)
    }
    
    @Test
    fun `Sprites con frontDefault null`() {
        val sprites = Sprites(frontDefault = null)
        
        assertEquals(null, sprites.frontDefault)
    }
    
    // ===== TESTS DE STATSLOT Y ABILITYSLOT (5% cobertura) =====
    
    @Test
    fun `StatSlot se crea correctamente`() {
        val stat = StatSlot(
            baseStat = 90,
            stat = NamedResource("speed", "url")
        )
        
        assertEquals(90, stat.baseStat)
        assertEquals("speed", stat.stat.name)
    }
    
    @Test
    fun `AbilitySlot se crea correctamente`() {
        val ability = AbilitySlot(
            slot = 1,
            ability = NamedResource("static", "url")
        )
        
        assertEquals(1, ability.slot)
        assertEquals("static", ability.ability.name)
    }
}
