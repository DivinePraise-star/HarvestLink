package com.techproject.harvestlink.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.techproject.harvestlink.HarvestLinkApplication
import com.techproject.harvestlink.data.chats.ChatRepository
import com.techproject.harvestlink.model.Message
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
class ChatListViewModel(val offlineMessageRepo: ChatRepository): ViewModel() {

    private val currentUser:String = "user1"

    val userMessages = offlineMessageRepo.getUsersMessages(currentUser)
        .map { UserMessages(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UserMessages()
        )

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
