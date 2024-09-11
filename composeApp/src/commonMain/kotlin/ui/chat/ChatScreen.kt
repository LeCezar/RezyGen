package ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import models.Author
import models.Message
import org.koin.core.parameter.parametersOf
import utils.koinScreenModelFixed

class ChatScreen(private val chatId: Int) : Screen {
    override val key = "chat/$chatId"

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        ChatScreenContent(
            onBack = {
                navigator.pop()
            },
            model = koinScreenModelFixed(
                parameters = { parametersOf(chatId) }
            )
        )
    }
}

@Composable
private fun ChatScreenContent(
    onBack: () -> Unit,
    model: ChatModel
) {
    val state by model.chatState.collectAsState()

    Scaffold(
        topBar = {
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = {
                        onBack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        bottomBar = {
            var text by remember { mutableStateOf("") }

            LaunchedEffect(state) {
                if (state is ChatState.Chatting.LoadingMessage) {
                    text = ""
                }
            }

            if (state is ChatState.Chatting.Success) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = text,
                    onValueChange = {
                        text = it
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onAny = {
                            model.sendMessage(text)
                        }
                    ),
                    label = { Text("Type a message") },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                model.sendMessage(text)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send"
                            )
                        }
                    }
                )
            }
        },

        ) {
        Box(
            modifier = Modifier.padding(it)
        ) {
            when (val s = state) {
                is ChatState.Error -> {
                    Text(text = "Error: ${s.error}")
                }

                ChatState.Loading -> {
                    CircularProgressIndicator()
                }

                is ChatState.Chatting -> {
                    val lazyListState = rememberLazyListState()

                    LaunchedEffect(s.messages) {
                        if (s.messages.isNotEmpty()) {
                            lazyListState.scrollToItem(s.messages.lastIndex)
                        }
                    }

                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = s.messages
                        ) { message ->
                            ChatEntry(message = message)
                        }

                        if (s is ChatState.Chatting.LoadingMessage) {
                            item {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }

                        if (s is ChatState.Chatting.Error) {
                            item {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Error: ${s.error}",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatEntry(
    modifier: Modifier = Modifier,
    message: Message
) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        Card(
            modifier = modifier
                .widthIn(max = maxWidth * 0.6f)
                .align(
                    when (message.author) {
                        Author.USER -> Alignment.CenterEnd
                        else -> Alignment.CenterStart
                    }
                ),
            colors = CardDefaults.cardColors().copy(
                containerColor = when (message.author) {
                    Author.USER -> MaterialTheme.colorScheme.surfaceContainer
                    else -> MaterialTheme.colorScheme.surfaceDim
                }
            )
        ) {
            Column(Modifier.padding(8.dp)) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    modifier = Modifier.padding(top = 4.dp)
                        .align(
                            when (message.author) {
                                Author.USER -> Alignment.End
                                else -> Alignment.Start
                            }
                        ).alpha(0.8f),
                    text = message.timestamp.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}