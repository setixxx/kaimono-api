package setixx.software.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class UpdatePasswordRequest(
    @SerialName("old_password")
    val oldPassword: String,
    @SerialName("new_password")
    val newPassword: String
)

@Serializable
data class UpdatePasswordResponse(
    @SerialName("password_update_status")
    val passwordUpdateStatus: String
)

@Serializable
data class UpdateUserInfoRequest(
    val name: String? = null,
    val surname: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val birthDate: String? = null,
    val gender: String? = null,
)

@Serializable
data class UpdateUserInfoResponse(
    @SerialName("user_update_status")
    val userUpdateStatus: String
)