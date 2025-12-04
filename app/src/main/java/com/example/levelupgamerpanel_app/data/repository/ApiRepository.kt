package com.example.levelupgamerpanel_app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.levelupgamerpanel_app.data.api.RetrofitClient
import com.example.levelupgamerpanel_app.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import retrofit2.Response

// Crear DataStore para guardar datos de autenticacion
private val Context.authDataStore by preferencesDataStore("auth")

// Repositorio que maneja todas las operaciones con la API
// Aqui se hacen las peticiones HTTP y se guarda el token de sesion
class ApiRepository(private val context: Context) {

    private val apiService = RetrofitClient.apiService

    // Claves para guardar datos en DataStore
    private val KEY_TOKEN = stringPreferencesKey("jwt_token")
    private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
    private val KEY_USER_NAME = stringPreferencesKey("user_name")
    private val KEY_USER_TYPE = stringPreferencesKey("user_type")

    // Flows para observar cambios en los datos guardados
    val tokenFlow: Flow<String?> = context.authDataStore.data.map { prefs ->
        prefs[KEY_TOKEN]
    }

    val userEmailFlow: Flow<String?> = context.authDataStore.data.map { prefs ->
        prefs[KEY_USER_EMAIL]
    }

    val userNameFlow: Flow<String?> = context.authDataStore.data.map { prefs ->
        prefs[KEY_USER_NAME]
    }

    val userTypeFlow: Flow<String?> = context.authDataStore.data.map { prefs ->
        prefs[KEY_USER_TYPE]
    }

    // ===== AUTENTICACION =====

