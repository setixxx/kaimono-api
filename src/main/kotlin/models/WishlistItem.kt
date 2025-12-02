package setixx.software.models

import java.time.Instant

data class WishlistItem(
    val id: Long,
    val userId: Long,
    val productId: Long,
    val productSizeId: Long?,
    val addedAt: Instant
)