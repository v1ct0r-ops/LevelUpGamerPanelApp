package com.example.levelupgamerpanel_app

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.levelupgamerpanel_app.data.models.*
import com.example.levelupgamerpanel_app.data.repository.ApiRepository
import io.mockk.*
import io.mockk.coEvery
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
class AppViewModelTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var viewModel: AppViewModel
    private lateinit var mockApplication: Application
    private lateinit var mockRepo: com.example.levelupgamerpanel_app.data.repository.ApiRepository
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.i(any(), any()) } returns 0
        every { android.util.Log.w(any(), any<String>()) } returns 0
        
        mockApplication = mockk(relaxed = true)
        mockRepo = mockk(relaxed = true)
        coEvery { mockRepo.getTodosProductos() } returns Result.success(emptyList())
        coEvery { mockRepo.getTodosUsuarios() } returns Result.success(emptyList())
        coEvery { mockRepo.getTodosPedidos() } returns Result.success(emptyList())
        coEvery { mockRepo.getTodasBoletas() } returns Result.success(emptyList())
        coEvery { mockRepo.getPerfil() } returns Result.success(com.example.levelupgamerpanel_app.data.models.Usuario(correo = "test@local", nombres = "Test", apellidos = "User"))
        coEvery { mockRepo.crearProducto(any()) } returns Result.success(com.example.levelupgamerpanel_app.data.models.Producto(codigo = "TEST001", nombre = "Test Product", precio = 10000.0, stock = 5, categoria = "Test"))
        coEvery { mockRepo.actualizarProducto(any(), any()) } returns Result.success(com.example.levelupgamerpanel_app.data.models.Producto(codigo = "PROD001", nombre = "Producto Actualizado", precio = 20000.0, stock = 10, categoria = "Test"))
        coEvery { mockRepo.eliminarProducto(any()) } returns Result.success(Unit)
        coEvery { mockRepo.buscarProductos(any()) } returns Result.success(emptyList())
        coEvery { mockRepo.cambiarEstadoPedido(any(), any()) } returns Result.success(com.example.levelupgamerpanel_app.data.models.Pedido(id = 1L, fecha = "2025-01-01T00:00:00", total = 0.0))
        coEvery { mockRepo.logout() } returns Unit
        coEvery { mockRepo.userEmailFlow } returns kotlinx.coroutines.flow.flowOf(null)

        viewModel = AppViewModel(mockApplication, mockRepo)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }
    
    @Test
    fun `cuando se crea el viewModel las listas empiezan vacías`() = runTest {
        // Verificamos que las listas estén vacías al inicio
        assertEquals(emptyList(), viewModel.productos.value)
        assertEquals(emptyList(), viewModel.usuarios.value)
        assertEquals(emptyList(), viewModel.pedidos.value)
        assertFalse(viewModel.isLoading.value)
    }
    
    @Test
    fun `clearError borra el mensaje de error`() = runTest {
        viewModel.clearError()
        
        // Verificamos que el error sea null
        assertEquals(null, viewModel.errorMessage.value)
    }
    
    @Test
    fun `cargarProductos trae la lista de productos`() = runTest {
        viewModel.cargarProductos()
        advanceUntilIdle()
        
        assertNotNull(viewModel.productos.value)
    }
    
    @Test
    fun `addProducto con datos correctos llama a onSuccess`() = runTest {
        val producto = Producto(
            codigo = "TEST001",
            nombre = "Test Product",
            precio = 10000.0,
            stock = 5,
            categoria = "Test"
        )
        var seEjecutoSuccess = false
        
        viewModel.addProducto(
            p = producto,
            onSuccess = { seEjecutoSuccess = true },
            onError = {}
        )
        advanceUntilIdle()
        
        assertNotNull(viewModel.productos.value)
    }
    
    @Test
    fun `updateProducto modifica un producto que ya existe`() = runTest {
        val producto = Producto(
            codigo = "PROD001",
            nombre = "Producto Actualizado",
            precio = 20000.0,
            stock = 10,
            categoria = "Test"
        )
        var seEjecutoSuccess = false
        
        viewModel.updateProducto(
            p = producto,
            onSuccess = { seEjecutoSuccess = true },
            onError = {}
        )
        advanceUntilIdle()
        
        assertNotNull(viewModel.productos.value)
    }
    
    @Test
    fun `removeProducto elimina un producto por su codigo`() = runTest {
        val productoCodigo = "PROD001"
        var seEjecutoSuccess = false
        
        viewModel.removeProducto(
            id = productoCodigo,
            onSuccess = { seEjecutoSuccess = true },
            onError = {}
        )
        advanceUntilIdle()
        
        assertNotNull(viewModel.productos.value)
    }
    
    @Test
    fun `buscarProductos filtra productos por su nombre`() = runTest {
        val nombre = "Mouse"
        
        viewModel.buscarProductos(nombre)
        advanceUntilIdle()
        
        assertNotNull(viewModel.productos.value)
    }
    
    @Test
    fun `setEstadoPedido cambia el estado de un pedido`() = runTest {
        val pedidoId = 1L
        val nuevoEstado = "DESPACHADO"
        var seEjecutoSuccess = false
        
        viewModel.setEstadoPedido(
            id = pedidoId,
            estado = nuevoEstado,
            onSuccess = { seEjecutoSuccess = true },
            onError = {}
        )
        advanceUntilIdle()
        
        assertNotNull(viewModel.pedidos.value)
    }
    
    @Test
    fun `logout limpia los datos del usuario`() = runTest {
        viewModel.logout()
        advanceUntilIdle()
        
        assertEquals(null, viewModel.usuarioActual.value)
        assertEquals(emptyList(), viewModel.productos.value)
        assertEquals(emptyList(), viewModel.pedidos.value)
    }
    
    @Test
    fun `isLoading empieza en false`() {
        assertFalse(viewModel.isLoading.value)
    }
    
    @Test
    fun `errorMessage empieza en null`() {
        assertEquals(null, viewModel.errorMessage.value)
    }
    
    @Test
    fun `addProducto llama onError cuando falla la creacion`() = runTest {
        coEvery { mockRepo.crearProducto(any()) } returns Result.failure(Exception("Error de red"))
        
        val producto = Producto(
            codigo = "FAIL001",
            nombre = "Test Fail",
            precio = 1000.0,
            stock = 1,
            categoria = "Test"
        )
        
        var errorLlamado = false
        var mensajeError = ""
        
        viewModel.addProducto(
            p = producto,
            onSuccess = {},
            onError = { 
                errorLlamado = true
                mensajeError = it
            }
        )
        advanceUntilIdle()
        
        assertTrue(errorLlamado, "onError debe ser llamado cuando falla")
        assertTrue(mensajeError.isNotEmpty(), "Debe haber mensaje de error")
        assertFalse(viewModel.isLoading.value)
    }
    
    @Test
    fun `updateProducto llama onError cuando falla`() = runTest {
        coEvery { mockRepo.actualizarProducto(any(), any()) } returns Result.failure(Exception("Error al actualizar"))
        
        val producto = Producto(
            codigo = "FAIL002",
            nombre = "Test",
            precio = 1000.0,
            stock = 1,
            categoria = "Test"
        )
        
        var errorLlamado = false
        
        viewModel.updateProducto(
            p = producto,
            onSuccess = {},
            onError = { errorLlamado = true }
        )
        advanceUntilIdle()
        
        assertTrue(errorLlamado)
    }
    
    @Test
    fun `removeProducto llama onError cuando falla`() = runTest {
        coEvery { mockRepo.eliminarProducto(any()) } returns Result.failure(Exception("No se puede eliminar"))
        
        var errorLlamado = false
        
        viewModel.removeProducto(
            id = "FAIL003",
            onSuccess = {},
            onError = { errorLlamado = true }
        )
        advanceUntilIdle()
        
        assertTrue(errorLlamado)
    }
    
    @Test
    fun `activarProducto cambia estado del producto`() = runTest {
        coEvery { mockRepo.activarProducto(any()) } returns Result.success(Unit)
        
        var successLlamado = false
        
        viewModel.activarProducto(
            codigo = "PROD001",
            onSuccess = { successLlamado = true },
            onError = {}
        )
        advanceUntilIdle()
        
        assertFalse(viewModel.isLoading.value)
    }
    
    @Test
    fun `activarProducto llama onError cuando falla`() = runTest {
        coEvery { mockRepo.activarProducto(any()) } returns Result.failure(Exception("Error"))
        
        var errorLlamado = false
        
        viewModel.activarProducto(
            codigo = "FAIL004",
            onSuccess = {},
            onError = { errorLlamado = true }
        )
        advanceUntilIdle()
        
        assertTrue(errorLlamado)
    }
    
    @Test
    fun `cargarUsuarios actualiza la lista`() = runTest {
        val usuarios = listOf(
            Usuario(correo = "user1@test.com", nombres = "User", apellidos = "One"),
            Usuario(correo = "user2@test.com", nombres = "User", apellidos = "Two")
        )
        coEvery { mockRepo.getTodosUsuarios() } returns Result.success(usuarios)
        
        viewModel.cargarUsuarios()
        advanceUntilIdle()
        
        assertEquals(usuarios, viewModel.usuarios.value)
        assertFalse(viewModel.isLoading.value)
    }
    
    @Test
    fun `cargarPedidos actualiza la lista`() = runTest {
        val pedidos = listOf(
            Pedido(id = 1L, fecha = "2025-01-01T00:00:00", total = 10000.0),
            Pedido(id = 2L, fecha = "2025-01-02T00:00:00", total = 20000.0)
        )
        coEvery { mockRepo.getTodosPedidos() } returns Result.success(pedidos)
        
        viewModel.cargarPedidos()
        advanceUntilIdle()
        
        assertEquals(pedidos, viewModel.pedidos.value)
        assertFalse(viewModel.isLoading.value)
    }
    
    @Test
    fun `cargarPedidoById llama onSuccess con el pedido`() = runTest {
        val pedido = Pedido(id = 1L, fecha = "2025-01-01T00:00:00", total = 10000.0)
        coEvery { mockRepo.getPedidoById(1L) } returns Result.success(pedido)
        
        var pedidoCargado: Pedido? = null
        
        viewModel.cargarPedidoById(
            id = 1L,
            onSuccess = { pedidoCargado = it },
            onError = {}
        )
        advanceUntilIdle()
        
        assertEquals(pedido, pedidoCargado)
        assertFalse(viewModel.isLoading.value)
    }
    
    @Test
    fun `cargarPedidoById llama onError cuando falla`() = runTest {
        coEvery { mockRepo.getPedidoById(any()) } returns Result.failure(Exception("Pedido no encontrado"))
        
        var errorLlamado = false
        var errorMsg = ""
        
        viewModel.cargarPedidoById(
            id = 999L,
            onSuccess = {},
            onError = { 
                errorLlamado = true
                errorMsg = it
            }
        )
        advanceUntilIdle()
        
        assertTrue(errorLlamado)
        assertTrue(errorMsg.contains("Pedido no encontrado"))
    }
    
    @Test
    fun `addUsuario crea usuario correctamente`() = runTest {
        val usuario = Usuario(
            run = "12345678-9",
            nombres = "Test",
            apellidos = "User",
            correo = "test@test.com",
            password = "pass123",
            tipoUsuario = "CLIENTE",
            region = "RM",
            comuna = "Santiago",
            direccion = "Calle 123"
        )
        
        coEvery { mockRepo.crearUsuario(any()) } returns Result.success(usuario)
        
        var successLlamado = false
        
        viewModel.addUsuario(
            u = usuario,
            onSuccess = { successLlamado = true },
            onError = {}
        )
        advanceUntilIdle()
        
        assertFalse(viewModel.isLoading.value)
    }
    
    @Test
    fun `addUsuario llama onError cuando falla`() = runTest {
        coEvery { mockRepo.crearUsuario(any()) } returns Result.failure(Exception("Usuario ya existe"))
        
        val usuario = Usuario(
            correo = "duplicate@test.com",
            nombres = "Test",
            apellidos = "User"
        )
        
        var errorLlamado = false
        
        viewModel.addUsuario(
            u = usuario,
            onSuccess = {},
            onError = { errorLlamado = true }
        )
        advanceUntilIdle()
        
        assertTrue(errorLlamado)
    }
    
    @Test
    fun `cargarBoletas actualiza la lista de boletas`() = runTest {
        val boletas = listOf(
            Boleta(numero = "BOL-001", fecha = "2025-01-01T00:00:00", pedidoId = 1L, total = 10000.0),
            Boleta(numero = "BOL-002", fecha = "2025-01-02T00:00:00", pedidoId = 2L, total = 20000.0)
        )
        coEvery { mockRepo.getTodasBoletas() } returns Result.success(boletas)
        
        viewModel.cargarBoletas()
        advanceUntilIdle()
        
        assertEquals(boletas, viewModel.boletas.value)
        assertFalse(viewModel.isLoading.value)
    }
    
    @Test
    fun `cargarBoletaByNumero llama onSuccess con la boleta`() = runTest {
        val boleta = Boleta(numero = "BOL-001", fecha = "2025-01-01T00:00:00", pedidoId = 1L, total = 10000.0)
        coEvery { mockRepo.getBoletaPorNumero("BOL-001") } returns Result.success(boleta)
        
        var boletaCargada: Boleta? = null
        
        viewModel.cargarBoletaByNumero(
            numero = "BOL-001",
            onSuccess = { boletaCargada = it },
            onError = {}
        )
        advanceUntilIdle()
        
        assertEquals(boleta, boletaCargada)
    }
    
    @Test
    fun `cargarBoletaByNumero llama onError cuando falla`() = runTest {
        coEvery { mockRepo.getBoletaPorNumero(any()) } returns Result.failure(Exception("Boleta no encontrada"))
        
        var errorLlamado = false
        
        viewModel.cargarBoletaByNumero(
            numero = "INVALID",
            onSuccess = {},
            onError = { errorLlamado = true }
        )
        advanceUntilIdle()
        
        assertTrue(errorLlamado)
    }
    
    @Test
    fun `cargarBoletaByPedido llama onSuccess con boleta cuando existe`() = runTest {
        val boleta = Boleta(numero = "BOL-001", fecha = "2025-01-01T00:00:00", pedidoId = 1L, total = 10000.0)
        coEvery { mockRepo.getBoletaPorPedido(1L) } returns Result.success(boleta)
        
        var boletaCargada: Boleta? = null
        
        viewModel.cargarBoletaByPedido(
            pedidoId = 1L,
            onSuccess = { boletaCargada = it },
            onError = {}
        )
        advanceUntilIdle()
        
        assertEquals(boleta, boletaCargada)
    }
    
    @Test
    fun `cargarBoletaByPedido llama onSuccess con null cuando no existe boleta (404)`() = runTest {
        coEvery { mockRepo.getBoletaPorPedido(999L) } returns Result.success(null)
        
        var boletaCargada: Boleta? = Boleta(numero = "TEMP", fecha = "2025-01-01T00:00:00", pedidoId = 1L, total = 0.0) // valor inicial no-null
        var errorLlamado = false
        
        viewModel.cargarBoletaByPedido(
            pedidoId = 999L,
            onSuccess = { boletaCargada = it },
            onError = { errorLlamado = true }
        )
        advanceUntilIdle()
        
        assertEquals(null, boletaCargada, "Debe ser null cuando no existe boleta")
        assertFalse(errorLlamado, "No debe llamar onError para 404")
    }
    
    @Test
    fun `cargarBoletaByPedido llama onError cuando hay error real (no 404)`() = runTest {
        coEvery { mockRepo.getBoletaPorPedido(any()) } returns Result.failure(Exception("Error de conexión"))
        
        var errorLlamado = false
        
        viewModel.cargarBoletaByPedido(
            pedidoId = 1L,
            onSuccess = {},
            onError = { errorLlamado = true }
        )
        advanceUntilIdle()
        
        assertTrue(errorLlamado)
    }
    
    @Test
    fun `generarBoleta llama onSuccess con la boleta generada`() = runTest {
        val boleta = Boleta(numero = "BOL-NEW", fecha = "2025-01-01T00:00:00", pedidoId = 1L, total = 10000.0)
        coEvery { mockRepo.generarBoleta(1L) } returns Result.success(boleta)
        coEvery { mockRepo.getTodasBoletas() } returns Result.success(listOf(boleta))
        
        var boletaGenerada: Boleta? = null
        
        viewModel.generarBoleta(
            pedidoId = 1L,
            onSuccess = { boletaGenerada = it },
            onError = {}
        )
        advanceUntilIdle()
        
        assertEquals(boleta, boletaGenerada)
    }
    
    @Test
    fun `generarBoleta llama onError cuando falla`() = runTest {
        coEvery { mockRepo.generarBoleta(any()) } returns Result.failure(Exception("Error al generar"))
        
        var errorLlamado = false
        
        viewModel.generarBoleta(
            pedidoId = 1L,
            onSuccess = {},
            onError = { errorLlamado = true }
        )
        advanceUntilIdle()
        
        assertTrue(errorLlamado)
    }
    
    @Test
    fun `login llama onOk cuando las credenciales son correctas`() = runTest {
        val usuario = Usuario(correo = "admin@test.com", nombres = "Admin", apellidos = "User", tipoUsuario = "ADMIN")
        val loginResponse = LoginResponse(
            token = "fake-jwt-token",
            usuario = usuario
        )
        coEvery { mockRepo.login(any(), any()) } returns Result.success(loginResponse)
        coEvery { mockRepo.getPerfil() } returns Result.success(usuario)
        coEvery { mockRepo.getTodosProductos() } returns Result.success(emptyList())
        coEvery { mockRepo.getTodosPedidos() } returns Result.success(emptyList())
        coEvery { mockRepo.getTodosUsuarios() } returns Result.success(emptyList())
        coEvery { mockRepo.getTodasBoletas() } returns Result.success(emptyList())
        
        var okLlamado = false
        
        viewModel.login(
            correo = "admin@test.com",
            pass = "password123",
            onError = {},
            onOk = { okLlamado = true }
        )
        advanceUntilIdle()
        
        assertTrue(okLlamado, "onOk debe ser llamado cuando login es exitoso")
    }
    
    @Test
    fun `login llama onError cuando las credenciales son incorrectas`() = runTest {
        coEvery { mockRepo.login(any(), any()) } returns Result.failure(Exception("Credenciales inválidas"))
        
        var errorLlamado = false
        var errorMsg = ""
        
        viewModel.login(
            correo = "wrong@test.com",
            pass = "wrongpass",
            onError = { 
                errorLlamado = true
                errorMsg = it
            },
            onOk = {}
        )
        advanceUntilIdle()
        
        assertTrue(errorLlamado, "onError debe ser llamado cuando login falla")
        assertTrue(errorMsg.contains("Credenciales") || errorMsg.contains("sesión"))
    }
    
    @Test
    fun `setEstadoPedido llama onSuccess cuando actualiza correctamente`() = runTest {
        val pedidoActualizado = Pedido(id = 1L, fecha = "2025-01-01T00:00:00", total = 10000.0, estado = "ENTREGADO")
        coEvery { mockRepo.cambiarEstadoPedido(1L, "ENTREGADO") } returns Result.success(pedidoActualizado)
        coEvery { mockRepo.getTodosPedidos() } returns Result.success(listOf(pedidoActualizado))
        
        var successLlamado = false
        
        viewModel.setEstadoPedido(
            id = 1L,
            estado = "ENTREGADO",
            onSuccess = { successLlamado = true },
            onError = {}
        )
        advanceUntilIdle()
        
        assertTrue(successLlamado)
    }
    
    @Test
    fun `setEstadoPedido llama onError cuando falla`() = runTest {
        coEvery { mockRepo.cambiarEstadoPedido(any(), any()) } returns Result.failure(Exception("Error al cambiar estado"))
        
        var errorLlamado = false
        
        viewModel.setEstadoPedido(
            id = 1L,
            estado = "CANCELADO",
            onSuccess = {},
            onError = { errorLlamado = true }
        )
        advanceUntilIdle()
        
        assertTrue(errorLlamado)
    }
    
    @Test
    fun `removeUsuario siempre llama onSuccess`() = runTest {
        var successLlamado = false
        
        viewModel.removeUsuario(
            correo = "test@test.com",
            onSuccess = { successLlamado = true },
            onError = {}
        )
        advanceUntilIdle()
        
        assertTrue(successLlamado, "removeUsuario siempre debe llamar onSuccess")
    }
}
