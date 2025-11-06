package com.example.levelupgamerpanel_app.data.store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.levelupgamerpanel_app.data.models.ItemPedido
import com.example.levelupgamerpanel_app.data.models.Pedido
import com.example.levelupgamerpanel_app.data.models.Producto
import com.example.levelupgamerpanel_app.data.models.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore("levelup_admin")

class AppStore(private val context: Context) {
    private val K_USUARIOS = stringPreferencesKey("usuarios")
    private val K_PRODUCTOS = stringPreferencesKey("productos")
    private val K_PEDIDOS = stringPreferencesKey("pedidos")
    private val K_SESION = stringPreferencesKey("sesion")

    private val json = Json { ignoreUnknownKeys = true }

    val usuariosFlow: Flow<List<Usuario>> = context.dataStore.data.map { pref ->
        pref[K_USUARIOS]?.let { json.decodeFromString(it) } ?: sampleUsuarios()
    }
    val productosFlow: Flow<List<Producto>> = context.dataStore.data.map { pref ->
        pref[K_PRODUCTOS]?.let { json.decodeFromString(it) } ?: sampleProductos()
    }
    val pedidosFlow: Flow<List<Pedido>> = context.dataStore.data.map { pref ->
        pref[K_PEDIDOS]?.let { json.decodeFromString(it) } ?: samplePedidos()
    }
    val sesionFlow: Flow<String?> = context.dataStore.data.map { it[K_SESION] }

    suspend fun saveUsuarios(list: List<Usuario>) {
        context.dataStore.edit { it[K_USUARIOS] = json.encodeToString(list) }
    }
    suspend fun saveProductos(list: List<Producto>) {
        context.dataStore.edit { it[K_PRODUCTOS] = json.encodeToString(list) }
    }
    suspend fun savePedidos(list: List<Pedido>) {
        context.dataStore.edit { it[K_PEDIDOS] = json.encodeToString(list) }
    }
    suspend fun saveSesion(correo: String?) {
        context.dataStore.edit { pref ->
            if (correo == null) pref.remove(K_SESION) else pref[K_SESION] = correo
        }
    }

    private fun sampleUsuarios() = listOf(
        Usuario("admin@duoc.cl","1234","admin","Admin","LevelUp",250),
        Usuario("vendedor@duoc.cl","1234","vendedor","Vendedor","Demo",80)
    )
    private fun sampleProductos() = listOf(
        Producto("P1","Control Pro",49990,10,"Accesorios",null),
        Producto("P2","Headset 7.1",79990,4,"Audio",null),
        Producto("P3","Control Xbox Series X",59990,8,"Accesorios",null),
        Producto("P4","HyperX Cloud II",79990,6,"Accesorios",null),
        Producto("P5","Catan",29990,12,"Juegos de Mesa",null),
        Producto("P6","Carcassonne",24990,9,"Juegos de Mesa",null),
        Producto("P7","PlayStation 5",549990,2,"Consolas",null),
        Producto("P8","PC ASUS ROG Strix",1299990,1,"Computadores Gamers",null),
        Producto("P9","Secretlab Titan",349990,3,"Sillas Gamers",null),
        Producto("P10","Logitech G502 HERO",49990,15,"Mouse",null),
        Producto("P11","Razer Goliathus Extended Chroma",29990,20,"Mousepad",null),
        Producto("P12","Polera 'Level-Up'",14990,25,"Poleras Personalizadas",null)
    )
    private fun samplePedidos() = listOf(
        // Tu existente
        Pedido(
            "PED-1", System.currentTimeMillis(), "pendiente",
            129980,
            "vendedor@duoc.cl",
            listOf(
                ItemPedido("P1", "Control Pro", 2, 49990)
            )
        ),
        Pedido(
            "PED-2",
            System.currentTimeMillis() - 86400000L, // ayer
            "despachado",
            159970,
            "vendedor@duoc.cl",
            listOf(
                ItemPedido("P3","Control Xbox Series X",1,59990),
                ItemPedido("P10","Logitech G502 HERO",2,49990)
            )
        ),

        Pedido(
            "PED-3",
            System.currentTimeMillis() - 2 * 86400000L,
            "pendiente",
            84970,
            "vendedor@duoc.cl",
            listOf(
                ItemPedido("P5","Catan",1,29990),
                ItemPedido("P6","Carcassonne",1,24990),
                ItemPedido("P11","Razer Goliathus Extended Chroma",1,29990)
            )
        ),

        Pedido(
            "PED-4",
            System.currentTimeMillis() - 3 * 86400000L, // hace 3 días
            "cancelado",
            629_980, // 1x P7 (549.990) + 1x P4 (79.990)
            "vendedor@duoc.cl",
            listOf(
                ItemPedido("P7","PlayStation 5",1,549990),
                ItemPedido("P4","HyperX Cloud II",1,79990)
            )
        )
    )

}