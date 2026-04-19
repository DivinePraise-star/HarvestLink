package com.techproject.harvestlink.data.chats

import com.techproject.harvestlink.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getUsersMessages(userId:String): Flow<List<Message>>
    fun getConversation(userId: String, recipientId: String): Flow<List<Message>>
    suspend fun insertMessage(message: Message)
}