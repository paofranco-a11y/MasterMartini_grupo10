package com.example.mastermartini.data.repository

import com.example.mastermartini.data.model.RecetaRemote
import com.example.mastermartini.data.remote.RecetaApiService
import com.example.mastermartini.data.remote.RetrofitInstance

/**
 * Repositorio encargado de las operaciones CRUD remotas contra MockAPI.io.
 */
open class RecetaRemoteRepository(
    private val api: RecetaApiService = RetrofitInstance.api
) {
    open suspend fun obtenerRecetas(): List<RecetaRemote> = api.getRecetas()

    open suspend fun obtenerRecetaPorId(id: String): RecetaRemote = api.getRecetaById(id)

    open suspend fun crearReceta(receta: RecetaRemote): RecetaRemote = api.createReceta(receta)

    open suspend fun actualizarReceta(id: String, receta: RecetaRemote): RecetaRemote =
        api.updateReceta(id, receta)

    open suspend fun eliminarReceta(id: String): RecetaRemote = api.deleteReceta(id)
}
