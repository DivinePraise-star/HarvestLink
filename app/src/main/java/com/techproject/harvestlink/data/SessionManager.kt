package com.techproject.harvestlink.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionManager(private val dataStore: DataStore<Preferences>) {
    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_ROLE = stringPreferencesKey("user_role")
    }

    suspend fun saveSession(accessToken: String, refreshToken: String, userId: String, role: String) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = accessToken
            prefs[REFRESH_TOKEN] = refreshToken
            prefs[USER_ID] = userId
            prefs[USER_ROLE] = role
        }
    }

    val sessionFlow: Flow<SessionData?> = dataStore.data.map { prefs ->
        val token = prefs[ACCESS_TOKEN] ?: return@map null
        SessionData(
            accessToken = token,
            refreshToken = prefs[REFRESH_TOKEN] ?: "",
            userId = prefs[USER_ID] ?: "",
            role = prefs[USER_ROLE] ?: ""
        )
    }

    suspend fun clearSession() {
        dataStore.edit { it.clear() }
    }
}

data class SessionData(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val role: String
)