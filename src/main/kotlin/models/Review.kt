package setixx.software.models

import java.time.Instant
import java.util.UUID

data class Review(
    val id: Long,
    val publicId: UUID,
    val userId: Long,
    val productId: Long,
    val orderId: Long?,
    val rating: Short,
    val comment: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)