package com.techproject.harvestlink.data.chats

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.techproject.harvestlink.model.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(message: Message)

    @Query("SELECT * FROM Message WHERE senderId = :userId OR receiverId = :userId ORDER BY timestamp DESC;")
    fun getUsersMessages(userId:String): Flow<List<Message>>

    @Query("SELECT * FROM Message WHERE (senderId = :userId AND receiverId = :recipientId) OR (senderId = :recipientId AND receiverId = :userId) ORDER BY timestamp DESC;")
    fun getConversation(userId: String, recipientId: String):Flow<List<Message>>
}

