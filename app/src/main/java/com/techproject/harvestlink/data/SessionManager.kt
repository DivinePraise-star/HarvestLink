package com.techproject.harvestlink.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class UserSession(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val role: String
)

class SessionManager(private val dataStore: DataStore<Preferences>) {
    private val ACCESS_TOKEN = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    private val USER_ID = stringPreferencesKey("user_id")
    private val ROLE = stringPreferencesKey("role")

    val sessionFlow: Flow<UserSession?> = dataStore.data.map { preferences: Preferences ->
        val accessToken = preferences[ACCESS_TOKEN]
        val refreshToken = preferences[REFRESH_TOKEN]
        val userId = preferences[USER_ID]
        val role = preferences[ROLE]

        if (accessToken != null && refreshToken != null && userId != null && role != null) {
            UserSession(accessToken, refreshToken, userId, role)
        } else {
            null
        }
    }

    suspend fun saveSession(accessToken: String, refreshToken: String, userId: String, role: String) {
        dataStore.edit { preferences: MutablePreferences ->
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
            preferences[USER_ID] = userId
            preferences[ROLE] = role
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences: MutablePreferences ->
            preferences.clear()
        }
    }
}
