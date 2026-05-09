package com.techproject.harvestlink.data.chats

import android.util.Log
import com.techproject.harvestlink.data.SupabaseService.client
import com.techproject.harvestlink.data.toEntity
import com.techproject.harvestlink.data.toLocalEntity
import com.techproject.harvestlink.data.toModel
import com.techproject.harvestlink.model.Conversation
import com.techproject.harvestlink.model.ConversationDetails
import com.techproject.harvestlink.model.Message
import com.techproject.harvestlink.model.MessageStatus
import com.techproject.harvestlink.model.User
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.io.IOException
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException

class MessageRepo(
    private val chatDao: ChatDao
): ChatRepository {
    override suspend fun fetchUser(userId: String): User {
        return try {
            val remote = client.postgrest.from("users")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }.decodeSingle<User>()
            chatDao.upsertUsers(listOf(remote.toEntity()))
            remote
        } catch (e: Exception) {
            chatDao.getUserById(userId)?.toModel() ?: User(
                id = userId,
                name = "Unknown",
                email = "",
                phoneNumber = "",
                isOnline = false
            )
        }
    }

    override fun observeUserOnlineStatus(userId: String): Flow<Boolean> {

        val channel = client.realtime.channel("user-status-$userId")

        val statusFlow = channel.postgresChangeFlow<PostgresAction.Update>(
            schema = "public"
        ) {
            table = "users"
            filter(column = "id", operator = FilterOperator.EQ, value = userId)
        }.map { action ->
            action.record["is_online"]?.jsonPrimitive?.booleanOrNull ?: false
        }

        return statusFlow
    }

    override suspend fun markMessagesAsRead(conversationId: String,userId: String){
        val payload = buildJsonObject {
            put("p_user_id", userId)
            put("p_conversation_id", conversationId)
        }
        try {
            client.postgrest.rpc("mark_conversation_read", payload)
        } catch (e: RestException) {
            Log.e("SupabaseRPC", "Supabase RPC Error: ${e.message}")
        } catch (e: HttpRequestTimeoutException) {
            Log.w("SupabaseRPC", "Request timed out. Please try again.")
        } catch (e: IOException) {
            Log.e("SupabaseRPC", "Network unavailable. Check your connection.")
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e("SupabaseRPC", "Unexpected error: ${e.localizedMessage}")
        }
    }

    override suspend fun getUsersMessages(conversationId: String): List<Message> {
        return try {
            val remote = client.postgrest.from("messages")
                .select {
                    filter { eq("conversationid", conversationId) }
                    order("createdat", Order.DESCENDING)
                }.decodeList<Message>()
            remote.forEach { message ->
                val serverId = message.id
                if (serverId.isBlank()) {
                    val localId = UUID.randomUUID().toString()
                    chatDao.upsertMessage(message.toLocalEntity(localId))
                } else {
                    val existingLocalId = chatDao.getLocalIdForServerId(serverId)
                    val localId = existingLocalId ?: serverId
                    chatDao.upsertMessage(message.toLocalEntity(localId, serverId = serverId))
                }
            }
            chatDao.getMessagesForConversation(conversationId).map { it.toModel() }
        } catch (e: Exception) {
            chatDao.getMessagesForConversation(conversationId).map { it.toModel() }
        }
    }

    override suspend fun getConversations(userId: String): List<ConversationDetails> {
        return try {
            val conversation = client.postgrest.from("user_chat_list")
                .select {
                    filter {
                        eq("viewerid", userId)
                    }
                    order("lastmessagetimestamp", Order.DESCENDING)
                }.decodeList<Conversation>()

            val mapped = conversation.map {
                ConversationDetails(
                    conversationId = it.id,
                    userName = it.userName,
                    userId = it.userId,
                    lastMessage = it.lastMessagePreview,
                    lastMessageTimestamp = it.lastMessageTimestamp ?: 0,
                    unreadCount = it.unreadCount
                )
            }
            chatDao.replaceConversationsForViewer(userId, mapped.map { it.toEntity(userId) })
            mapped
        } catch (e: Exception) {
            chatDao.getConversationsForViewer(userId).map { it.toModel() }
        }
    }

    override suspend fun getOrCreateConversation(userId: String, user2Id: String): String{
        val payload = buildJsonObject {
            put("user_id_1", userId)
            put("user_id_2", user2Id)
        }
        val result = client.postgrest.rpc("get_or_create_direct_conversation",payload)

        return result.decodeAs<String>()
    }

    override suspend fun insertMessage(message: Message): SendMessageResult {
        val localId = message.metadata?.ifBlank { null } ?: UUID.randomUUID().toString()
        val localEntity = message.toLocalEntity(localId)
        chatDao.upsertMessage(localEntity)

        return try {
            val serverId = sendMessageToServer(message)
            chatDao.markMessageSent(localId, serverId, MessageStatus.sent.name)
            SendMessageResult(serverId, true)
        } catch (e: Exception) {
            chatDao.updateMessageStatus(localId, MessageStatus.sending.name)
            SendMessageResult(localId, false)
        }
    }

    override suspend fun syncPendingMessages(conversationId: String?) {
        val pending = chatDao.getPendingMessages(conversationId)
        pending.forEach { entity ->
            val message = entity.toModel()
            try {
                val serverId = sendMessageToServer(message)
                chatDao.markMessageSent(entity.localId, serverId, MessageStatus.sent.name)
            } catch (_: Exception) {
            }
        }
    }

    private suspend fun sendMessageToServer(message: Message): String {
        val payload = buildJsonObject {
            put("p_conversation_id", message.conversationId)
            put("p_sender_id", message.senderId)
            put("p_content", message.content)
            put("p_type", message.type.name)
            put("p_reply_to_id", message.replyToMessageId)
        }
        val result = client.postgrest.rpc("send_message", payload)
        return result.decodeAs<String>()
    }
}