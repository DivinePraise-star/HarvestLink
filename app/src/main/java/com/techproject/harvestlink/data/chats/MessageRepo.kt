package com.techproject.harvestlink.data.chats

import com.techproject.harvestlink.data.SupabaseService.client
import com.techproject.harvestlink.model.Conversation
import com.techproject.harvestlink.model.ConversationDetails
import com.techproject.harvestlink.model.Message
import com.techproject.harvestlink.model.User
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class MessageRepo(): ChatRepository {
    override suspend fun fetchUser(userId: String): User {
        return client.postgrest.from("users")
            .select {
                filter {
                    eq("id",userId)
                }
            }.decodeSingle<User>()
    }

    override suspend fun getUsersMessages(conversationId: String): List<Message> {
        return client.postgrest.from("messages")
            .select {
                filter { eq("conversationid",conversationId) }
                order("createdat",Order.DESCENDING)
            }.decodeList<Message>()
    }

    override suspend fun getConversation(userId: String): List<ConversationDetails> {
        val conversation = client.postgrest.from("user_chat_list")
            .select {
                filter {
                    eq("viewerid", userId)
                }
                order("lastmessagetimestamp", Order.DESCENDING)
            }.decodeList<Conversation>()

        return conversation.map{
            ConversationDetails(
                conversationId = it.id,
                userName = it.userName,
                userId = it.userId,
                lastMessage = it.lastMessagePreview,
                lastMessageTimestamp = it.lastMessageTimestamp ?: 0,
                unreadCount = it.unreadCount
            )
        }
    }

    override suspend fun insertMessage(message: Message): String {
        val payload = buildJsonObject {
            put("p_conversation_id", message.conversationId)
            put("p_sender_id", message.senderId)
            put("p_content", message.content)
            put("p_type", message.type.name)
            put("p_reply_to_id", message.replyToMessageId)
        }
        val result = client.postgrest.rpc("send_message",payload)

        return result.decodeAs<String>()
    }
}