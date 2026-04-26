package com.techproject.harvestlink.data.chats

import com.techproject.harvestlink.model.ConversationDetails
import com.techproject.harvestlink.model.Message
import com.techproject.harvestlink.model.User

interface ChatRepository {
    suspend fun fetchUser(userId: String): User
    suspend fun getUsersMessages(conversationId: String): List<Message>
    suspend fun getConversation(userId: String):List<ConversationDetails>
    suspend fun insertMessage(message: Message): String
}