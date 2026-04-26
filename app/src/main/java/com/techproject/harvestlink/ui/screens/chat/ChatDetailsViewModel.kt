package com.techproject.harvestlink.ui.screens.chat

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.techproject.harvestlink.HarvestLinkApplication
import com.techproject.harvestlink.data.chats.ChatRepository
import com.techproject.harvestlink.model.Message
import com.techproject.harvestlink.model.MessageStatus
import com.techproject.harvestlink.model.MessageType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class ChatDetailsViewModel(
    private val messageRepo: ChatRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val currentUserId = "3230676f-fc7c-4ca2-a538-8cf168442831"
    val recipientId: String = checkNotNull(savedStateHandle["recipientId"])
    val conversationId: String = checkNotNull(savedStateHandle["conversationId"])

    private val _messagesUi = MutableStateFlow(MessageUi())
    val messagesUi: StateFlow<MessageUi> = _messagesUi

    init {
        loadMessages(conversationId,recipientId)
    }

    private fun loadMessages(conversationId: String, recipientId: String){
        viewModelScope.launch{
            val messages = messageRepo.getUsersMessages(conversationId)
            val recipient = messageRepo.fetchUser(recipientId)

            _messagesUi.update {
                it.copy(
                    messages = messages,
                    recipientName = recipient.name,
                    isOnline = recipient.isOnline
                )
            }
        }
    }

    fun sendMessage(text: String, type: MessageType = MessageType.text) {
        if (text.isBlank()) return

        val tempId = UUID.randomUUID().toString()

        val pendingMessage = Message(
            id = "123456789",
            senderId = currentUserId,
            conversationId = conversationId,
            type = type,
            content = text,
            createdAt = System.currentTimeMillis(),
            status = MessageStatus.sending,
            metadata = tempId
        )

        _messagesUi.update { ui ->
            ui.copy(messages = listOf(pendingMessage) + ui.messages)
        }

        viewModelScope.launch {
            try {
                messageRepo.insertMessage(pendingMessage.copy(status = MessageStatus.sent))

                _messagesUi.update { ui ->
                    ui.copy(
                        messages = ui.messages.map { msg ->
                            if (msg.metadata == tempId) msg.copy(status = MessageStatus.sent) else msg
                        }
                    )
                }
            } catch (e: Exception) {
                _messagesUi.update { ui ->
                    ui.copy(
                        messages = ui.messages.map { msg ->
                            if (msg.metadata == tempId) msg.copy(status = MessageStatus.error) else msg
                        }
                    )
                }
                Log.e("RaceParticipant", "Error sending message",e)
            }
        }
    }

    companion object {
        val Factory :ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as HarvestLinkApplication)
                val repo = application.container.repository
                ChatDetailsViewModel(repo,this.createSavedStateHandle())
            }
        }
    }
}

data class MessageUi(
    val messages: List<Message> = emptyList(),
    val recipientName: String? = "",
    val recipientProfilePictureUrl: String? = "",
    val isOnline: Boolean = false
)