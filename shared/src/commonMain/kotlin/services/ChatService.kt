package services

import api.ChatApi
import models.Conversation
import models.Message


class ChatService(private val chatApi: ChatApi) {

    suspend fun getChats(): List<Conversation> = chatApi.getChats()

    suspend fun getChatMessages(chatId: Int): List<Message> = chatApi.getChatMessages(chatId)

    suspend fun sendMessage(chatId: Int, message: String): List<Message> =
        chatApi.sendMessage(chatId, message)

    suspend fun createChat(title: String): Conversation = chatApi.createChat(title)

    suspend fun deleteChat(chatId: Int) = chatApi.deleteChat(chatId)
}