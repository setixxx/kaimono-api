package setixx.software.models

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.UUID

data class User(
    val id: Long,
    val publicId: UUID,
    val name: String,
    val surname: String?,
    val phone: String,
    val email: String,
    val birthday: LocalDate?,
    val gender: String,
    val passwordHash: String
)