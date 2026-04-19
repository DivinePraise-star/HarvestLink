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
        enableEdgeToEdge()
        setContent {
            HarvestLinkTheme {
                HarvestLinkApp()
            }
        }
    }
}
