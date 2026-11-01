package com.example.mastermartini.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

// Delegado global de instancia única para evitar DataStore multiple instances exception
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mastermartini_session_prefs")

object PreferencesKeys {
    val RAZON_SOCIAL = stringPreferencesKey("razon_social")
    val RUT = stringPreferencesKey("rut")
    val RUBRO = stringPreferencesKey("rubro")
    val TELEFONO = stringPreferencesKey("telefono")
    val EMAIL = stringPreferencesKey("email")
    val FOTO_URI = stringPreferencesKey("foto_uri")
    val SESION_ACTIVA = booleanPreferencesKey("is_sesion_activa")
}
