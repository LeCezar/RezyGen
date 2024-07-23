package models

import kotlinx.serialization.Serializable

@Serializable
data class TestResponse(
    val message: String
)