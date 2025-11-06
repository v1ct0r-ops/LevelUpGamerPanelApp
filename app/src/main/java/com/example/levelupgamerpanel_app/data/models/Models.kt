package com.example.levelupgamerpanel_app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Producto(
    val id: String,
    val nombre: String,
    val precio: Int,
    val stock: Int,
    val categoria: String = "",
    val imagenUri: String? = null
)

@Serializable
data class ItemPedido(
    val idProducto: String,
    val nombre: String,
    val cantidad: Int,
    val precio: Int
)

@Serializable
data class Pedido(
    val id: String,
    val fecha: Long = System.currentTimeMillis(),
    val estado: String = "pendiente",
    val total: Int,
    val usuarioCorreo: String,
    val items: List<ItemPedido> = emptyList()
)

@Serializable
data class Usuario(
    val correo: String,
    val pass: String,
    val tipoUsuario: String = "admin",
    val nombres: String = "",
    val apellidos: String = "",
    val puntosLevelUp: Int = 0
)