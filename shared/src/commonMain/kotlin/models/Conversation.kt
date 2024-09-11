package models

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Conversation(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("first_message")
    val firstMessageContent: String? = null,
    @SerialName("created_at")
    val createdAt: Instant
)