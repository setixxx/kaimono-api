package setixx.software.models

import java.time.Instant

data class ProductImage(
    val id: Long,
    val productId: Long,
    val imageUrl: String,
    val displayOrder: Int?,
    val isPrimary: Boolean?,
    val createdAt: Instant
)
