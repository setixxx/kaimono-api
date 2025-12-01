package setixx.software.models

import java.time.Instant

data class Cart(
    val id: Long,
    val userId: Long,
    val createdAt: Instant,
    val updatedAt: Instant
)
