package com.techproject.harvestlink.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String = "",
    @SerialName("conversationid")
    val conversationId: String,
    @SerialName("senderid")
    val senderId: String,
    val content: String,
    val type: MessageType,
    @SerialName("createdat")
    val createdAt: Long,

    val status: MessageStatus,

    // optional but useful
    val replyToMessageId: String? = null,
    val metadata: String? = null // JSON for attachments, etc.
)

@Serializable
data class Conversation(
    @SerialName("conversationid")
    val id: String,
    @SerialName("lastmessageid")
    val lastMessageId: String? = null,
    @SerialName("lastmessagepreview")
    val lastMessagePreview: String? = null,
    @SerialName("lastmessagetimestamp")
    val lastMessageTimestamp: Long? = 0,
    @SerialName("lastsenderid")
    val lastSenderId: String? = null,
    @SerialName("otheruserid")
    val userId: String,
    @SerialName("otherusername")
    val userName: String,
    @SerialName("unreadcount")
    val unreadCount: Int?,
    @SerialName("lastreadmessageid")
    val lastReadMessageId: String? = null,
    @SerialName("lastreadat")
    val lastReadAt: Long? = null

)

data class ConversationDetails(
    val conversationId: String,
    val userName:String,
    val userId: String,
    val profilePictureUrl:String = "",
    val lastMessage:String?,
    val lastMessageTimestamp:Long,
    val unreadCount:Int? = null
)

//data class Message(
//    @PrimaryKey val id: String,
//    val conversationId: String,
//    val senderId: String,
//    val content: String,
//    val type: MessageType,
//    val createdAt: Long,
//    val editedAt: Long? = null,
//    val deletedAt: Long? = null,
//    val status: MessageStatus = MessageStatus.SENDING
//)


enum class MessageType { text, image, video, file }
enum class MessageStatus { sending, sent, delivered, read, error }


