package com.example.levelupgamerpanel_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.levelupgamerpanel_app.ui.screens.HomeScreen
import com.example.levelupgamerpanel_app.ui.screens.LoginScreen
import com.example.levelupgamerpanel_app.ui.screens.pedidos.DetallePedidoScreen
import com.example.levelupgamerpanel_app.ui.screens.pedidos.PedidosScreen
import com.example.levelupgamerpanel_app.ui.screens.productos.EditarProductoScreen
import com.example.levelupgamerpanel_app.ui.screens.productos.NuevoProductoScreen
import com.example.levelupgamerpanel_app.ui.screens.productos.ProductosScreen
import com.example.levelupgamerpanel_app.ui.screens.usuarios.NuevoUsuarioScreen
import com.example.levelupgamerpanel_app.ui.screens.usuarios.UsuariosScreen

object Routes {
    const val Login = "login"
    const val Registro = "registro"
    const val Home = "home"
    const val Productos = "productos"
    const val NuevoProducto = "nuevo_producto"
    const val EditarProducto = "editar_producto/{id}"
    const val Pedidos = "pedidos"
    const val DetallePedido = "detalle_pedido/{id}"
    const val Usuarios = "usuarios"
    const val NuevoUsuario = "nuevo_usuario"
}

@Composable
fun AppNav(nav: NavHostController){
    NavHost(navController = nav, startDestination = Routes.Login){
        composable(Routes.Login){
            com.example.levelupgamerpanel_app.ui.screens.login.PantallaLogin(navController = nav)
        }
        composable(Routes.Registro){
            com.example.levelupgamerpanel_app.ui.screens.registro.PantallaRegistro(navController = nav)
        }
        composable(Routes.Home){ HomeScreen(nav) }
        composable(Routes.Productos){ ProductosScreen(nav) }
        composable(Routes.NuevoProducto){ NuevoProductoScreen(nav) }
        composable(Routes.EditarProducto){ EditarProductoScreen(nav) }
        composable(Routes.Pedidos){ PedidosScreen(nav) }
        composable(Routes.DetallePedido){ DetallePedidoScreen(nav) }
        composable(Routes.Usuarios){ UsuariosScreen(nav) }
        composable(Routes.NuevoUsuario){ NuevoUsuarioScreen(nav) }
    }
}