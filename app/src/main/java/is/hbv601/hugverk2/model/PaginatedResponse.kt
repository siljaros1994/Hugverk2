package `is`.hbv601.hugverk2.model

data class PaginatedResponse<T>(
    val content: List<T>,
    val totalPages: Int,
    val totalElements: Int,
    val number: Int
)