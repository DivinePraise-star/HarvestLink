package com.techproject.harvestlink.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.techproject.harvestlink.HarvestLinkApplication
import com.techproject.harvestlink.data.MoreData
import com.techproject.harvestlink.data.chats.ChatRepository
import com.techproject.harvestlink.model.ConversationDetails
import com.techproject.harvestlink.model.Farmer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatListViewModel(val messageRepo: ChatRepository): ViewModel() {

    val currentUser:String = "3230676f-fc7c-4ca2-a538-8cf168442831"

    private val _chatListUiState = MutableStateFlow(ChatListUiState())
    val chatListUiState: StateFlow<ChatListUiState> = _chatListUiState

    init {
        viewModelScope.launch {
            val conversations = messageRepo.getConversation(currentUser)
            _chatListUiState.update {
                it.copy(
                    conversations = conversations
                )
            }
        }
    }

    fun fetchFarmers(){
        viewModelScope.launch {
            val farmers = MoreData.fetchFarmers()
            val filteredFarmers = farmers.filter { it -> it.id !in _chatListUiState.value.conversations.map { it.userId } }
            _chatListUiState.update {
                it.copy(
                    farmers = filteredFarmers
                )
            }
        }
    }

    fun toggleNewChat(){
        _chatListUiState.update {
            it.copy(
                isNewChat = !it.isNewChat
            )
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

data class ChatListUiState(
    val conversations: List<ConversationDetails> = emptyList(),
    val farmers: List<Farmer> = emptyList(),
    val isNewChat: Boolean = false
)