package setixx.software.models

import java.math.BigDecimal
import java.time.Instant

data class ProductSize(
    val id: Long,
    val productId: Long,
    val size: String,
    val stockQuantity: Int,
    val priceModifier: BigDecimal,
    val createdAt: Instant,
    val updatedAt: Instant
)
