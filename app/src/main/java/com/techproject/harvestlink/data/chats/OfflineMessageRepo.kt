package com.techproject.harvestlink.data.chats

import com.techproject.harvestlink.model.Message
import kotlinx.coroutines.flow.Flow

class OfflineMessageRepo(val messageDao: MessageDao): ChatRepository {
    override fun getUsersMessages(userId: String): Flow<List<Message>> = messageDao.getUsersMessages(userId)

    override fun getConversation(
        userId: String,
        recipientId: String
    ): Flow<List<Message>> = messageDao.getConversation(userId,recipientId)

    override suspend fun insertMessage(message: Message) = messageDao.insert(message)

}