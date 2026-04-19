package com.techproject.harvestlink

import android.app.Application
import com.techproject.harvestlink.data.AppContainer
import com.techproject.harvestlink.data.DefaultAppContainer

class HarvestLinkApplication: Application(){
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}