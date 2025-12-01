package setixx.software.models

data class OrderStatus(
    val id: Long,
    val code: String,
    val name: String,
    val description: String?
)