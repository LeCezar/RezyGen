package ui.chat

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import models.Author
import models.Message
import services.ChatService

sealed class ChatState {
    data object Loading : ChatState()

    sealed class Chatting(
        open val messages: List<Message>
    ) : ChatState() {

        data class LoadingMessage(
            override val messages: List<Message>
        ) : Chatting(messages)

        data class Error(
            override val messages: List<Message>,
            val error: Throwable
        ) : Chatting(messages)

        data class Success(
            override val messages: List<Message>
        ) : Chatting(messages)
    }

    data class Error(val error: Throwable) : ChatState()
}

class ChatModel(
    private val chatId: Int,
    private val chatService: ChatService
) : ScreenModel {

    val chatState = MutableStateFlow<ChatState>(ChatState.Loading)

    init {
        loadMessages()
    }

    fun loadMessages() {
        screenModelScope.launch {
            runCatching {
                chatService.getChatMessages(chatId)
            }.onSuccess {
                chatState.value = ChatState.Chatting.Success(it)
            }.onFailure {
                chatState.value = ChatState.Error(it)
            }
        }
    }

    fun sendMessage(content: String) {
        screenModelScope.launch {
            val temporaryMessage = Message(
                id = -1,
                conversationId = chatId,
                content = content,
                author = Author.USER,
                timestamp = Clock.System.now()
            )

            chatState.update {
                val state = it as ChatState.Chatting
                ChatState.Chatting.LoadingMessage(state.messages + temporaryMessage)
            }

            runCatching {
                chatService.sendMessage(chatId, content)
            }.onSuccess { newMessages ->
                chatState.update {
                    val state = it as ChatState.Chatting.LoadingMessage
                    ChatState.Chatting.Success(state.messages.dropLast(1) + newMessages)
                }
            }.onFailure { error ->
                chatState.update {
                    val state = it as ChatState.Chatting.LoadingMessage
                    ChatState.Chatting.Error(state.messages.dropLast(1), error)
                }
            }
        }
    }
}