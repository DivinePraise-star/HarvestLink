package com.techproject.harvestlink.data

import android.content.Context
import com.techproject.harvestlink.data.chats.ChatDatabase
import com.techproject.harvestlink.data.chats.ChatRepository
import com.techproject.harvestlink.data.chats.OfflineMessageRepo

interface AppContainer{
    val repository : ChatRepository
}

class DefaultAppContainer(private val context: Context): AppContainer{
    override val repository: ChatRepository by lazy{
        OfflineMessageRepo(ChatDatabase.getDatabase(context).getDao())
    }

}