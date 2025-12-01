package setixx.software.models

import java.time.Instant

data class Review(
    val id: Long,
    val userId: Long,
    val productId: Long,
    val orderId: Long?,
    val rating: Short,
    val comment: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)