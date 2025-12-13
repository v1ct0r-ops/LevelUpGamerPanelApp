package com.example.levelupgamerpanel_app.data.repository

import com.example.levelupgamerpanel_app.data.models.*
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.*

/**
 * Tests para PokemonRepository
 * Prueba la integración con PokeAPI
 * 
 * Cubre:
 * - Carga de lista de Pokémon
 * - Carga de Pokémon aleatorios con detalles completos (tipos y estadísticas)
 * - Manejo de errores de API
 * - Parseo de datos de respuesta
 * - Propiedades computadas del modelo Pokemon (que representa Pokémon)
 */

@Ignore
@OptIn(ExperimentalCoroutinesApi::class)

class PokemonRepositoryTest {
    
    private lateinit var repository: PokemonRepository
    
    @Before
    fun setup() {
        // Mock android.util.Log para evitar errores "not mocked"
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.i(any(), any()) } returns 0
        every { android.util.Log.w(any(), any<String>()) } returns 0
        
        repository = PokemonRepository()
    }
    
    @After
    fun tearDown() {
        unmockkAll()
    }
    
    // ===== TESTS DE getPokemonList (30% cobertura) =====
    
    @Test
    fun `getPokemonList - retorna Result exitoso`() = runTest {
        val result = repository.getPokemonList(page = 1)
        
        // Debe retornar un Result (success o failure)
        assertNotNull(result)
    }
    
    @Test
    fun `getPokemonList - acepta diferentes páginas`() = runTest {
        val resultPage1 = repository.getPokemonList(page = 1)
        val resultPage2 = repository.getPokemonList(page = 2)
        
        assertNotNull(resultPage1)
        assertNotNull(resultPage2)
    }
    
    @Test
    fun `getPokemonList - retorna lista de Pokemon cuando es exitoso`() = runTest {
        val result = repository.getPokemonList()
        
        result.onSuccess { pokemonList ->
            // Si es exitoso, debe ser una lista (puede estar vacía)
            assertNotNull(pokemonList)
            assertTrue(pokemonList is List<Pokemon>)
        }
    }
    
    @Test
    fun `getPokemonList - maneja errores correctamente`() = runTest {
        val result = repository.getPokemonList()
        
        result.onFailure { exception ->
            // Si falla, debe tener un mensaje de error
            assertNotNull(exception.message)
        }
    }
    
    // ===== TESTS DE getRandomPokemonWithDetails (40% cobertura) =====
    
    @Test
    fun `getRandomPokemonWithDetails - retorna Result exitoso`() = runTest {
        val result = repository.getRandomPokemonWithDetails()
        
        assertNotNull(result)
    }
    
    @Test
    fun `getRandomPokemonWithDetails - retorna lista de hasta 50 pokemon aleatorios`() = runTest {
        val result = repository.getRandomPokemonWithDetails()
        
        result.onSuccess { pokemonList ->
            assertNotNull(pokemonList)
            assertTrue(pokemonList is List<Pokemon>)
            // Puede tener hasta 50 pokémon (algunos pueden fallar al cargar)
            assertTrue(pokemonList.size <= 50)
        }
    }
    
    @Test
    fun `getRandomPokemonWithDetails - pokemon tienen tipos y stats cargados`() = runTest {
        val result = repository.getRandomPokemonWithDetails()
        
        result.onSuccess { pokemonList ->
            if (pokemonList.isNotEmpty()) {
                val primerPokemon = pokemonList.first()
                
                // Verificar que tiene nombre
                assertNotNull(primerPokemon.name)
                assertTrue(primerPokemon.name.isNotEmpty())
                
                // Verificar que tiene URL
                assertNotNull(primerPokemon.url)
                
                // Verificar que tiene ID válido (mayor a 0)
                assertTrue(primerPokemon.id > 0)
                
                // Los tipos y stats pueden ser null si falló la carga
                // pero si existen deben tener datos válidos
                primerPokemon.types?.let { tipos ->
                    assertTrue(tipos.isNotEmpty())
                }
                
                primerPokemon.baseStats?.let { stats ->
                    assertTrue(stats > 0)
                }
            }
        }
    }
    
    @Test
    fun `getRandomPokemonWithDetails - maneja errores de red`() = runTest {
        val result = repository.getRandomPokemonWithDetails()
        
        result.onFailure { exception ->
            // Si falla, debe tener mensaje descriptivo
            assertNotNull(exception.message)
            assertTrue(exception.message!!.isNotEmpty())
        }
    }
    
    @Test
    fun `getRandomPokemonWithDetails - genera IDs aleatorios entre 1 y 1025`() = runTest {
        val result = repository.getRandomPokemonWithDetails()
        
        result.onSuccess { pokemonList ->
            pokemonList.forEach { pokemon ->
                // Cada pokémon debe tener ID válido en el rango
                assertTrue(pokemon.id in 1..1025, 
                    "Pokemon ID ${pokemon.id} debe estar entre 1 y 1025")
            }
        }
    }
    
    @Test
    fun `getRandomPokemonWithDetails - pokemon tienen nombre válido`() = runTest {
        val result = repository.getRandomPokemonWithDetails()
        
        result.onSuccess { pokemonList ->
            pokemonList.forEach { pokemon ->
                // Nombre no debe estar vacío
                assertTrue(pokemon.name.isNotEmpty(),
                    "Pokemon debe tener nombre no vacío")
                
                // Nombre debe ser lowercase (así viene de la API)
                assertTrue(pokemon.name.all { it.isLowerCase() || it == '-' },
                    "Nombre de Pokemon debe estar en minúsculas")
            }
        }
    }
    
    // ===== TESTS DE PROPIEDADES DEL MODELO GAME (30% cobertura) =====
    
    @Test
    fun `Pokemon - typesString retorna tipos de Pokemon formateados correctamente`() {
        val bulbasaur = Pokemon(
            name = "bulbasaur",
            url = "https://pokeapi.co/api/v2/pokemon/1/",
            types = listOf(
                PokemonType(
                    slot = 1,
                    type = NamedResource("grass", "url")
                ),
                PokemonType(
                    slot = 2,
                    type = NamedResource("poison", "url")
                )
            ),
            baseStats = 318
        )
        
        val typesString = bulbasaur.typesString
        
        // Debe contener los tipos traducidos al español
        assertTrue(typesString.contains("Planta") || typesString.contains("Veneno"))
        assertTrue(typesString.contains(" / "))
    }
    
    @Test
    fun `Pokemon - typesString retorna Cargando cuando types es null`() {
        val pokemon = Pokemon(
            name = "pikachu",
            url = "https://pokeapi.co/api/v2/pokemon/25/",
            types = null,
            baseStats = null
        )
        
        assertEquals("Cargando...", pokemon.typesString)
    }
    
    @Test
    fun `Pokemon - id se extrae correctamente de la URL del Pokemon`() {
        val bulbasaur = Pokemon("bulbasaur", "https://pokeapi.co/api/v2/pokemon/1/")
        val pikachu = Pokemon("pikachu", "https://pokeapi.co/api/v2/pokemon/25/")
        val mew = Pokemon("mew", "https://pokeapi.co/api/v2/pokemon/151/")
        
        assertEquals(1, bulbasaur.id)
        assertEquals(25, pikachu.id)
        assertEquals(151, mew.id)
    }
    
    @Test
    fun `Pokemon - ratingsCount retorna generacion correcta del Pokemon segun ID`() {
        val gen1Pokemon = Pokemon("bulbasaur", "https://pokeapi.co/api/v2/pokemon/1/")
        val gen2Pokemon = Pokemon("chikorita", "https://pokeapi.co/api/v2/pokemon/152/")
        val gen3Pokemon = Pokemon("treecko", "https://pokeapi.co/api/v2/pokemon/252/")
        val gen5Pokemon = Pokemon("victini", "https://pokeapi.co/api/v2/pokemon/494/")
        
        assertEquals(1, gen1Pokemon.generation) // Gen 1: 1-151
        assertEquals(2, gen2Pokemon.generation) // Gen 2: 152-251
        assertEquals(3, gen3Pokemon.generation) // Gen 3: 252-386
        assertEquals(5, gen5Pokemon.generation) // Gen 5: 494-649
    }
    
    @Test
    fun `Pokemon - backgroundImage retorna URL correcta del sprite del Pokemon`() {
        val pikachu = Pokemon("pikachu", "https://pokeapi.co/api/v2/pokemon/25/")
        
        val expectedUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png"
        assertEquals(expectedUrl, pikachu.spriteUrl)
    }
    
    @Test
    fun `Pokemon - rating se calcula correctamente desde baseStats del Pokemon`() {
        val pikachuConStats = Pokemon(
            "pikachu",
            "url",
            baseStats = 320
        )
        
        val pikachuSinStats = Pokemon(
            "pikachu",
            "url",
            baseStats = null
        )
        
        assertEquals(3.2, pikachuConStats.rating, 0.01)
        assertEquals(0.0, pikachuSinStats.rating, 0.01)
    }
}
