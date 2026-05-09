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
import com.techproject.harvestlink.data.SessionManager
import com.techproject.harvestlink.data.chats.ChatRepository
import com.techproject.harvestlink.model.Message
import com.techproject.harvestlink.model.MessageStatus
import com.techproject.harvestlink.model.MessageType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class ChatDetailsViewModel(
    private val messageRepo: ChatRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    var currentUserId: String = ""
    val recipientId: String = checkNotNull(savedStateHandle["recipientId"])
    val conversationId: String = checkNotNull(savedStateHandle["conversationId"])

    private val _messagesUi = MutableStateFlow(MessageUi())
    val messagesUi: StateFlow<MessageUi> = _messagesUi

    init {
        viewModelScope.launch {
            val session = sessionManager.sessionFlow.filterNotNull().first()
            currentUserId = session.userId

            loadMessages(conversationId, recipientId)
            observeRecipientOnlineStatus()
        }
    }

    private fun loadMessages(conversationId: String, recipientId: String){
        viewModelScope.launch{
            _messagesUi.update { it.copy(isLoading = true) }
            try {
                messageRepo.markMessagesAsRead(conversationId,currentUserId)
                messageRepo.syncPendingMessages(conversationId)
                val messages = messageRepo.getUsersMessages(conversationId)
                val recipient = messageRepo.fetchUser(recipientId)

                _messagesUi.update {
                    it.copy(
                        messages = messages,
                        recipientName = recipient.name,
                        isOnline = recipient.isOnline
                    )
                }
            } finally {
                _messagesUi.update { it.copy(isLoading = false) }
            }
        }
    }

    fun sendMessage(text: String, type: MessageType = MessageType.text) {
        if (text.isBlank()) return
        if (currentUserId.isBlank()) {
            Log.w("ChatDetailsViewModel", "Missing session user id; message not sent")
            return
        }

        val tempId = UUID.randomUUID().toString()

        val pendingMessage = Message(
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
                val result = messageRepo.insertMessage(pendingMessage.copy(status = MessageStatus.sending))

                if (result.isSynced) {
                    _messagesUi.update { ui ->
                        ui.copy(
                            messages = ui.messages.map { msg ->
                                if (msg.metadata == tempId) {
                                    msg.copy(
                                        id = result.messageId,
                                        status = MessageStatus.sent
                                    )
                                } else msg
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatDetailsViewModel", "Error sending message",e)
            }
        }
    }

    private fun observeRecipientOnlineStatus() {
        viewModelScope.launch {
            // Get initial online status (if needed)
            val initialUser = messageRepo.fetchUser(recipientId)
            _messagesUi.update { it.copy(isOnline = initialUser.isOnline) }

            // Listen for changes
            messageRepo.observeUserOnlineStatus(recipientId).collect { isOnline ->
                _messagesUi.update { it.copy(isOnline = isOnline) }
            }
        }
    }

    companion object {
        val Factory :ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as HarvestLinkApplication)
                val repo = application.container.repository
                ChatDetailsViewModel(repo, application.sessionManager, this.createSavedStateHandle())
            }
        }
    }
}

data class MessageUi(
    val messages: List<Message> = emptyList(),
    val recipientName: String? = "",
    val recipientProfilePictureUrl: String? = "",
    val isOnline: Boolean = false,
    val isLoading: Boolean = true
)