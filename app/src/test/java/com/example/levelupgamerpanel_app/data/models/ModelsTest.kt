package com.example.levelupgamerpanel_app.data.models

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests para los modelos de datos
 * Cobertura: validación de propiedades, valores por defecto, propiedades computadas
 */
class ModelsTest {
    
    // ===== TESTS DE PRODUCTO (30% cobertura) =====
    
    @Test
    fun `Producto con valores correctos se crea exitosamente`() {
        val producto = Producto(
            codigo = "PROD001",
            nombre = "Mouse Gamer",
            precio = 25000.0,
            stock = 10,
            categoria = "Accesorios",
            imagen = "https://example.com/mouse.jpg",
            activo = true,
            stockCritico = 5
        )
        
        assertEquals("PROD001", producto.codigo)
        assertEquals("Mouse Gamer", producto.nombre)
        assertEquals(25000.0, producto.precio)
        assertEquals(10, producto.stock)
        assertEquals("Accesorios", producto.categoria)
        assertTrue(producto.activo)
        assertEquals(5, producto.stockCritico)
    }
    
    @Test
    fun `Producto con valores por defecto se crea correctamente`() {
        val producto = Producto(
            codigo = "PROD002",
            nombre = "Teclado",
            precio = 35000.0,
            stock = 5,
            categoria = "Perifericos"
        )
        
        assertEquals("Perifericos", producto.categoria)
        assertEquals(null, producto.imagen)
        assertTrue(producto.activo)
        assertEquals(5, producto.stockCritico)
    }
    
    @Test
    fun `Producto inactivo se puede crear`() {
        val producto = Producto(
            codigo = "INACT001",
            nombre = "Producto Descontinuado",
            precio = 1000.0,
            stock = 0,
            categoria = "Otros",
            activo = false
        )
        
        assertFalse(producto.activo)
    }
    
    // ===== TESTS DE ITEMPEDIDO (15% cobertura) =====
    
    @Test
    fun `ItemPedido con producto nested funciona correctamente`() {
        val producto = Producto(
            codigo = "PROD001",
            nombre = "Mouse",
            precio = 10000.0,
            stock = 5,
            categoria = "Accesorios"
        )
        
        val item = ItemPedido(
            id = 1L,
            producto = producto,
            cantidad = 2,
            precio = 10000.0
        )
        
        assertEquals(1L, item.id)
        assertEquals(2, item.cantidad)
        assertEquals(10000.0, item.precio)
        assertEquals("Mouse", item.nombreProducto)
        assertEquals("PROD001", item.productoId)
    }
    
    @Test
    fun `ItemPedido sin producto retorna valores por defecto`() {
        val item = ItemPedido(
            id = 2L,
            producto = null,
            cantidad = 1,
            precio = 5000.0
        )
        
        assertEquals("Producto desconocido", item.nombreProducto)
        assertEquals(null, item.productoId)
    }
    
    // ===== TESTS DE PEDIDO (15% cobertura) =====
    
    @Test
    fun `Pedido con valores completos se crea correctamente`() {
        val usuario = Usuario(
            correo = "cliente@test.com",
            nombres = "Juan",
            apellidos = "Pérez"
        )
        
        val pedido = Pedido(
            id = 1L,
            fecha = "2025-01-01T10:00:00",
            estado = "PENDIENTE",
            total = 50000.0,
            subtotal = 45000.0,
            descuentoDuoc = 5000.0,
            descuentoPuntos = 0.0,
            usuario = usuario,
            region = "Metropolitana",
            comuna = "Santiago",
            direccion = "Calle Falsa 123",
            items = emptyList()
        )
        
        assertEquals(1L, pedido.id)
        assertEquals("PENDIENTE", pedido.estado)
        assertEquals(50000.0, pedido.total)
        assertEquals("cliente@test.com", pedido.usuario?.correo)
        assertEquals("Metropolitana", pedido.region)
    }
    
