package com.techproject.harvestlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.techproject.harvestlink.ui.HarvestLinkApp
import com.techproject.harvestlink.ui.theme.HarvestLinkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.d("SupabaseDebug", "URL: ${com.techproject.harvestlink.BuildConfig.SUPABASE_URL}, KEY: ${com.techproject.harvestlink.BuildConfig.SUPABASE_API_KEY}")
        enableEdgeToEdge()
        setContent {
            HarvestLinkTheme {
                HarvestLinkApp()
            }
        }
    }
}
