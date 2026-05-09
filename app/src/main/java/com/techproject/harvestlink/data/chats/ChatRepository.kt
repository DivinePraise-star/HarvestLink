package com.techproject.harvestlink.data.chats

import com.techproject.harvestlink.model.ConversationDetails
import com.techproject.harvestlink.model.Message
import com.techproject.harvestlink.model.User

interface ChatRepository {
    suspend fun fetchUser(userId: String): User
    suspend fun getUsersMessages(conversationId: String): List<Message>
    suspend fun getConversations(userId: String):List<ConversationDetails>
    suspend fun getOrCreateConversation(userId: String, user2Id: String): String
    suspend fun insertMessage(message: Message): SendMessageResult
    suspend fun syncPendingMessages(conversationId: String? = null)
}

data class SendMessageResult(
    val messageId: String,
    val isSynced: Boolean
)