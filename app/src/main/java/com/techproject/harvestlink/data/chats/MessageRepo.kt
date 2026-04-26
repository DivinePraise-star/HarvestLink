package com.techproject.harvestlink.data.chats

import com.techproject.harvestlink.data.SupabaseService.client
import com.techproject.harvestlink.model.Conversation
import com.techproject.harvestlink.model.ConversationDetails
import com.techproject.harvestlink.model.Message
import com.techproject.harvestlink.model.User
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.rpc

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

    override suspend fun insertMessage(message: Message) {
        client.postgrest
            .rpc("send_message", mapOf(
                "p_message_id"      to message.id,
                "p_conversation_id" to message.conversationId,
                "p_sender_id"       to message.senderId,
                "p_content"         to message.content,
                "p_type"            to message.type,
                "p_reply_to_id"     to message.replyToMessageId,
            ))
    }
}