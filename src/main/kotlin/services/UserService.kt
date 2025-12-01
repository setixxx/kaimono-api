package setixx.software.services

import com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date
import setixx.software.data.dto.LoginResponse
import setixx.software.data.dto.LoginUserRequest
import setixx.software.data.dto.RegisterUserRequest
import setixx.software.data.dto.UpdatePasswordRequest
import setixx.software.data.dto.UpdatePasswordResponse
import setixx.software.data.dto.UpdateUserInfoRequest
import setixx.software.data.repositories.UserRepository
import setixx.software.models.User
import setixx.software.utils.dateParse
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
        if (request.name.isEmpty()) {
            throw IllegalArgumentException("Name cannot be empty")
        }
        if (request.phone.isBlank()) {
            throw IllegalArgumentException("Phone number cannot be empty")
        }
        if (request.email.isBlank()) {
            throw IllegalArgumentException("Email cannot be empty")
        }
        if (request.password.isBlank()) {
            throw IllegalArgumentException("Password cannot be empty")
        }

        val passwordHash = hashString(request.password)

        val birthdayDate = dateParse(request.birthday)

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
            val newPasswordHash = hashString(request.newPassword)
            userRepository.updatePassword(user.id, newPasswordHash)
        }
    }

    suspend fun updateUserInfo(
        email: String,
        request: UpdateUserInfoRequest,
    ) {
        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("User email doesn't exist")
        val birthday = dateParse(request.birthDate)
            ?: throw IllegalArgumentException("Incorrect Birthday date format")
        if ((request.gender != "Male") || (request.gender != "Female"))
            throw IllegalArgumentException("Incorrect Gender format")

        userRepository.updateUser(
            id = user.id,
            name = request.name!!,
            surname = request.surname!!,
            phone = request.phone!!,
            email = request.email!!,
            birthday = birthday,
            gender = request.gender,
        )
    }
}