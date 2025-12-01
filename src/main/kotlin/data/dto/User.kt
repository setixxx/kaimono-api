package setixx.software.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

@Serializable
data class UpdatePasswordResponse(
    val passwordUpdateStatus: String
)