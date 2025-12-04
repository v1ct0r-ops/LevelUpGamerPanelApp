package com.example.levelupgamerpanel_app.data.api

import com.example.levelupgamerpanel_app.data.models.*
import retrofit2.Response
import retrofit2.http.*

// Interface que define todos los endpoints del backend
// Cada funcion representa una peticion HTTP que puedo hacer al servidor
interface ApiService {
    
    // ===== AUTENTICACION =====
    
    // Enviar correo y contrasena para iniciar sesion
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    // Crear una cuenta nueva en el sistema
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Usuario>
    
    // Cerrar sesion y eliminar el token
    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Unit>
    
    // ===== PRODUCTOS =====
    
    // Obtener solo productos activos
    @GET("productos/activos")
    suspend fun getProductosActivos(@Header("Authorization") token: String): Response<List<Producto>>
    
    // Obtener todos los productos (activos e inactivos)
    @GET("productos")
    suspend fun getTodosProductos(@Header("Authorization") token: String): Response<List<Producto>>
    
    // Obtener un producto por su codigo
    @GET("productos/{codigo}")
    suspend fun getProducto(
        @Header("Authorization") token: String,
        @Path("codigo") codigo: String
    ): Response<Producto>
    
    // Crear un nuevo producto
    @POST("productos")
    suspend fun crearProducto(
        @Header("Authorization") token: String,
        @Body producto: Producto
    ): Response<Producto>
    
    // Actualizar un producto existente
    @PUT("productos/{codigo}")
    suspend fun actualizarProducto(
        @Header("Authorization") token: String,
        @Path("codigo") codigo: String,
        @Body producto: Producto
    ): Response<Producto>
    
    // Eliminar un producto por su codigo
    @DELETE("productos/{codigo}")
    suspend fun eliminarProducto(
        @Header("Authorization") token: String,
        @Path("codigo") codigo: String
    ): Response<Unit>
    
    // Buscar productos por categoria
    @GET("productos/categoria/{categoria}")
    suspend fun getProductosPorCategoria(
        @Header("Authorization") token: String,
        @Path("categoria") categoria: String
    ): Response<List<Producto>>
    
    // Buscar productos por nombre
    @GET("productos/buscar")
    suspend fun buscarProductos(
        @Header("Authorization") token: String,
        @Query("nombre") nombre: String
    ): Response<List<Producto>>
    
    // Obtener productos con stock bajo
    @GET("productos/stock-critico")
    suspend fun getProductosStockCritico(
        @Header("Authorization") token: String
    ): Response<List<Producto>>
    
    // Activar o desactivar un producto
    @PUT("productos/{codigo}/activar")
    suspend fun activarProducto(
        @Header("Authorization") token: String,
        @Path("codigo") codigo: String
    ): Response<Unit>
    
    // ===== USUARIOS =====
    
    // Obtener informacion del usuario logueado
    @GET("usuarios/me")
    suspend fun getPerfil(@Header("Authorization") token: String): Response<Usuario>
    
    // Actualizar informacion del usuario logueado
    @PUT("usuarios/me")
    suspend fun actualizarPerfil(
        @Header("Authorization") token: String,
        @Body usuario: Usuario
    ): Response<Usuario>
    
    // Obtener lista de todos los usuarios
    @GET("usuarios")
    suspend fun getTodosUsuarios(@Header("Authorization") token: String): Response<List<Usuario>>
    
    // Obtener solo usuarios activos
    @GET("usuarios/activos")
    suspend fun getUsuariosActivos(@Header("Authorization") token: String): Response<List<Usuario>>
    
    // Obtener un usuario por su RUN
    @GET("usuarios/{run}")
    suspend fun getUsuario(
        @Header("Authorization") token: String,
        @Path("run") run: String
    ): Response<Usuario>
    
    // Actualizar un usuario existente
    @PUT("usuarios/{run}")
    suspend fun actualizarUsuario(
        @Header("Authorization") token: String,
        @Path("run") run: String,
        @Body usuario: Usuario
    ): Response<Usuario>
    
    // Crear un nuevo usuario
    @POST("usuarios")
    suspend fun crearUsuario(
        @Header("Authorization") token: String,
        @Body request: RegisterRequest
    ): Response<Usuario>
    
    // Obtener puntos acumulados del usuario
    @GET("usuarios/puntos")
    suspend fun getPuntos(@Header("Authorization") token: String): Response<Int>
    
    // ===== PEDIDOS =====
    
    // Crear un nuevo pedido
    @POST("v1/pedidos")
    suspend fun crearPedido(
        @Header("Authorization") token: String,
        @Body pedido: Pedido
    ): Response<Pedido>
    
    // Obtener pedidos del usuario logueado
    @GET("v1/pedidos/mis-pedidos")
    suspend fun getMisPedidos(@Header("Authorization") token: String): Response<List<Pedido>>
    
    // Obtener todos los pedidos del sistema
    @GET("v1/pedidos")
    suspend fun getTodosPedidos(@Header("Authorization") token: String): Response<List<Pedido>>
    
    // Obtener un pedido por su ID
    @GET("v1/pedidos/{id}")
    suspend fun getPedidoById(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Pedido>
    
    // Cambiar el estado de un pedido
    @PUT("v1/pedidos/{id}/estado")
    suspend fun cambiarEstadoPedido(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body estadoRequest: Map<String, String>
    ): Response<Pedido>
    
    // ===== BOLETAS =====
    
    // Obtener todas las boletas
    @GET("v1/boletas")
    suspend fun getTodasBoletas(@Header("Authorization") token: String): Response<List<Boleta>>
    
    // Obtener una boleta por su numero
    @GET("v1/boletas/{numero}")
    suspend fun getBoletaPorNumero(
        @Header("Authorization") token: String,
        @Path("numero") numero: String
    ): Response<Boleta>
    
    // Obtener la boleta de un pedido especifico
    @GET("v1/boletas/pedido/{pedidoId}")
    suspend fun getBoletaPorPedido(
        @Header("Authorization") token: String,
        @Path("pedidoId") pedidoId: Long
    ): Response<Boleta>
    
    // Generar una boleta para un pedido
    @POST("v1/boletas/generar/{pedidoId}")
    suspend fun generarBoleta(
        @Header("Authorization") token: String,
        @Path("pedidoId") pedidoId: Long
    ): Response<Boleta>
}
