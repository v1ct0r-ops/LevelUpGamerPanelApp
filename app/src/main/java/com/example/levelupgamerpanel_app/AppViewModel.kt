package com.example.levelupgamerpanel_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupgamerpanel_app.data.models.*
import com.example.levelupgamerpanel_app.data.models.Producto
import com.example.levelupgamerpanel_app.data.models.Usuario
import com.example.levelupgamerpanel_app.data.store.AppStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppViewModel(app: Application): AndroidViewModel(app){
    private val store = AppStore(app)

    val usuarios   = store.usuariosFlow .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val productos  = store.productosFlow.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val pedidos    = store.pedidosFlow  .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val sesionCorreo = store.sesionFlow .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Usuario actual derivado de sesionCorreo + usuarios
    val usuarioActual: StateFlow<Usuario?> =
        combine(usuarios, sesionCorreo) { list, correo ->
            list.firstOrNull { it.correo.equals(correo, true) }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun login(correo: String, pass: String, onError:(String)->Unit, onOk:()->Unit) {
        viewModelScope.launch {
            val list = store.usuariosFlow.first()
            val u = list.firstOrNull { it.correo.equals(correo, true) && it.pass == pass }
            if (u == null) onError("Credenciales inválidas")
            else { store.saveSesion(u.correo); onOk() }
        }
    }

    fun logout() { viewModelScope.launch { store.saveSesion(null) } }

    fun addProducto(p: Producto){ viewModelScope.launch { store.saveProductos(productos.value + p) } }
    fun updateProducto(p: Producto){ viewModelScope.launch { store.saveProductos(productos.value.map{ if (it.id==p.id) p else it }) } }
    fun removeProducto(id:String){ viewModelScope.launch { store.saveProductos(productos.value.filterNot{ it.id==id }) } }

    fun setEstadoPedido(id:String, estado:String){
        viewModelScope.launch { store.savePedidos(pedidos.value.map{ if (it.id==id) it.copy(estado=estado) else it }) }
    }

    fun addUsuario(u: Usuario){ viewModelScope.launch { store.saveUsuarios(usuarios.value + u) } }
    fun removeUsuario(correo:String){ viewModelScope.launch { store.saveUsuarios(usuarios.value.filterNot{ it.correo.equals(correo,true) }) } }
}
