package ui.conversation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import models.Conversation
import services.ChatService

sealed class ConversationsState {
    data object Loading : ConversationsState()
    data class Success(val chats: List<Conversation>) : ConversationsState()
    data class Error(val error: Throwable) : ConversationsState()
}

sealed class ConversationEvent {
    data class NavigateToChat(val chatId: Int) : ConversationEvent()
}

class ConversationModel(private val chatService: ChatService) : ScreenModel {

    val conversationsState = MutableStateFlow<ConversationsState>(ConversationsState.Loading)
    val events = MutableSharedFlow<ConversationEvent>()

    fun loadChats() {
        screenModelScope.launch {
            runCatching {
                chatService.getChats()
            }.onSuccess { chats ->
                conversationsState.update {
                    ConversationsState.Success(chats)
                }
            }.onFailure { error ->
                conversationsState.update {
                    ConversationsState.Error(error)
                }
            }
        }
    }

    fun createConversation() {
        screenModelScope.launch {
            runCatching {
                chatService.createChat("New Conversation")
            }.onSuccess { chat ->
                conversationsState.update { state ->
                    (state as ConversationsState.Success).copy(
                        chats = state.chats + chat
                    )
                }
                events.emit(ConversationEvent.NavigateToChat(chat.id))
            }.onFailure { error ->
                conversationsState.update {
                    ConversationsState.Error(error)
                }
            }
        }
    }

    fun deleteConversation(id: Int) {
        screenModelScope.launch {
            runCatching {
                chatService.deleteChat(id)
            }.onSuccess {
                conversationsState.update { state ->
                    (state as ConversationsState.Success).copy(
                        chats = state.chats.filter { it.id != id }
                    )
                }
            }.onFailure { error ->
                conversationsState.update {
                    ConversationsState.Error(error)
                }
            }
        }
    }
}