package setixx.software.models

import java.time.Instant

data class Token(
    val id: Long,
    val userId: Long,
    val tokenHash: String,
    val expiresAt: Instant,
    val createdAt: Instant,
    val deviceInfo: String
)
