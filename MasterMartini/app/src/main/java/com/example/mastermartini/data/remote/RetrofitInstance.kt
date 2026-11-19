package com.example.mastermartini.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton de Retrofit configurado con la URL base de MockAPI.io.
 */
object RetrofitInstance {
    private const val BASE_URL = "https://6afe63113a0e70bc8d5367db.mockapi.io/api/v1/"

    val api: RecetaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecetaApiService::class.java)
    }
}
