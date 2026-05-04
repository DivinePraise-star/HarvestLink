package com.techproject.harvestlink

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.techproject.harvestlink.data.AppContainer
import com.techproject.harvestlink.data.DefaultAppContainer
import com.techproject.harvestlink.data.SessionManager

private const val SESSION_MANGAER = "session_manager"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = SESSION_MANGAER
)

class HarvestLinkApplication: Application(){
    lateinit var container: AppContainer
    lateinit var sessionManager: SessionManager

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        sessionManager = SessionManager(dataStore)
    }
}