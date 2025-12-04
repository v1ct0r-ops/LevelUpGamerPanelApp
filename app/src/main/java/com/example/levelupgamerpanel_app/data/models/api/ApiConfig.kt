package com.example.levelupgamerpanel_app.data.api

// Archivo que guarda la configuracion de las URLs del backend
object ApiConfig {

    // Aqui defino las 3 URLs posibles para conectarme al backend
    private const val URL_PRODUCTION = "https://levelupgamer-fullstack-4.onrender.com/api/"
    private const val URL_LOCAL_EMULATOR = "http://10.0.2.2:8080/api/" 
    private const val URL_LOCAL_DEVICE = "http://192.168.1.X:8080/api/" 

    // Variable para cambiar entre desarrollo local o produccion
    // true = uso backend en mi maquina
    // false = uso backend en Render
    private const val IS_DEVELOPMENT = false

    // Esta propiedad devuelve la URL que se va a usar en toda la aplicacion
    // Si estoy en desarrollo devuelve URL_LOCAL, si no devuelve URL_PRODUCTION
    val BASE_URL: String
        get() = if (IS_DEVELOPMENT) {
            URL_LOCAL_EMULATOR  // URL para conectar al backend cuando corro el emulador
            // URL_LOCAL_DEVICE  // URL para conectar desde celular fisico a mi PC
        } else {
            URL_PRODUCTION  // URL del backend subido a Render
        }

    // Tiempo maximo de espera para las peticiones HTTP en segundos
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L

    // Si es true, se imprimen los logs de las peticiones HTTP en el logcat
    const val ENABLE_LOGGING = true
}
