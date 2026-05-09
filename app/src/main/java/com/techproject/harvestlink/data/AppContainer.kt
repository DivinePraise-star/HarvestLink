package com.techproject.harvestlink.data

import android.content.Context
import com.techproject.harvestlink.data.chats.ChatRepository
import com.techproject.harvestlink.data.chats.MessageRepo
import com.techproject.harvestlink.data.HarvestDatabase

interface AppContainer{
    val repository : ChatRepository
}

class DefaultAppContainer(private val context: Context): AppContainer{
    override val repository: ChatRepository by lazy{
        val database = HarvestDatabase.getDatabase(context)
        MessageRepo(database.chatDao())
    }

}