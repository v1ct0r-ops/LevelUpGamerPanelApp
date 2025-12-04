package com.example.levelupgamerpanel_app.data.api

import com.example.levelupgamerpanel_app.data.models.Pokemon
import com.example.levelupgamerpanel_app.data.models.PokemonDetail
import com.example.levelupgamerpanel_app.data.models.PokemonListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Interface que define los endpoints de la PokeAPI
interface PokeApiService {
    
    // Obtener lista de Pokemon 
    @GET("pokemon")
    suspend fun getPokemons(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<PokemonListResponse>
    
    // Obtener detalles completos de un Pokemon especifico
    // id: puede ser el numero del Pokemon o su nombre
    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(
        @Path("id") id: String
    ): Response<PokemonDetail>
    
    // Buscar Pokemon por nombre
    // La PokeAPI no tiene busqueda real, solo podemos traer una lista
    @GET("pokemon")
    suspend fun searchPokemons(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): Response<PokemonListResponse>
}
