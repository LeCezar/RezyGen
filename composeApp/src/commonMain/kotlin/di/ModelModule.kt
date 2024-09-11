package di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import ui.chat.ChatModel
import ui.conversation.ConversationModel

val modelModule = module {
    factoryOf(::ConversationModel)

    factory { (id: Int) ->
        ChatModel(
            chatId = id,
            chatService = get()
        )
    }
}