package setixx.software.models

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class Order(
    val id: Long,
    val publicId: UUID,
    val userId: Long,
    val addressId: Long,
    val statusId: Long,
    val totalAmount: BigDecimal,
    val createdAt: Instant,
    val updatedAt: Instant
)