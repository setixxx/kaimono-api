package setixx.software.models

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class Product(
    val id: Long,
    val publicId: UUID,
    val name: String,
    val description: String,
    val basePrice: BigDecimal,
    val isAvailable: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)
