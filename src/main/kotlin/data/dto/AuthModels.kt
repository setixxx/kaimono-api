package setixx.software.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class RegisterUserRequest(
    val name: String,
    val surname: String? = null,
    val phone: String,
    val email: String,
    val birthday: String? = null,
    val gender: String? = null,
    val password: String
)

@Serializable
data class RegisterResponse(
    @SerialName("public_id")
    val publicId: String
)

@Serializable
data class LoginUserRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val uuid: String
)