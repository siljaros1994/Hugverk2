package `is`.hbv601.hugverk2.model

import java.time.LocalDateTime

data class Message(
    val senderId: Long,
    val receiverId: Long,
    val content: String,
    val timestamp: LocalDateTime
)