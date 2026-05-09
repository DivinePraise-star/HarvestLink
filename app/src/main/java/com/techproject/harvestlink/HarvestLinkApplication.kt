package com.techproject.harvestlink

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.techproject.harvestlink.data.AppContainer
import com.techproject.harvestlink.data.DefaultAppContainer
import com.techproject.harvestlink.data.HarvestDatabase
import com.techproject.harvestlink.data.MoreData
import com.techproject.harvestlink.data.SessionManager
import com.techproject.harvestlink.data.SupabaseService.client
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.postgrest
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import kotlin.coroutines.cancellation.CancellationException

private const val SESSION_MANGAER = "session_manager"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = SESSION_MANGAER
)

class HarvestLinkApplication: Application(){
    lateinit var container: AppContainer
    lateinit var sessionManager: SessionManager
    lateinit var database: HarvestDatabase

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        sessionManager = SessionManager(dataStore)
        database = HarvestDatabase.getDatabase(this)
        MoreData.initCache(database.harvestDao())
        registerActivityLifecycleCallbacks(AppLifecycleTracker())
    }

    private fun updateUserOnlineStatus(isOnline: Boolean) {

        CoroutineScope(Dispatchers.IO).launch {
            val session = sessionManager.sessionFlow.filterNotNull().first()
            val userId = session.userId
            try {
                client.postgrest.from("users")
                    .update({ set("is_online", isOnline) }) {
                        filter { eq("id", userId) }
                    }
                MoreData.markMessagesAsDelivered(userId)
            } catch (e: RestException) {
                Log.e("SupabaseError", "Supabase API error: ${e.message}")
            } catch (e: HttpRequestTimeoutException) {
                Log.w("SupabaseError", "Request timed out. Please check your connection.")
            } catch (e: IOException) {
                Log.e("SupabaseError", "Network connection failed: ${e.message}")
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("SupabaseError", "Unexpected error: ${e.localizedMessage}")
            }
        }
    }

    private inner class AppLifecycleTracker : ActivityLifecycleCallbacks {
        private var resumedActivities = 0

        override fun onActivityResumed(activity: Activity) {
            if (resumedActivities == 0) {
                // Just came to foreground
                updateUserOnlineStatus(true)
            }
            resumedActivities++
        }

        override fun onActivityPaused(activity: Activity) {
            resumedActivities--
            if (resumedActivities == 0) {
                // Went to background
                updateUserOnlineStatus(false)
            }
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {}
    }
}