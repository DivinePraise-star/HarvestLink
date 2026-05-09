package com.techproject.harvestlink.ui.screens.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.techproject.harvestlink.HarvestLinkApplication
import com.techproject.harvestlink.data.MoreData
import com.techproject.harvestlink.data.SessionManager
import com.techproject.harvestlink.data.chats.ChatRepository
import com.techproject.harvestlink.model.ConversationDetails
import com.techproject.harvestlink.model.Farmer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatListViewModel(
    val messageRepo: ChatRepository,
    private val sessionManager: SessionManager
): ViewModel() {

    private val _chatListUiState = MutableStateFlow(ChatListUiState())
    val chatListUiState: StateFlow<ChatListUiState> = _chatListUiState

    init {
        viewModelScope.launch {
            _chatListUiState.update { it.copy(isLoadingConversations = true) }
            try {
                messageRepo.syncPendingMessages()
                val session = sessionManager.sessionFlow.filterNotNull().first()
                val conversations = messageRepo.getConversations(session.userId)
                _chatListUiState.update {
                    it.copy(
                        conversations = conversations
                    )
                }
            } catch (e: Exception) {
                Log.e("ChatListViewModel", "Failed to load conversations", e)
            } finally {
                _chatListUiState.update { it.copy(isLoadingConversations = false) }
            }
        }
    }

    fun fetchFarmers(){
        viewModelScope.launch {
            _chatListUiState.update { it.copy(isLoadingFarmers = true) }
            try {
                val farmers = MoreData.fetchFarmers()
                val filteredFarmers = farmers.filter { it.id !in _chatListUiState.value.conversations.map { conversation -> conversation.userId } }
                _chatListUiState.update {
                    it.copy(
                        farmers = filteredFarmers
                    )
                }
            } catch (e: Exception) {
                Log.e("ChatListViewModel", "Failed to load farmers", e)
            } finally {
                _chatListUiState.update { it.copy(isLoadingFarmers = false) }
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

    suspend fun getOrCreateConversation(userId: String): String {
        val session = sessionManager.sessionFlow.filterNotNull().first()
        val conversationId = messageRepo.getOrCreateConversation(session.userId, userId)
        return conversationId
    }

    companion object {
        val Factory :ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as HarvestLinkApplication)
                val repo = application.container.repository
                ChatListViewModel(repo, application.sessionManager)
            }
        }
    }
}

data class ChatListUiState(
    val conversations: List<ConversationDetails> = emptyList(),
    val farmers: List<Farmer> = emptyList(),
    val isNewChat: Boolean = false,
    val isLoadingConversations: Boolean = true,
    val isLoadingFarmers: Boolean = false
)