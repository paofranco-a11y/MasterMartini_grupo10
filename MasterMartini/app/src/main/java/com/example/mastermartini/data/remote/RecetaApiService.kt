package com.example.mastermartini.data.remote

import com.example.mastermartini.data.model.RecetaRemote
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Interfaz de endpoints REST de MockAPI.io para operaciones CRUD sobre recetas.
 */
interface RecetaApiService {

    @GET("recetas")
    suspend fun getRecetas(): List<RecetaRemote>

    @GET("recetas/{id}")
    suspend fun getRecetaById(@Path("id") id: String): RecetaRemote

    @POST("recetas")
    suspend fun createReceta(@Body receta: RecetaRemote): RecetaRemote

    @PUT("recetas/{id}")
    suspend fun updateReceta(
        @Path("id") id: String,
        @Body receta: RecetaRemote
    ): RecetaRemote

    @DELETE("recetas/{id}")
    suspend fun deleteReceta(@Path("id") id: String): RecetaRemote
}
