package models

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    @SerialName("id")
    val id: Int,
    @SerialName("conversation")
    val conversationId: Int,
    @SerialName("content")
    val content: String,
    @SerialName("author")
    val author: Author,
    @SerialName("timestamp")
    val timestamp: Instant
)

@Serializable
enum class Author {
    @SerialName("USER")
    USER,

    @SerialName("ANTHROPIC")
    ANTHROPIC,

    @SerialName("OPENAI")
    OPENAI
}