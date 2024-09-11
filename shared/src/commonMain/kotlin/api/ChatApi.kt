package api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.discardRemaining
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import models.Conversation
import models.Message

class ChatApi(private val client: HttpClient) {

    suspend fun getChats(): List<Conversation> = client.get(
        url = Url(BASE_URL + "conversations")
    ).body() ?: emptyList()

    suspend fun getChatMessages(chatId: Int): List<Message> = client.get(
        url = Url(BASE_URL + "conversations/$chatId/messages")
    ).body() ?: emptyList()

    suspend fun sendMessage(chatId: Int, message: String): List<Message> = client.post(
        url = Url(BASE_URL + "conversations/$chatId/add_message/"),
    ) {
        contentType(ContentType.Application.Json)
        setBody(
            body = buildJsonObject {
                put("content", message)
            }
        )
    }.bodyAsText().run {
        println(this)
        Json.parseToJsonElement(this).jsonObject.run {
            listOf(
                Json.decodeFromJsonElement<Message>(get("user_message")!!),
                Json.decodeFromJsonElement<Message>(get("response_message")!!)
            )
        }
    }

    suspend fun createChat(title: String): Conversation = client.post(
        url = Url(BASE_URL + "conversations/")
    ) {
        contentType(ContentType.Application.Json)
        setBody(
            buildJsonObject {
                put("title", title)
            }
        )
    }.body()

    suspend fun deleteChat(chatId: Int) = client.delete(
        url = Url(BASE_URL + "conversations/$chatId/")
    ).discardRemaining()
}