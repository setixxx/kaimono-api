package setixx.software.models

import java.time.Instant

data class PaymentMethod(
    val id: Long,
    val userId: Long,
    val cardNumberLast4: String,
    val cardHolderName: String,
    val expiryMonth: Short,
    val expiryYear: Short,
    val isDefault: Boolean,
    val createdAt: Instant
)
