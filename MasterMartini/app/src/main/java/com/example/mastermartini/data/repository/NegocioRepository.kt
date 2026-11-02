package com.example.mastermartini.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.mastermartini.data.local.PreferencesKeys
import com.example.mastermartini.data.local.dataStore
import com.example.mastermartini.data.model.PerfilNegocio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class NegocioRepository(private val context: Context) {

    /**
     * Flujo reactivo que emite el PerfilNegocio almacenado o null si no se ha completado el registro.
     */
    val perfilNegocioFlow: Flow<PerfilNegocio?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            val razonSocial = prefs[PreferencesKeys.RAZON_SOCIAL]
            val rut = prefs[PreferencesKeys.RUT]
            val rubro = prefs[PreferencesKeys.RUBRO]
            val telefono = prefs[PreferencesKeys.TELEFONO]
            val email = prefs[PreferencesKeys.EMAIL]
            val fotoUri = prefs[PreferencesKeys.FOTO_URI]

            if (!razonSocial.isNullOrBlank() && !rut.isNullOrBlank() && !email.isNullOrBlank()) {
                PerfilNegocio(
                    razonSocial = razonSocial,
                    rut = rut,
                    rubro = rubro.orEmpty(),
                    telefono = telefono.orEmpty(),
                    email = email,
                    fotoUri = fotoUri
                )
            } else {
                null
            }
        }

    /**
     * Flujo reactivo que indica si la aplicación tiene una sesión activa registrada.
     */
    val isSesionActivaFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            prefs[PreferencesKeys.SESION_ACTIVA] ?: false
        }

    /**
     * Guarda el perfil de negocio y activa la sesión.
     */
    suspend fun guardarPerfilNegocio(perfil: PerfilNegocio) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.RAZON_SOCIAL] = perfil.razonSocial
            prefs[PreferencesKeys.RUT] = perfil.rut
            prefs[PreferencesKeys.RUBRO] = perfil.rubro
            prefs[PreferencesKeys.TELEFONO] = perfil.telefono
            prefs[PreferencesKeys.EMAIL] = perfil.email
            perfil.fotoUri?.let { uri ->
                prefs[PreferencesKeys.FOTO_URI] = uri
            } ?: prefs.remove(PreferencesKeys.FOTO_URI)
            prefs[PreferencesKeys.SESION_ACTIVA] = true
        }
    }

    /**
     * Invalida la sesión actual sin borrar el registro de la empresa.
     */
    suspend fun cerrarSesion() {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.SESION_ACTIVA] = false
        }
    }
}