    @Test
    fun `Pedido con items calcula correctamente`() {
        val item1 = ItemPedido(id = 1L, producto = null, cantidad = 2, precio = 10000.0)
        val item2 = ItemPedido(id = 2L, producto = null, cantidad = 1, precio = 15000.0)
        
        val pedido = Pedido(
            id = 1L,
            fecha = "2025-01-01T10:00:00",
            total = 35000.0,
            items = listOf(item1, item2)
        )
        
        assertEquals(2, pedido.items.size)
        assertEquals(35000.0, pedido.total)
    }
    
    // ===== TESTS DE USUARIO (15% cobertura) =====
    
    @Test
    fun `Usuario con todos los campos se crea correctamente`() {
        val usuario = Usuario(
            correo = "admin@test.com",
            password = "securepass123",
            tipoUsuario = "ADMIN",
            nombres = "Admin",
            apellidos = "Principal",
            puntosLevelUp = 1000,
            run = "12345678-9",
            region = "Valparaíso",
            comuna = "Viña del Mar",
            direccion = "Av. Libertad 456",
            activo = true
        )
        
        assertEquals("admin@test.com", usuario.correo)
        assertEquals("ADMIN", usuario.tipoUsuario)
        assertEquals(1000, usuario.puntosLevelUp)
        assertEquals("12345678-9", usuario.run)
        assertTrue(usuario.activo)
    }
    
    @Test
    fun `Usuario con valores por defecto se crea`() {
        val usuario = Usuario(
            correo = "simple@test.com"
        )
        
        assertEquals("", usuario.password)
        assertEquals("CLIENTE", usuario.tipoUsuario)
        assertEquals(0, usuario.puntosLevelUp)
        assertTrue(usuario.activo)
    }
    
    // ===== TESTS DE BOLETA (15% cobertura) =====
    
    @Test
    fun `Boleta con pedido nested se crea correctamente`() {
        val pedido = Pedido(
            id = 1L,
            fecha = "2025-01-01T10:00:00",
            total = 50000.0
        )
        
        val boleta = Boleta(
            id = 1L,
            numero = "BOL-2025-001",
            fecha = "2025-01-01T10:30:00",
            cliente = "Juan Pérez",
            pedidoId = 1L,
            pedido = pedido,
            total = 50000.0
        )
        
        assertEquals("BOL-2025-001", boleta.numero)
        assertEquals("Juan Pérez", boleta.cliente)
        assertEquals(1L, boleta.pedidoId)
        assertEquals(50000.0, boleta.total)
        assertNotNull(boleta.pedido)
    }
    
    @Test
    fun `Boleta sin cliente se puede crear`() {
        val boleta = Boleta(
            numero = "BOL-2025-002",
            fecha = "2025-01-02T11:00:00",
            pedidoId = 2L,
            total = 30000.0
        )
        
        assertEquals(null, boleta.cliente)
        assertEquals(2L, boleta.pedidoId)
    }
    
    // ===== TESTS DE LOGIN REQUEST/RESPONSE (10% cobertura) =====
    
    @Test
    fun `LoginRequest se crea correctamente`() {
        val request = LoginRequest(
            email = "user@test.com",
            password = "mypassword"
        )
        
        assertEquals("user@test.com", request.email)
        assertEquals("mypassword", request.password)
    }
    
    @Test
    fun `LoginResponse contiene datos del usuario y token`() {
        val usuario = Usuario(
            correo = "user@test.com",
            nombres = "Test",
            apellidos = "User"
        )
        
        val response = LoginResponse(
            token = "jwt.token.here",
            type = "Bearer",
            usuario = usuario,
            message = "Login exitoso"
        )
        
        assertEquals("jwt.token.here", response.token)
        assertEquals("Bearer", response.type)
        assertEquals("user@test.com", response.usuario.correo)
        assertEquals("Login exitoso", response.message)
    }
    
