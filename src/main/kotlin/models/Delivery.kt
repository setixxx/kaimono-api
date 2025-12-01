package setixx.software.models

import java.time.Instant
import java.time.LocalDate

data class Delivery(
    val id: Long,
    val orderId: Long,
    val addressId: Long,
    val trackingNumber: String?,
    val estimatedDeliveryDate: LocalDate?,
    val actualDeliveryDate: LocalDate?,
    val status: String,
    val createdAt: Instant,
    val updatedAt: Instant
)
