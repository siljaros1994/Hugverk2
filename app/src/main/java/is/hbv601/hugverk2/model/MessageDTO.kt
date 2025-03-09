package `is`.hbv601.hugverk2.model

import java.time.LocalDateTime

data class MessageDTO(
    val id: Long,
    val senderId: Long,
    val receiverId: Long,
    val content: String,
    val timestamp: LocalDateTime
)