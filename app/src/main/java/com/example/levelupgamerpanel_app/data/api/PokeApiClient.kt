package com.example.levelupgamerpanel_app.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Cliente de Retrofit para conectarse a la PokeAPI
// Esta API es publica y gratuita, sirve para obtener datos de Pokemon
object PokeApiClient {
    
    // URL base de la PokeAPI
    private const val BASE_URL = "https://pokeapi.co/api/v2/"
    
    // Interceptor para ver los logs de las peticiones HTTP
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (ApiConfig.ENABLE_LOGGING) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }
    
    // Cliente HTTP configurado con timeouts y logging
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(ApiConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(ApiConfig.READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(ApiConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
        .build()
    
    // Instancia de Retrofit que convierte JSON a objetos Kotlin
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    // Servicio que contiene los endpoints de la PokeAPI
    val apiService: PokeApiService = retrofit.create(PokeApiService::class.java)
}
