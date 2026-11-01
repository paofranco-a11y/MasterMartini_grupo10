package com.example.mastermartini.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión contextual estándar para DataStore Preferences
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "favoritos_recetas_prefs")

class FavoritosRepository private constructor(private val context: Context) {

    // Clave para almacenar el conjunto de identificadores de recetas favoritas
    private val CLAVE_FAVORITOS = stringSetPreferencesKey("ids_favoritas")

    // Flujo observable con el conjunto de recetas favoritas
    val favoritosIds: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[CLAVE_FAVORITOS] ?: emptySet()
    }

    // Comprueba si una receta específica está guardada en favoritos
    fun esFavorita(recetaId: String): Flow<Boolean> {
        return favoritosIds.map { setIds ->
            setIds.contains(recetaId)
        }
    }

    // Alterna el estado (guarda o elimina de DataStore de forma asíncrona)
    suspend fun toggleFavorito(recetaId: String) {
        context.dataStore.edit { preferences ->
            val actuales = preferences[CLAVE_FAVORITOS]?.toMutableSet() ?: mutableSetOf()
            if (actuales.contains(recetaId)) {
                actuales.remove(recetaId)
            } else {
                actuales.add(recetaId)
            }
            preferences[CLAVE_FAVORITOS] = actuales
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: FavoritosRepository? = null

        fun getInstance(context: Context): FavoritosRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = FavoritosRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}