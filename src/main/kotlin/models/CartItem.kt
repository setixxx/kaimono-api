package setixx.software.models

import java.time.Instant

data class CartItem(
    val id: Long,
    val cartId: Long,
    val productId: Long,
    val productSizeId: Long,
    val quantity: Int,
    val addedAt: Instant
)
