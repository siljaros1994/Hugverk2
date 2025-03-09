package `is`.hbv601.hugverk2.model

data class MessageForm(
    val senderId: Long,
    val receiverId: Long,
    val text: String
)
