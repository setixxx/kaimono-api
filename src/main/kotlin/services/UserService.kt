package setixx.software.services

import setixx.software.data.dto.RegisterUserRequest
import setixx.software.data.repositories.UserRepository
import setixx.software.models.User
import java.security.MessageDigest
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UserService(private val userRepository: UserRepository) {

    suspend fun register(request: RegisterUserRequest): User {
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
            gender = request.gender,
            passwordHash = passwordHash
        )
    }

    private fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}