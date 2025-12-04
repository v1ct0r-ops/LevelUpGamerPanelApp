package com.example.levelupgamerpanel_app.data.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Crear DataStore para guardar datos localmente en el dispositivo
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

// Almacenamiento local para guardar datos del usuario logueado
class AppStore(private val context: Context) {

    companion object {
        // Las llaves para identificar cada dato guardado
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
    }

    // Observar el email del usuario que esta logueado
    val userEmailFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_EMAIL_KEY]
        }

    // Observar el token de autenticacion guardado
    val authTokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[AUTH_TOKEN_KEY]
        }

    // Guardar el email del usuario en el dispositivo
    suspend fun saveUserEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_EMAIL_KEY] = email
        }
    }

    // Guardar el token de autenticacion en el dispositivo
    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
        }
    }

    // Borrar todos los datos guardados cuando el usuario cierra sesion
    suspend fun clearUserData() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_EMAIL_KEY)
            preferences.remove(AUTH_TOKEN_KEY)
        }
    }

    // Obtener el email del usuario guardado
    suspend fun getUserEmail(): String? {
        var email: String? = null
        context.dataStore.edit { preferences ->
            email = preferences[USER_EMAIL_KEY]
        }
        return email
    }

    // Obtener el token de autenticacion guardado
    suspend fun getAuthToken(): String? {
        var token: String? = null
        context.dataStore.edit { preferences ->
            token = preferences[AUTH_TOKEN_KEY]
        }
        return token
    }
}
