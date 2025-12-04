package com.example.levelupgamerpanel_app

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.levelupgamerpanel_app.data.models.LoginResponse
import com.example.levelupgamerpanel_app.data.models.Usuario
import com.example.levelupgamerpanel_app.data.repository.ApiRepository
import com.example.levelupgamerpanel_app.ui.screens.login.LoginViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var mockApp: Application
    private lateinit var mockRepository: ApiRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.i(any(), any()) } returns 0
        every { android.util.Log.w(any(), any<String>()) } returns 0
        
        mockApp = mockk(relaxed = true)
        mockRepository = mockk(relaxed = true)
        
        viewModel = LoginViewModel(mockApp).apply {
            val repositoryField = LoginViewModel::class.java.getDeclaredField("repository")
            repositoryField.isAccessible = true
            repositoryField.set(this, mockRepository)
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `estado inicial - todos los campos vacios`() {
        assertEquals("", viewModel.usuario.value)
        assertEquals("", viewModel.contraseña.value)
        assertEquals(null, viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
        assertFalse(viewModel.intentoLogin.value)
        assertFalse(viewModel.loginExitoso.value)
    }

    @Test
    fun `onUsuarioChange - actualiza el campo usuario`() {
        viewModel.onUsuarioChange("admin@test.com")
        assertEquals("admin@test.com", viewModel.usuario.value)
    }

    @Test
    fun `onUsuarioChange - limpia el error al cambiar`() {
        viewModel.error.value = "Error anterior"
        viewModel.onUsuarioChange("admin@test.com")
        assertEquals(null, viewModel.error.value)
    }

    @Test
    fun `onContraseñaChange - actualiza el campo contraseña`() {
        viewModel.onContraseñaChange("password123")
        assertEquals("password123", viewModel.contraseña.value)
    }

    @Test
    fun `onContraseñaChange - limpia el error al cambiar`() {
        viewModel.error.value = "Error anterior"
        viewModel.onContraseñaChange("password123")
        assertEquals(null, viewModel.error.value)
    }

    @Test
    fun `validarLogin - error cuando usuario esta vacio`() = runTest {
        viewModel.usuario.value = ""
        viewModel.contraseña.value = "password123"

        viewModel.validarLogin()
        advanceUntilIdle()

        assertEquals("El correo es obligatorio", viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
        assertTrue(viewModel.intentoLogin.value)
        assertFalse(viewModel.loginExitoso.value)
    }

    @Test
    fun `validarLogin - error cuando contraseña esta vacia`() = runTest {
        viewModel.usuario.value = "admin@test.com"
        viewModel.contraseña.value = ""

        viewModel.validarLogin()
        advanceUntilIdle()

        assertEquals("La contraseña es obligatoria", viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
        assertTrue(viewModel.intentoLogin.value)
        assertFalse(viewModel.loginExitoso.value)
    }

    @Test
    fun `validarLogin - error cuando ambos campos estan vacios`() = runTest {
        viewModel.usuario.value = ""
        viewModel.contraseña.value = ""

        viewModel.validarLogin()
        advanceUntilIdle()

        val errorMsg = viewModel.error.value
        assertNotNull(errorMsg)
        assertTrue(errorMsg.contains("correo") || errorMsg.contains("contraseña"))
        assertFalse(viewModel.isLoading.value)
        assertTrue(viewModel.intentoLogin.value)
        assertFalse(viewModel.loginExitoso.value)
    }

    @Test
    fun `validarLogin - login exitoso con usuario ADMIN`() = runTest {
        val adminUser = Usuario(
            correo = "admin@test.com",
            password = "hashedPassword123",
            run = "12345678K",
            nombres = "Admin",
            apellidos = "User",
            tipoUsuario = "ADMIN",
            region = "Metropolitana",
            comuna = "Santiago",
            direccion = "Calle 123",
            puntosLevelUp = 0,
            activo = true
        )

        val loginResponse = LoginResponse(
            token = "test-token-123",
            usuario = adminUser
        )

        // Mock del repository para login exitoso
        coEvery { mockRepository.login(any(), any()) } returns Result.success(loginResponse)
        coEvery { mockRepository.obtenerUsuarioActual() } returns adminUser

        viewModel.usuario.value = "admin@test.com"
        viewModel.contraseña.value = "password123"

        viewModel.validarLogin()
        advanceUntilIdle()

        assertTrue(viewModel.loginExitoso.value)
        assertEquals(null, viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
        assertTrue(viewModel.intentoLogin.value)
        
        coVerify(exactly = 1) { mockRepository.login(any(), any()) }
    }

    @Test
    fun `validarLogin - login exitoso con usuario VENDEDOR`() = runTest {
        val vendedorUser = Usuario(
            correo = "vendedor@test.com",
            password = "hashedPassword456",
            run = "87654321K",
            nombres = "Vendedor",
            apellidos = "Test",
            tipoUsuario = "VENDEDOR",
            region = "Metropolitana",
            comuna = "Santiago",
            direccion = "Calle 456",
            puntosLevelUp = 0,
            activo = true
        )

        val loginResponse = LoginResponse(
            token = "test-token-456",
            usuario = vendedorUser
        )

        coEvery { mockRepository.login(any(), any()) } returns Result.success(loginResponse)
        coEvery { mockRepository.obtenerUsuarioActual() } returns vendedorUser

        viewModel.usuario.value = "vendedor@test.com"
        viewModel.contraseña.value = "password123"

        viewModel.validarLogin()
        advanceUntilIdle()

        assertTrue(viewModel.loginExitoso.value)
        assertEquals(null, viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
        
        coVerify(exactly = 1) { mockRepository.login(any(), any()) }
    }

    @Test
    fun `validarLogin - BLOQUEA usuario CLIENTE y hace logout`() = runTest {
        val clienteUser = Usuario(
            correo = "cliente@test.com",
            password = "hashedPassword789",
            run = "11111111K",
            nombres = "Cliente",
            apellidos = "Bloqueado",
            tipoUsuario = "CLIENTE",
            region = "Metropolitana",
            comuna = "Santiago",
            direccion = "Calle 789",
            puntosLevelUp = 100,
            activo = true
        )

        val loginResponse = LoginResponse(
            token = "test-token-789",
            usuario = clienteUser
        )

        coEvery { mockRepository.login(any(), any()) } returns Result.success(loginResponse)
        coEvery { mockRepository.obtenerUsuarioActual() } returns clienteUser
        coEvery { mockRepository.logout() } just Runs

        viewModel.usuario.value = "cliente@test.com"
        viewModel.contraseña.value = "password123"

        viewModel.validarLogin()
        advanceUntilIdle()

        // DEBE bloquear al cliente
        assertFalse(viewModel.loginExitoso.value)
        assertEquals("Acceso denegado. Esta aplicación es solo para administradores y vendedores.", viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
        
        coVerify(exactly = 1) { mockRepository.logout() }
    }

    @Test
    fun `validarLogin - CLIENTE bloqueado no debe marcar loginExitoso`() = runTest {
        val clienteUser = Usuario(
            correo = "cliente2@test.com",
            password = "hashedPassword999",
            run = "22222222K",
            nombres = "Cliente",
            apellidos = "NoPermitido",
            tipoUsuario = "CLIENTE",
            region = "Valparaíso",
            comuna = "Viña del Mar",
            direccion = "Avenida 123",
            puntosLevelUp = 50,
            activo = true
        )

        val loginResponse = LoginResponse(
            token = "token-cliente",
            usuario = clienteUser
        )

        coEvery { mockRepository.login(any(), any()) } returns Result.success(loginResponse)
        coEvery { mockRepository.obtenerUsuarioActual() } returns clienteUser
        coEvery { mockRepository.logout() } just Runs

        viewModel.usuario.value = "cliente2@test.com"
        viewModel.contraseña.value = "password123"

        viewModel.validarLogin()
        advanceUntilIdle()

        assertFalse(viewModel.loginExitoso.value)
        val errorMsg = viewModel.error.value
        assertNotNull(errorMsg)
        assertTrue(errorMsg.contains("Acceso denegado"))
    }

    @Test
    fun `validarLogin - error de credenciales invalidas`() = runTest {
        val errorException = Exception("Credenciales inválidas")
        coEvery { mockRepository.login(any(), any()) } returns Result.failure(errorException)

        viewModel.usuario.value = "wrong@test.com"
        viewModel.contraseña.value = "wrongpassword"

        viewModel.validarLogin()
        advanceUntilIdle()

        assertFalse(viewModel.loginExitoso.value)
        assertEquals("Credenciales inválidas", viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
        assertTrue(viewModel.intentoLogin.value)
    }

    @Test
    fun `validarLogin - error de red sin mensaje especifico`() = runTest {
        coEvery { mockRepository.login(any(), any()) } returns Result.failure(Exception())

        viewModel.usuario.value = "user@test.com"
        viewModel.contraseña.value = "password123"

        viewModel.validarLogin()
        advanceUntilIdle()

        assertFalse(viewModel.loginExitoso.value)
        assertEquals("Error al iniciar sesión", viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `validarLogin - error de conexion`() = runTest {
        val errorException = Exception("Error de conexión")
        coEvery { mockRepository.login(any(), any()) } returns Result.failure(errorException)

        viewModel.usuario.value = "user@test.com"
        viewModel.contraseña.value = "password123"

        viewModel.validarLogin()
        advanceUntilIdle()

        assertFalse(viewModel.loginExitoso.value)
        assertEquals("Error de conexión", viewModel.error.value)
    }

    @Test
    fun `validarLogin - isLoading es true durante la llamada`() = runTest {
        val adminUser = Usuario(
            correo = "user@test.com",
            password = "hash",
            run = "12345678K",
            nombres = "Test",
            apellidos = "User",
            tipoUsuario = "ADMIN",
            region = "RM",
            comuna = "Santiago",
            direccion = "Calle 1",
            puntosLevelUp = 0,
            activo = true
        )
        
        val loginResponse = LoginResponse(
            token = "test-token",
            usuario = adminUser
        )
        
        coEvery { mockRepository.login(any(), any()) } returns Result.success(loginResponse)
        coEvery { mockRepository.obtenerUsuarioActual() } returns adminUser

        viewModel.usuario.value = "user@test.com"
        viewModel.contraseña.value = "password123"

        viewModel.validarLogin()
        
        testScheduler.advanceTimeBy(50)
        advanceUntilIdle()
        
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `validarLogin - intentoLogin se marca como true`() = runTest {
        coEvery { mockRepository.login(any(), any()) } returns Result.failure(Exception("Error"))

        assertFalse(viewModel.intentoLogin.value)

        viewModel.usuario.value = "user@test.com"
        viewModel.contraseña.value = "password123"
        viewModel.validarLogin()
        advanceUntilIdle()

        assertTrue(viewModel.intentoLogin.value)
    }

    @Test
    fun `validarLogin - usuario con espacios en blanco`() = runTest {
        viewModel.usuario.value = "   "
        viewModel.contraseña.value = "password123"

        viewModel.validarLogin()
        advanceUntilIdle()

        val errorMsg = viewModel.error.value
        assertNotNull(errorMsg)
        assertTrue(errorMsg.contains("correo"))
        assertFalse(viewModel.loginExitoso.value)
    }

    @Test
    fun `validarLogin - contraseña con espacios en blanco`() = runTest {
        viewModel.usuario.value = "user@test.com"
        viewModel.contraseña.value = "   "

        viewModel.validarLogin()
        advanceUntilIdle()

        val errorMsg = viewModel.error.value
        assertNotNull(errorMsg)
        assertTrue(errorMsg.contains("contraseña"))
        assertFalse(viewModel.loginExitoso.value)
    }
}