    // Iniciar sesion con correo y contrasena
    suspend fun login(correo: String, password: String): Result<LoginResponse> {
        return try {
            // Log para debuggear
            android.util.Log.d("ApiRepository", "=== INTENTO DE LOGIN ===")
            android.util.Log.d("ApiRepository", "Correo: $correo")
            android.util.Log.d("ApiRepository", "Password length: ${password.length}")

            // El backend espera "email", no "correo"
            val loginRequest = LoginRequest(email = correo, password = password)
            android.util.Log.d("ApiRepository", "Request creado: email=${loginRequest.email}")

            val response = apiService.login(loginRequest)
            android.util.Log.d("ApiRepository", "Response code: ${response.code()}")
            android.util.Log.d("ApiRepository", "Response successful: ${response.isSuccessful}")

            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                android.util.Log.d("ApiRepository", "Login exitoso! Token: ${loginResponse.token.take(20)}...")

                // Guardar token y datos del usuario
                saveAuthData(
                    token = loginResponse.token,
                    email = loginResponse.usuario.correo,
                    name = "${loginResponse.usuario.nombres} ${loginResponse.usuario.apellidos}",
                    type = loginResponse.usuario.tipoUsuario
                )
                Result.success(loginResponse)
            } else {
                // Leer el error del servidor
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("ApiRepository", "Login fallido.")
                android.util.Log.e("ApiRepository", "Error body: $errorBody")
                Result.failure(Exception("Credenciales inválidas"))
            }
        } catch (e: Exception) {
            android.util.Log.e("ApiRepository", "Exception en login: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Registrar un nuevo usuario
    suspend fun register(request: RegisterRequest): Result<Usuario> {
        return try {
            android.util.Log.d("ApiRepository", "=== INTENTO DE REGISTRO ===")
            android.util.Log.d("ApiRepository", "RUN: ${request.run}")
            android.util.Log.d("ApiRepository", "Nombres: ${request.nombres}")
            android.util.Log.d("ApiRepository", "Correo: ${request.correo}")
            android.util.Log.d("ApiRepository", "Tipo Usuario: ${request.tipoUsuario}")
            android.util.Log.d("ApiRepository", "Region: ${request.region}")

            val response = apiService.register(request)
            android.util.Log.d("ApiRepository", "Response code: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                android.util.Log.d("ApiRepository", "Registro exitoso!")
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("ApiRepository", "Error al registrar - Code: ${response.code()}")
                android.util.Log.e("ApiRepository", "Error body: $errorBody")
                Result.failure(Exception("Error al registrar usuario: $errorBody"))
            }
        } catch (e: Exception) {
            android.util.Log.e("ApiRepository", "Exception en register: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Cerrar sesion y limpiar datos guardados
    suspend fun logout() {
        val token = getToken()
        if (token != null) {
            try {
                apiService.logout("Bearer $token")
            } catch (e: Exception) {
                // Ignorar errores al hacer logout en el servidor
            }
        }
        clearAuthData()
    }

    // Guardar token y datos del usuario en DataStore
    private suspend fun saveAuthData(token: String, email: String, name: String, type: String) {
        context.authDataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_USER_EMAIL] = email
            prefs[KEY_USER_NAME] = name
            prefs[KEY_USER_TYPE] = type
        }
    }

    // Limpiar todos los datos de autenticacion
    private suspend fun clearAuthData() {
        context.authDataStore.edit { prefs ->
            prefs.clear()
        }
    }

    // Obtener el token JWT guardado
    private suspend fun getToken(): String? {
        return tokenFlow.first()
    }

    // Obtener datos del usuario actual desde DataStore
    suspend fun obtenerUsuarioActual(): Usuario? {
        val email = userEmailFlow.first() ?: return null
        val type = userTypeFlow.first() ?: return null
        val name = userNameFlow.first() ?: ""

        // Separar nombre completo en nombres y apellidos
        val nombres = name.split(" ").firstOrNull() ?: ""
        val apellidos = name.split(" ").drop(1).joinToString(" ")

        return Usuario(
            correo = email,
            password = "", 
            tipoUsuario = type,
            nombres = nombres,
            apellidos = apellidos,
            puntosLevelUp = 0,
            run = "",
            region = "",  
            comuna = "",
            direccion = "",
            activo = true
        )
    }

    // Obtener token con el formato "Bearer token"
    private suspend fun getBearerToken(): String? {
        val token = getToken()
        return if (token != null) {
            // Evitar duplicar Bearer si ya esta en el token
            if (token.startsWith("Bearer ", ignoreCase = true)) token else "Bearer $token"
        } else null
    }

    // ===== PRODUCTOS =====

    suspend fun getProductosActivos(): Result<List<Producto>> {
        return executeApiCall { token ->
            apiService.getProductosActivos(token)
        }
    }

    suspend fun getTodosProductos(): Result<List<Producto>> {
        return executeApiCall { token ->
            apiService.getTodosProductos(token)
        }
    }

    suspend fun getProducto(codigo: String): Result<Producto> {
        return executeApiCall { token ->
            apiService.getProducto(token, codigo)
        }
    }

    suspend fun crearProducto(producto: Producto): Result<Producto> {
        return executeApiCall { token ->
            apiService.crearProducto(token, producto)
        }
    }

    suspend fun actualizarProducto(codigo: String, producto: Producto): Result<Producto> {
        return executeApiCall { token ->
            android.util.Log.d("ApiRepository", "=== ACTUALIZANDO PRODUCTO ===")
            android.util.Log.d("ApiRepository", "Codigo: $codigo")
            android.util.Log.d("ApiRepository", "Nombre: ${producto.nombre}")
            android.util.Log.d("ApiRepository", "Activo: ${producto.activo}")
            android.util.Log.d("ApiRepository", "Precio: ${producto.precio}")
            android.util.Log.d("ApiRepository", "Stock: ${producto.stock}")
            
            val response = apiService.actualizarProducto(token, codigo, producto)
            
            android.util.Log.d("ApiRepository", "Response code: ${response.code()}")
            android.util.Log.d("ApiRepository", "Response successful: ${response.isSuccessful}")
            
            if (response.isSuccessful && response.body() != null) {
                val productoActualizado = response.body()!!
                android.util.Log.d("ApiRepository", "Producto actualizado exitosamente:")
                android.util.Log.d("ApiRepository", "  - Activo: ${productoActualizado.activo}")
                android.util.Log.d("ApiRepository", "  - Nombre: ${productoActualizado.nombre}")
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("ApiRepository", "Error al actualizar producto: $errorBody")
            }
            
            response
        }
    }

    suspend fun eliminarProducto(codigo: String): Result<Unit> {
        return executeApiCall { token ->
            apiService.eliminarProducto(token, codigo)
        }
    }

    suspend fun buscarProductos(nombre: String): Result<List<Producto>> {
        return executeApiCall { token ->
            apiService.buscarProductos(token, nombre)
        }
    }

    suspend fun getProductosStockCritico(): Result<List<Producto>> {
        return executeApiCall { token ->
            apiService.getProductosStockCritico(token)
        }
    }
    
    suspend fun activarProducto(codigo: String): Result<Unit> {
        return executeApiCall { token ->
            apiService.activarProducto(token, codigo)
        }
    }

    // ===== USUARIOS =====

    suspend fun getPerfil(): Result<Usuario> {
        return executeApiCall { token ->
            apiService.getPerfil(token)
        }
    }

    suspend fun getTodosUsuarios(): Result<List<Usuario>> {
        return executeApiCall { token ->
            apiService.getTodosUsuarios(token)
        }
    }

    suspend fun getUsuariosActivos(): Result<List<Usuario>> {
        return executeApiCall { token ->
            apiService.getUsuariosActivos(token)
        }
    }
    
    suspend fun crearUsuario(request: RegisterRequest): Result<Usuario> {
        return executeApiCall { token ->
            apiService.crearUsuario(token, request)
        }
    }

    // ===== PEDIDOS =====

    suspend fun getMisPedidos(): Result<List<Pedido>> {
        return executeApiCall { token ->
            apiService.getMisPedidos(token)
        }
    }

    suspend fun getTodosPedidos(): Result<List<Pedido>> {
        return executeApiCall { token ->
            apiService.getTodosPedidos(token)
        }
    }
    
    suspend fun getPedidoById(id: Long): Result<Pedido> {
        return executeApiCall { token ->
            android.util.Log.d("ApiRepository", "=== OBTENIENDO PEDIDO POR ID ===")
            android.util.Log.d("ApiRepository", "ID: $id")
            
            val response = apiService.getPedidoById(token, id)
            
            android.util.Log.d("ApiRepository", "Response code: ${response.code()}")
            android.util.Log.d("ApiRepository", "Response successful: ${response.isSuccessful}")
            
            if (response.isSuccessful && response.body() != null) {
                val pedido = response.body()!!
                android.util.Log.d("ApiRepository", "Pedido obtenido exitosamente:")
                android.util.Log.d("ApiRepository", "  - ID: ${pedido.id}")
                android.util.Log.d("ApiRepository", "  - Estado: ${pedido.estado}")
                android.util.Log.d("ApiRepository", "  - Total: ${pedido.total}")
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("ApiRepository", "Error al obtener pedido: $errorBody")
            }
            
            response
        }
    }

    suspend fun cambiarEstadoPedido(id: Long, estado: String): Result<Pedido> {
        return executeApiCall { token ->
            apiService.cambiarEstadoPedido(token, id, mapOf("estado" to estado))
        }
    }

    // ===== BOLETAS =====

    // Obtener todas las boletas
    suspend fun getTodasBoletas(): Result<List<Boleta>> {
        return executeApiCall { token ->
            android.util.Log.d("ApiRepository", "Obteniendo todas las boletas...")
            apiService.getTodasBoletas(token)
        }
    }

    // Obtener boleta por su numero
    suspend fun getBoletaPorNumero(numero: String): Result<Boleta> {
        return executeApiCall { token ->
            android.util.Log.d("ApiRepository", "Obteniendo boleta por número: $numero")
            apiService.getBoletaPorNumero(token, numero)
        }
    }

    // Obtener boleta de un pedido especifico
    // Si no existe devuelve null en lugar de error
    suspend fun getBoletaPorPedido(pedidoId: Long): Result<Boleta?> {
        return try {
            val token = getBearerToken()
            if (token == null) return Result.failure(Exception("No autenticado. Inicia sesión primero."))

            android.util.Log.d("ApiRepository", "Obteniendo boleta por pedido ID: $pedidoId")
            val response = apiService.getBoletaPorPedido(token, pedidoId)

            if (response.isSuccessful) {
                Result.success(response.body())
            } else {
                if (response.code() == 404) {
                    // No existe boleta para este pedido
                    android.util.Log.d("ApiRepository", "No existe boleta para pedido ID: $pedidoId (404)")
                    Result.success(null)
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "Sesión expirada. Inicia sesión nuevamente."
                        403 -> "No tienes permisos para esta acción."
                        else -> "Error: ${response.message()}"
                    }
                    Result.failure(Exception(errorMsg))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    // Generar boleta para un pedido
    suspend fun generarBoleta(pedidoId: Long): Result<Boleta> {
        return try {
            val token = getBearerToken()
            if (token == null) return Result.failure(Exception("No autenticado. Inicia sesión primero."))

            android.util.Log.d("ApiRepository", "Generando boleta para pedido ID: $pedidoId")
            val response = apiService.generarBoleta(token, pedidoId)

            if (response.isSuccessful && response.body() != null) {
                android.util.Log.d("ApiRepository", "Boleta generada/recuperada: ${response.body()?.numero}")
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("ApiRepository", "Error al generar boleta (code=${response.code()}): $errorBody")
                val errorMsg = when (response.code()) {
                    401 -> "Sesión expirada. Inicia sesión nuevamente."
                    403 -> "No tienes permisos para esta acción."
                    404 -> "Recurso no encontrado."
                    else -> "Error: ${response.message()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    // ===== FUNCION AUXILIAR =====

    // Funcion generica para ejecutar peticiones HTTP con manejo de errores
    private suspend fun <T> executeApiCall(
        apiCall: suspend (String) -> Response<T>
    ): Result<T> {
        return try {
            val token = getBearerToken()
            if (token == null) {
                return Result.failure(Exception("No autenticado. Inicia sesión primero."))
            }

            val response = apiCall(token)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = when (response.code()) {
                    401 -> "Sesión expirada. Inicia sesión nuevamente."
                    403 -> "No tienes permisos para esta acción."
                    404 -> "Recurso no encontrado."
                    else -> "Error: ${response.message()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}
