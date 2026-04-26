package com.techproject.harvestlink.ui.screens.chat

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.techproject.harvestlink.HarvestLinkApplication
import com.techproject.harvestlink.data.chats.ChatRepository
import com.techproject.harvestlink.model.ConversationDetails
import com.techproject.harvestlink.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatListViewModel(val messageRepo: ChatRepository): ViewModel() {

    val currentUser:String = "3230676f-fc7c-4ca2-a538-8cf168442831"

    private val _conversations = MutableStateFlow<List<ConversationDetails>>(emptyList())

    val conversations: StateFlow<List<ConversationDetails>> = _conversations

//    val userMessages = offlineMessageRepo.getUsersMessages(currentUser)
//        .map { UserMessages(it) }
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000L),
//            initialValue = UserMessages()
//        )

    init {
        viewModelScope.launch {
            _conversations.value = messageRepo.getConversation(currentUser)
        }
    }

    companion object {
        val Factory :ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as HarvestLinkApplication)
                val repo = application.container.repository
                ChatListViewModel(repo)
            }
        }
    }
}

data class UserMessages(
    val userMessages: List<Message> = emptyList(),
    val currentUser:String = "user1"
)
