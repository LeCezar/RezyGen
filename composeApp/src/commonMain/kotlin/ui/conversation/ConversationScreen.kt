package ui.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import models.Conversation
import ui.chat.ChatScreen
import utils.koinScreenModelFixed

class ConversationScreen : Screen {
    override val key = "conversations"

    @Composable
    override fun Content() {
        val localNavigator = LocalNavigator.currentOrThrow

        ConversationScreenContent(
            model = koinScreenModelFixed(),
            onNavigateToChat = { chatId ->
                localNavigator.push(ChatScreen(chatId))
            }
        )
    }

}

@Composable
private fun ConversationScreenContent(
    onNavigateToChat: (chatId: Int) -> Unit,
    model: ConversationModel
) {

    val state by model.conversationsState.collectAsState()

    LaunchedEffect(Unit) {
        model.loadChats()
    }

    LaunchedEffect(model) {
        model.events.collect {
            when (it) {
                is ConversationEvent.NavigateToChat -> onNavigateToChat(it.chatId)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    model.createConversation()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = null
                )
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(it)) {
            when (val s = state) {
                ConversationsState.Loading -> {
                    CircularProgressIndicator()
                }

                is ConversationsState.Error -> {
                    Text(
                        text = "Error: ${s.error.message}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                is ConversationsState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(
                            items = s.chats,
                            key = { it.id }
                        ) { chat ->
                            ConversationCard(
                                modifier = Modifier.fillMaxWidth(),
                                conversation = chat,
                                onClick = { onNavigateToChat(chat.id) },
                                onDeleteClick = { model.deleteConversation(chat.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversationCard(
    modifier: Modifier = Modifier,
    conversation: Conversation,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick).padding(16.dp),
    ) {
        Row(
            Modifier.height(IntrinsicSize.Max)
        ) {
            Column(
                modifier = Modifier.weight(1f).align(Alignment.CenterVertically).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = conversation.title,
                    style = MaterialTheme.typography.bodyLarge,
                )

                conversation.firstMessageContent?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Icon(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically)
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .clickable {
                        onDeleteClick()
                    }.padding(16.dp),
                imageVector = Icons.Default.Delete,
                tint = MaterialTheme.colorScheme.error,
                contentDescription = null
            )
        }
    }
}