package com.techproject.harvestlink.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Message(
    @PrimaryKey
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "", // Or chatId for group chats
    val content: String  = "",
    val timestamp: Long = 0L,
    val type: MessageType = MessageType.TEXT,
    val status: MessageStatus = MessageStatus.SENDING
)

enum class MessageType { TEXT, IMAGE, VIDEO, FILE }
enum class MessageStatus { SENDING, SENT, DELIVERED, READ }


