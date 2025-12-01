package setixx.software.models

import java.time.Instant

data class Category(
    val id: Long,
    val name: String,
    val description: String?,
    val parentId: Long?,
    val createdAt: Instant,
    val updatedAt: Instant
)
