package com.example.levelupgamerpanel_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.levelupgamerpanel_app.ui.navigation.AppNav
import com.example.levelupgamerpanel_app.ui.theme.LevelUpTheme

// Actividad principal de la aplicacion
// Aqui arranca todo cuando abres la app
class MainActivity : ComponentActivity() {
    
    // El ViewModel que maneja los datos y la logica de negocio
    // viewModels() crea automaticamente una instancia porque AppViewModel hereda de AndroidViewModel
    private val vm: AppViewModel by viewModels()

    // Se ejecuta cuando se crea la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configuro la interfaz de usuario
        setContent {
            // Aplico el tema de colores y estilos
            LevelUpTheme {
                // Surface es como el contenedor principal
                Surface(color = MaterialTheme.colorScheme.background) {
                    
                    // Controlador para navegar entre pantallas
                    val nav = rememberNavController()
                    
                    // Inicio el sistema de navegacion con el controlador y el viewmodel
                    AppNav(nav, vm)
                }
            }
        }
    }
}
