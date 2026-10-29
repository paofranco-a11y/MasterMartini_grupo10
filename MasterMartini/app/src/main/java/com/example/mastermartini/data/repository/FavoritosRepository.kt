package com.example.mastermartini.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

// Repositorio de persistencia local para favoritos sin conexión (R-13)
class FavoritosRepository private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("favoritos_recetas_prefs", Context.MODE_PRIVATE)

    private val _favoritosIds = MutableStateFlow<Set<String>>(emptySet())
    val favoritosIds: StateFlow<Set<String>> = _favoritosIds.asStateFlow()

    init {
        val guardados = prefs.getStringSet("ids_favoritas", emptySet()) ?: emptySet()
        _favoritosIds.value = guardados
    }

    fun esFavorita(recetaId: String) = favoritosIds.map { it.contains(recetaId) }

    fun toggleFavorito(recetaId: String) {
        val actuales = _favoritosIds.value.toMutableSet()
        if (actuales.contains(recetaId)) {
            actuales.remove(recetaId)
        } else {
            actuales.add(recetaId)
        }
        prefs.edit().putStringSet("ids_favoritas", actuales).apply()
        _favoritosIds.value = actuales
    }

    companion object {
        @Volatile
        private var INSTANCE: FavoritosRepository? = null

        fun getInstance(context: Context): FavoritosRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = FavoritosRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
}