    @Test
    fun `LoginResponse con valores por defecto`() {
        val usuario = Usuario(correo = "test@test.com")
        
        val response = LoginResponse(
            token = "token123",
            usuario = usuario
        )
        
        assertEquals("Bearer", response.type)
        assertEquals("", response.message)
        assertEquals(null, response.refreshToken)
    }
    
    // ===== TESTS DE REGISTER REQUEST (10% cobertura) =====
    
    @Test
    fun `RegisterRequest con todos los campos`() {
        val request = RegisterRequest(
            run = "12345678-9",
            nombres = "Juan",
            apellidos = "Pérez",
            correo = "juan@test.com",
            password = "pass123",
            tipoUsuario = "VENDEDOR",
            region = "Metropolitana",
            comuna = "Santiago",
            direccion = "Calle 123"
        )
        
        assertEquals("12345678-9", request.run)
        assertEquals("Juan", request.nombres)
        assertEquals("VENDEDOR", request.tipoUsuario)
        assertEquals("Metropolitana", request.region)
    }
    
    @Test
    fun `RegisterRequest con valores por defecto`() {
        val request = RegisterRequest(
            run = "11111111-1",
            nombres = "Test",
            apellidos = "User",
            correo = "test@test.com",
            password = "pass"
        )
        
        assertEquals("CLIENTE", request.tipoUsuario)
        assertEquals("", request.region)
        assertEquals("", request.comuna)
        assertEquals("", request.direccion)
    }

    
    @Test
    fun `Producto inactivo tiene activo en false`() {
        // Given & When
        val producto = Producto(
            codigo = "PROD003",
            nombre = "Auriculares",
            precio = 45000.0,
            stock = 0,
            categoria = "Audio",
            activo = false
        )
        
        // Then
        assertFalse(producto.activo)
    }
    
    @Test
    fun `Usuario con datos completos se crea exitosamente`() {
        // Given & When
        val usuario = Usuario(
            correo = "test@test.com",
            password = "password123",
            tipoUsuario = "CLIENTE",
            nombres = "Juan",
            apellidos = "Pérez",
            puntosLevelUp = 100,
            run = "12345678-9",
            region = "Metropolitana",
            comuna = "Santiago",
            direccion = "Calle Falsa 123",
            activo = true
        )
        
        // Then
        assertEquals("test@test.com", usuario.correo)
        assertEquals("CLIENTE", usuario.tipoUsuario)
        assertEquals("Juan", usuario.nombres)
        assertEquals("Pérez", usuario.apellidos)
        assertEquals(100, usuario.puntosLevelUp)
        assertTrue(usuario.activo)
    }
    
    @Test
    fun `Usuario por defecto es CLIENTE y activo`() {
        // Given & When
        val usuario = Usuario(
            correo = "nuevo@test.com"
        )
        
        // Then
        assertEquals("CLIENTE", usuario.tipoUsuario)
        assertTrue(usuario.activo)
        assertEquals(0, usuario.puntosLevelUp)
    }
    
    @Test
    fun `Pedido con items se crea correctamente`() {
        // Given
        val producto1 = Producto(
            codigo = "PROD001",
            nombre = "Mouse Gamer",
            precio = 25000.0,
            stock = 10,
            categoria = "Accesorios"
        )
        val producto2 = Producto(
            codigo = "PROD002",
            nombre = "Teclado",
            precio = 35000.0,
            stock = 5,
            categoria = "Perifericos"
        )
        val items = listOf(
            ItemPedido(
                id = 1L,
                producto = producto1,
                cantidad = 2,
                precio = 25000.0
            ),
            ItemPedido(
                id = 2L,
                producto = producto2,
                cantidad = 1,
                precio = 35000.0
            )
        )
        
        // When
        val pedido = Pedido(
            id = 1L,
            fecha = "2024-01-01T10:00:00",
            estado = "PENDIENTE",
            total = 85000.0,
            items = items
        )
        
        // Then
        assertEquals(1L, pedido.id)
        assertEquals("PENDIENTE", pedido.estado)
        assertEquals(85000.0, pedido.total)
        assertEquals(2, pedido.items.size)
    }
    
