package com.example.levelupgamerpanel_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupgamerpanel_app.data.models.*
import com.example.levelupgamerpanel_app.data.repository.ApiRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ViewModel principal que conecta la interfaz con el backend
class AppViewModel(app: Application) : AndroidViewModel(app) {

    // Repositorio para hacer llamadas a la API del backend
    private val repository = ApiRepository(app)

    // Estados que la UI puede observar (productos, usuarios, pedidos, etc)
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios.asStateFlow()

    private val _pedidos = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidos: StateFlow<List<Pedido>> = _pedidos.asStateFlow()

    private val _boletas = MutableStateFlow<List<Boleta>>(emptyList())
    val boletas: StateFlow<List<Boleta>> = _boletas.asStateFlow()

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Correo del usuario que tiene sesion activa
    val sesionCorreo: StateFlow<String?> = repository.userEmailFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        // Cuando cambia la sesion, cargar o limpiar datos automaticamente
        viewModelScope.launch {
            sesionCorreo.collect { email ->
                if (email != null) {
                    cargarDatosIniciales()
                } else {
                    limpiarDatos()
                }
            }
        }
    }

    // Autenticacion: iniciar sesion en el backend

    fun login(correo: String, pass: String, onError:(String)->Unit, onOk:()->Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            android.util.Log.d("AppViewModel", "Intentando login con: $correo")

            val result = repository.login(correo, pass)

            _isLoading.value = false

            if (result.isSuccess) {
                android.util.Log.d("AppViewModel", "Login exitoso en ViewModel")
                // Login exitoso, cargar datos
                cargarDatosIniciales()
                onOk()
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error al iniciar sesión"
                android.util.Log.e("AppViewModel", "Login fallido: $error")
                _errorMessage.value = error
                onError(error)
            }
        }
    }

    // Cerrar sesion y limpiar datos del usuario
    fun logout() {
        viewModelScope.launch {
            repository.logout()
            limpiarDatos()
        }
    }

    // Cargar todos los datos necesarios al iniciar sesion
    private suspend fun cargarDatosIniciales() {
        cargarPerfil()
        cargarProductos()
        cargarPedidos()
        cargarUsuarios()
        // Cargar boletas solo si el usuario es admin o vendedor
        val tipo = _usuarioActual.value?.tipoUsuario?.uppercase()
        if (tipo == "ADMIN" || tipo == "VENDEDOR") {
            cargarBoletas()
        }
    }

    // Limpiar todos los datos cuando se cierra sesion
    private fun limpiarDatos() {
        _usuarioActual.value = null
        _productos.value = emptyList()
        _usuarios.value = emptyList()
        _pedidos.value = emptyList()
        _boletas.value = emptyList()
    }

    // Cargar perfil del usuario actual desde el backend
    private suspend fun cargarPerfil() {
        val result = repository.getPerfil()
        if (result.isSuccess) {
            _usuarioActual.value = result.getOrNull()
        }
    }

    // Cargar lista de productos desde el backend
    fun cargarProductos() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getTodosProductos()
            _isLoading.value = false

            if (result.isSuccess) {
                _productos.value = result.getOrNull() ?: emptyList()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    // Cargar lista de usuarios desde el backend
    fun cargarUsuarios() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getTodosUsuarios()
            _isLoading.value = false
            
            if (result.isSuccess) {
                _usuarios.value = result.getOrNull() ?: emptyList()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    // Cargar lista de pedidos desde el backend
    fun cargarPedidos() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getTodosPedidos()
            _isLoading.value = false
            
            if (result.isSuccess) {
                _pedidos.value = result.getOrNull() ?: emptyList()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
        }
    }
    
    // Cargar un pedido especifico por su ID
    fun cargarPedidoById(id: Long, onSuccess: (Pedido) -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getPedidoById(id)
            _isLoading.value = false
            
            if (result.isSuccess) {
                result.getOrNull()?.let { onSuccess(it) }
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error al cargar pedido"
                _errorMessage.value = error
                onError(error)
            }
        }
    }

    // Crear un nuevo producto en el backend
    fun addProducto(p: Producto, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.crearProducto(p)
            _isLoading.value = false

            if (result.isSuccess) {
                cargarProductos()  // Recargar lista
                onSuccess()
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error al crear producto"
                _errorMessage.value = error
                onError(error)
            }
        }
    }

    // Actualizar un producto existente en el backend
    fun updateProducto(p: Producto, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.actualizarProducto(p.codigo, p)  // Cambiar de p.id a p.codigo
            _isLoading.value = false

            if (result.isSuccess) {
                cargarProductos()  // Recargar lista
                onSuccess()
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error al actualizar producto"
                _errorMessage.value = error
                onError(error)
            }
        }
    }
    
    // Activar o desactivar un producto por su codigo
    fun activarProducto(codigo: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.activarProducto(codigo)
            _isLoading.value = false

            if (result.isSuccess) {
                cargarProductos()  // Recargar lista para actualizar el estado
                onSuccess()
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error al activar/desactivar producto"
                _errorMessage.value = error
                onError(error)
            }
        }
    }

    // Eliminar un producto por su ID/codigo
    fun removeProducto(id: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.eliminarProducto(id)
            _isLoading.value = false

            if (result.isSuccess) {
                cargarProductos()  // Recargar lista
                onSuccess()
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error al eliminar producto"
                _errorMessage.value = error
                onError(error)
            }
        }
    }

    // Buscar productos por nombre en el backend
    fun buscarProductos(nombre: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.buscarProductos(nombre)
            _isLoading.value = false

            if (result.isSuccess) {
                _productos.value = result.getOrNull() ?: emptyList()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    // Cambiar el estado de un pedido (PENDIENTE, DESPACHADO, CANCELADO)
    fun setEstadoPedido(id: Long, estado: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {  // Cambiar id de String a Long
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.cambiarEstadoPedido(id, estado)
            _isLoading.value = false

            if (result.isSuccess) {
                cargarPedidos()  // Recargar lista
                onSuccess()
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error al cambiar estado"
                _errorMessage.value = error
                onError(error)
            }
        }
    }

    // Crear un nuevo usuario en el backend
    fun addUsuario(u: Usuario, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            
            val request = RegisterRequest(
                run = u.run,
                nombres = u.nombres,
                apellidos = u.apellidos,
                correo = u.correo,
                password = u.password,
                tipoUsuario = u.tipoUsuario,
                region = u.region,
                comuna = u.comuna,
                direccion = u.direccion
            )
            
            val result = repository.crearUsuario(request)
            _isLoading.value = false
            
            result.onSuccess {
                cargarUsuarios()
                onSuccess()
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Error al crear usuario"
                onError(e.message ?: "Error desconocido")
            }
        }
    }

    // Eliminar un usuario (solo recarga la lista porque falta endpoint en backend)
    fun removeUsuario(correo: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            cargarUsuarios()
            onSuccess()
        }
    }

    // Cargar todas las boletas desde el backend
    fun cargarBoletas() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getTodasBoletas()
            _isLoading.value = false

            if (result.isSuccess) {
                _boletas.value = result.getOrNull() ?: emptyList()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    // Cargar una boleta especifica por su numero
    fun cargarBoletaByNumero(
        numero: String,
        onSuccess: (Boleta) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getBoletaPorNumero(numero)
            _isLoading.value = false

            if (result.isSuccess) {
                result.getOrNull()?.let { onSuccess(it) }
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error al cargar boleta"
                _errorMessage.value = error
                onError(error)
            }
        }
    }

    // Cargar una boleta asociada a un pedido especifico
    fun cargarBoletaByPedido(
        pedidoId: Long,
        onSuccess: (Boleta?) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getBoletaPorPedido(pedidoId)
            _isLoading.value = false

            if (result.isSuccess) {
                onSuccess(result.getOrNull())
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: ""
                if (errorMsg.contains("404") || errorMsg.contains("no encontrada", ignoreCase = true)) {
                    onSuccess(null)
                } else {
                    _errorMessage.value = errorMsg
                    onError(errorMsg)
                }
            }
        }
    }

    // Generar una boleta nueva para un pedido
    fun generarBoleta(
        pedidoId: Long,
        onSuccess: (Boleta) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.generarBoleta(pedidoId)
            _isLoading.value = false

            if (result.isSuccess) {
                cargarBoletas()
                result.getOrNull()?.let { onSuccess(it) }
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error al generar boleta"
                _errorMessage.value = error
                onError(error)
            }
        }
    }

    // Limpiar el mensaje de error
    fun clearError() {
        _errorMessage.value = null
    }
}
