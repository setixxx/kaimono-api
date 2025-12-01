package setixx.software.models

import java.time.Instant

data class Notification(
    val id: Long,
    val userId: Long,
    val orderId: Long?,
    val type: String,
    val title: String,
    val message: String,
    val isRead: Boolean,
    val createdAt: Instant
)