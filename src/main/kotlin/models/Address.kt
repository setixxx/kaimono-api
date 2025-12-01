package setixx.software.models

import java.time.Instant

data class Address(
    val id: Long,
    val userId: Long,
    val city: String,
    val street: String,
    val house: String,
    val apartment: String?,
    val zipCode: String,
    val additionalInfo: String?,
    val isDefault: Boolean,
    val createdAt: Instant
)