    @Test
    fun `Pedido calcula total correctamente`() {
        // Given
        val producto1 = Producto(codigo = "PROD001", nombre = "Producto 1", precio = 10000.0, stock = 10, categoria = "Cat1")
        val producto2 = Producto(codigo = "PROD002", nombre = "Producto 2", precio = 5000.0, stock = 10, categoria = "Cat2")
        val items = listOf(
            ItemPedido(id = 1L, producto = producto1, cantidad = 2, precio = 10000.0),
            ItemPedido(id = 2L, producto = producto2, cantidad = 3, precio = 5000.0)
        )
        val totalEsperado = (2 * 10000.0) + (3 * 5000.0) // 35000
        
        // When
        val pedido = Pedido(
            id = 2L,
            fecha = "2024-01-02T10:00:00",
            total = totalEsperado,
            items = items
        )
        
        // Then
        assertEquals(35000.0, pedido.total)
    }
    
    @Test
    fun `LoginRequest se crea con credenciales`() {
        // Given & When
        val request = LoginRequest(
            email = "admin@test.com",
            password = "admin123"
        )
        
        // Then
        assertEquals("admin@test.com", request.email)
        assertEquals("admin123", request.password)
    }
    
    @Test
    fun `RegisterRequest contiene todos los campos requeridos`() {
        // Given & When
        val request = RegisterRequest(
            run = "12345678-9",
            nombres = "María",
            apellidos = "González",
            correo = "maria@test.com",
            password = "password123",
            region = "Metropolitana",
            comuna = "Santiago",
            direccion = "Av. Principal 456"
        )
        
        // Then
        assertEquals("12345678-9", request.run)
        assertEquals("María", request.nombres)
        assertEquals("González", request.apellidos)
        assertEquals("maria@test.com", request.correo)
        assertEquals("password123", request.password)
        assertNotNull(request.region)
        assertNotNull(request.direccion)
    }
    
    @Test
    fun `LoginResponse contiene token y datos de usuario`() {
        // Given & When
        val usuario = Usuario(
            correo = "test@test.com",
            tipoUsuario = "ADMIN",
            puntosLevelUp = 500
        )
        val response = LoginResponse(
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9",
            type = "Bearer",
            usuario = usuario
        )
        
        // Then
        assertNotNull(response.token)
        assertEquals("Bearer", response.type)
        assertEquals("ADMIN", response.usuario.tipoUsuario)
        assertEquals(500, response.usuario.puntosLevelUp)
    }
    
    @Test
    fun `ItemPedido con cantidad y precio calcula subtotal correcto`() {
        // Given & When
        val producto = Producto(
            codigo = "PROD001",
            nombre = "Mouse Gamer",
            precio = 25000.0,
            stock = 10,
            categoria = "Accesorios"
        )
        val item = ItemPedido(
            id = 1L,
            producto = producto,
            cantidad = 3,
            precio = 25000.0
        )
        
        val subtotal = item.cantidad * item.precio
        
        // Then
        assertEquals(3, item.cantidad)
        assertEquals(25000.0, item.precio)
        assertEquals(75000.0, subtotal)
    }
    
    @Test
    fun `Boleta se crea correctamente`() {
        // Given & When
        val boleta = Boleta(
            id = 1L,
            numero = "BOL-001",
            fecha = "2024-01-01T10:00:00",
            pedidoId = 1L,
            total = 85000.0,
            cliente = "Juan Pérez"
        )
        
        // Then
        assertEquals(1L, boleta.id)
        assertEquals("BOL-001", boleta.numero)
        assertEquals(1L, boleta.pedidoId)
        assertEquals(85000.0, boleta.total)
        assertEquals("Juan Pérez", boleta.cliente)
    }
}

