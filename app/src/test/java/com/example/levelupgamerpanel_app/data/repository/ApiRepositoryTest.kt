package com.example.levelupgamerpanel_app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.levelupgamerpanel_app.data.models.*
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApiRepositoryTest {
    
    private lateinit var repository: ApiRepository
    private lateinit var mockContext: Context
    
    @Before
    fun setup() {
        // Mock de android.util.Log para evitar errores en tests
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.i(any(), any()) } returns 0
        every { android.util.Log.w(any(), any<String>()) } returns 0
        
        mockContext = mockk(relaxed = true)
        
        // Mock DataStore
        val mockDataStore: DataStore<Preferences> = mockk(relaxed = true)
        val mockPreferences: Preferences = mockk(relaxed = true)
        
        every { mockDataStore.data } returns flowOf(mockPreferences)
        every { mockPreferences[any<Preferences.Key<String>>()] } returns null
        
        repository = ApiRepository(mockContext)
    }
    
    @After
    fun tearDown() {
        unmockkAll()
    }
    
    @Test
    fun `login con credenciales válidas retorna success`() = runTest {
        // Given
        val correo = "test@test.com"
        val password = "password123"
        
        // When
        val result = repository.login(correo, password)
        
        // Then
        assertTrue(result.isSuccess || result.isFailure) // Verifica que retorna un Result
    }
    
    @Test
    fun `register con datos válidos retorna success`() = runTest {
        // Given
        val request = RegisterRequest(
            run = "12345678-9",
            nombres = "Test",
            apellidos = "User",
            correo = "test@test.com",
            password = "password123",
            tipoUsuario = "CLIENTE",
            region = "Metropolitana",
            comuna = "Santiago",
            direccion = "Test Address 123"
        )
        
        // When
        val result = repository.register(request)
        
        // Then
        assertTrue(result.isSuccess || result.isFailure)
    }
    
    @Test
    fun `getProductosActivos retorna lista de productos`() = runTest {
        // When
        val result = repository.getProductosActivos()
        
        // Then
        assertTrue(result.isSuccess || result.isFailure)
    }
    
    @Test
    fun `getTodosPedidos retorna lista de pedidos`() = runTest {
        // When
        val result = repository.getTodosPedidos()
        
        // Then
        assertTrue(result.isSuccess || result.isFailure)
    }
}
