package com.example.levelupgamerpanel_app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.runtime.Composable

// Herramientas de navegacion de Jetpack Compose
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

// Importar todas las pantallas de la app
import com.example.levelupgamerpanel_app.ui.screens.HomeScreen
import com.example.levelupgamerpanel_app.ui.screens.login.PantallaLogin
import com.example.levelupgamerpanel_app.ui.screens.boletas.BoletasScreen
import com.example.levelupgamerpanel_app.ui.screens.boletas.DetalleBoletaScreen
import com.example.levelupgamerpanel_app.ui.screens.pedidos.DetallePedidoScreen
import com.example.levelupgamerpanel_app.ui.screens.pedidos.PedidosScreen
import com.example.levelupgamerpanel_app.ui.screens.productos.EditarProductoScreen
import com.example.levelupgamerpanel_app.ui.screens.productos.NuevoProductoScreen
import com.example.levelupgamerpanel_app.ui.screens.productos.ProductosScreen
import com.example.levelupgamerpanel_app.ui.screens.pokemon.PokemonScreen
import com.example.levelupgamerpanel_app.ui.screens.usuarios.NuevoUsuarioScreen
import com.example.levelupgamerpanel_app.ui.screens.usuarios.UsuariosScreen

// Rutas de navegacion de la app (como URLs internas)
object Routes {
    const val Splash = "splash"                        // Pantalla de carga inicial
    const val Login = "login"                          // Iniciar sesion
    const val Registro = "registro"                    // Crear cuenta nueva
    const val Home = "home"                           // Pantalla principal
    const val Productos = "productos"                  // Lista de productos
    const val NuevoProducto = "nuevo_producto"         // Crear nuevo producto
    const val EditarProducto = "editar_producto/{id}"  // Editar producto existente
    const val Pedidos = "pedidos"                     // Lista de pedidos
    const val DetallePedido = "detalle_pedido/{id}"   // Detalles de un pedido
    const val Boletas = "boletas"                     // Lista de boletas
    const val DetalleBoleta = "detalle_boleta/{numero}" // Detalles de una boleta
    const val Usuarios = "usuarios"                   // Lista de usuarios
    const val NuevoUsuario = "nuevo_usuario"          // Crear nuevo usuario
    const val Games = "games"                         // Explorar Pokemon desde PokeAPI
}

// Configurar el sistema de navegacion de la app
@Composable
fun AppNav(nav: NavHostController, appViewModel: com.example.levelupgamerpanel_app.AppViewModel){
    // Contenedor principal de todas las pantallas
    NavHost(
        navController = nav,              // Controlador de navegacion
        startDestination = Routes.Splash  // Primera pantalla al abrir la app
    ){
        // Registrar cada pantalla con su ruta

        // Pantalla de carga inicial (Splash Screen)
        composable(Routes.Splash){
            com.example.levelupgamerpanel_app.ui.screens.SplashScreen(navController = nav)
        }

        // Pantalla de inicio de sesion
        composable(Routes.Login){
            PantallaLogin(navController = nav, appViewModel = appViewModel)
        }

        // Pantalla de registro de nuevos usuarios
        composable(Routes.Registro){
            com.example.levelupgamerpanel_app.ui.screens.registro.PantallaRegistro(navController = nav)
        }

        // Pantalla principal (Home)
        composable(Routes.Home){ HomeScreen(nav) }

        // Lista de productos del catalogo
        composable(Routes.Productos){ ProductosScreen(nav) }

        // Formulario para crear producto nuevo
        composable(Routes.NuevoProducto){ NuevoProductoScreen(nav) }

        // Formulario para editar producto (recibe ID como parametro)
        composable(
            route = Routes.EditarProducto,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ){ EditarProductoScreen(nav) }

        // Lista de todos los pedidos
        composable(Routes.Pedidos){ PedidosScreen(nav) }

        // Detalles de un pedido especifico (recibe ID como parametro)
        composable(
            route = Routes.DetallePedido,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ){ DetallePedidoScreen(nav) }

        // Lista de boletas emitidas
        composable(Routes.Boletas){ BoletasScreen(nav) }

        // Explorar Pokemon desde PokeAPI
        composable(Routes.Games){ PokemonScreen(nav) }

        // Detalles de una boleta especifica (recibe numero como parametro)
        composable(
            route = Routes.DetalleBoleta,
            arguments = listOf(navArgument("numero") { type = NavType.StringType })
        ){ DetalleBoletaScreen(nav) }

        // Lista de usuarios del sistema
        composable(Routes.Usuarios){ UsuariosScreen(nav) }

        // Formulario para crear usuario nuevo
        composable(Routes.NuevoUsuario){ NuevoUsuarioScreen(nav) }
    }
}