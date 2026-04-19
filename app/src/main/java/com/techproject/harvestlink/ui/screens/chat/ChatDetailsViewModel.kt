package com.techproject.harvestlink.ui.screens.chat

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
import com.techproject.harvestlink.model.MessageType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class ChatDetailsViewModel(
    private val offlineMessageRepo: ChatRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val currentUserId = "user1"
    val recipientId: String = checkNotNull(savedStateHandle["recipientId"])

    val userConversation =  offlineMessageRepo.getConversation(currentUserId, recipientId)
        .map { UserConversation(
            conversation = it,
            recipientId = recipientId
            )}
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UserConversation(recipientId = recipientId)
        )

    fun sendMessage(text: String, type: MessageType = MessageType.TEXT) {
        if (text.isNotBlank()) {
            val newMessage = Message(
                id = UUID.randomUUID().toString(),
                senderId = currentUserId,
                receiverId = recipientId,
                type = type,
                content = text,
                timestamp = System.currentTimeMillis()
            )
            viewModelScope.launch {
                offlineMessageRepo.insertMessage(newMessage)
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

data class UserConversation(
    val conversation: List<Message> = emptyList(),
    val currentUser: String = "user1",
    val recipientId: String = ""
)
