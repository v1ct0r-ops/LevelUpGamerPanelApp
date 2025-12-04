package com.example.levelupgamerpanel_app.data.models

import com.google.gson.annotations.SerializedName

// Modelos de datos del backend Spring Boot
// Estos modelos deben coincidir con las entidades del backend

// Modelo de Producto
data class Producto(
    val codigo: String,
    val nombre: String,
    val descripcion: String? = "",
    val detalles: String? = "",
    val precio: Double,
    val stock: Int,
    val stockCritico: Int? = 5,
    val categoria: String,
    val imagen: String? = null,
    val activo: Boolean = true
)

// Item individual dentro de un pedido
data class ItemPedido(
    val id: Long? = null,
    val producto: Producto? = null,
    val cantidad: Int,
    val precio: Double
) {
    // Propiedades auxiliares para acceder facilmente a los datos del producto
    val nombreProducto: String
        get() = producto?.nombre ?: "Producto desconocido"
    
    val productoId: String?
        get() = producto?.codigo
}

// Modelo de Pedido
data class Pedido(
    val id: Long,
    val fecha: String,
    val estado: String = "PENDIENTE",
    val total: Double,
    val subtotal: Double? = null,
    val descuentoDuoc: Double? = null,
    val descuentoPuntos: Double? = null,
    val usuario: Usuario? = null,
    val region: String? = null,
    val comuna: String? = null,
    val direccion: String? = null,
    val items: List<ItemPedido> = emptyList()
)

// Modelo de Usuario
data class Usuario(
    val correo: String,
    val password: String = "",
    val tipoUsuario: String = "",
    val nombres: String = "",
    val apellidos: String = "",
    val puntosLevelUp: Int = 0,
    val run: String = "",
    val region: String = "",
    val comuna: String = "",
    val direccion: String = "",
    val activo: Boolean = true
)

// Request para login
data class LoginRequest(
    val email: String,
    val password: String
)

// Response del login con token JWT
data class LoginResponse(
    val token: String,
    val type: String = "Bearer",
    val refreshToken: String? = null,
    val usuario: Usuario,
    val message: String = ""
)

// Request para registrar un usuario nuevo
data class RegisterRequest(
    val run: String,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val password: String,
    val tipoUsuario: String = "",
    val region: String = "",
    val comuna: String = "",
    val direccion: String = ""
)

// Modelo de Boleta
data class Boleta(
    val id: Long? = null,
    val numero: String,
    val fecha: String,
    val fechaEmision: String? = null,
    val cliente: String? = null,
    val pedidoId: Long,
    val pedido: Pedido? = null,
    val total: Double,
    val totalNumerico: Double? = null
)

// Modelo de error de la API
data class ApiError(
    val timestamp: String = "",
    val status: Int = 0,
    val error: String = "",
    val message: String = "",
    val path: String = ""
)
