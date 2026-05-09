package com.techproject.harvestlink.data.chats

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.techproject.harvestlink.data.ConversationCacheEntity
import com.techproject.harvestlink.data.MessageEntity
import com.techproject.harvestlink.data.UserEntity

@Dao
interface ChatDao {
    @Query("SELECT * FROM message_cache WHERE conversation_id = :conversationId ORDER BY created_at DESC")
    suspend fun getMessagesForConversation(conversationId: String): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMessages(items: List<MessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMessage(item: MessageEntity)

    @Query("UPDATE message_cache SET server_id = :serverId, status = :status WHERE local_id = :localId")
    suspend fun markMessageSent(localId: String, serverId: String, status: String)

    @Query("UPDATE message_cache SET status = :status WHERE local_id = :localId")
    suspend fun updateMessageStatus(localId: String, status: String)

    @Query("SELECT local_id FROM message_cache WHERE server_id = :serverId LIMIT 1")
    suspend fun getLocalIdForServerId(serverId: String): String?

    @Query(
        "SELECT * FROM message_cache WHERE server_id IS NULL AND (:conversationId IS NULL OR conversation_id = :conversationId) ORDER BY created_at ASC"
    )
    suspend fun getPendingMessages(conversationId: String?): List<MessageEntity>

    @Query("SELECT * FROM conversation_cache WHERE viewer_id = :viewerId ORDER BY last_message_timestamp DESC")
    suspend fun getConversationsForViewer(viewerId: String): List<ConversationCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertConversations(items: List<ConversationCacheEntity>)

    @Query("DELETE FROM conversation_cache WHERE viewer_id = :viewerId")
    suspend fun clearConversationsForViewer(viewerId: String)

    @Transaction
    suspend fun replaceConversationsForViewer(viewerId: String, items: List<ConversationCacheEntity>) {
        clearConversationsForViewer(viewerId)
        upsertConversations(items)
    }

    @Query("SELECT * FROM user_cache WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUsers(items: List<UserEntity>)
}
