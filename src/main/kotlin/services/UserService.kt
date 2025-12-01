package setixx.software.services

import setixx.software.data.dto.LoginResponse
import setixx.software.data.dto.LoginUserRequest
import setixx.software.data.dto.RegisterUserRequest
import setixx.software.data.dto.UpdatePasswordRequest
import setixx.software.data.dto.UpdatePasswordResponse
import setixx.software.data.repositories.UserRepository
import setixx.software.models.User
import setixx.software.utils.hashString
import java.security.MessageDigest
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UserService(
    private val userRepository: UserRepository
) {

    suspend fun register(request: RegisterUserRequest): User {
        if (userRepository.findByEmail(request.email) != null) {
            throw IllegalArgumentException("User email already exists")
        }

        if (request.password.length < 6) {
            throw IllegalArgumentException("Password is too short")
        }

        val passwordHash = hashString(request.password)

        val birthdayDate = request.birthday?.let {
            try {
                LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
            } catch (e: Exception) {
                null
            }
        }

        return userRepository.registerUser(
            name = request.name,
            surname = request.surname,
            phone = request.phone,
            email = request.email,
            birthday = birthdayDate,
            gender = "Male",
            passwordHash = passwordHash
        )
    }

    suspend fun login(request: LoginUserRequest): User {
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("User email doesn't exist")

        val passwordHash = hashString(request.password)

        if (user.passwordHash == passwordHash) {
            return user
        } else
            throw IllegalArgumentException("User password doesn't match")
    }

    suspend fun updatePassword(
        email: String,
        request: UpdatePasswordRequest,
    ) {
        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("User email doesn't exist")
        val oldPasswordHash = hashString(request.oldPassword)
        if (user.passwordHash != oldPasswordHash) {
            throw IllegalArgumentException("User password doesn't match")
        } else {
            userRepository.updatePassword(email, request.newPassword)
        }
    }

}