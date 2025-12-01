package setixx.software.models

import java.math.BigDecimal

data class OrderItem(
    val id: Long,
    val orderId: Long,
    val productId: Long,
    val productSizeId: Long,
    val quantity: Int,
    val priceAtPurchase: BigDecimal,
    val subtotal: BigDecimal
)