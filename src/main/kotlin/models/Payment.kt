package setixx.software.models

import java.math.BigDecimal
import java.time.Instant

data class Payment(
    val id: Long,
    val orderId: Long,
    val paymentMethodId: Long?,
    val amount: BigDecimal,
    val status: String,
    val transactionId: String?,
    val paidAt: Instant?,
    val createdAt: Instant
)
