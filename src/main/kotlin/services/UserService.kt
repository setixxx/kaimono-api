package setixx.software.services

import setixx.software.data.dto.LoginUserRequest
import setixx.software.data.dto.RegisterUserRequest
import setixx.software.data.dto.UpdatePasswordRequest
import setixx.software.data.dto.UpdateUserInfoRequest
import setixx.software.data.dto.UserInfoResponse
import setixx.software.data.repositories.UserRepository
import setixx.software.data.tables.Users.phone
import setixx.software.models.User
import setixx.software.utils.AuthenticationException
import setixx.software.utils.UserAlreadyExistsException
import setixx.software.utils.ValidationException
import setixx.software.utils.dateParse
import setixx.software.utils.hashString
import java.util.UUID

class UserService(
    private val userRepository: UserRepository
) {

    suspend fun register(request: RegisterUserRequest): User {
        if (userRepository.findByEmail(request.email) != null) {
            throw UserAlreadyExistsException("User with email ${request.email} already exists")
        }

        if (userRepository.findByPhone(request.phone) != null) {
            throw UserAlreadyExistsException("User with phone ${request.phone} already exists")
        }

        if (request.password.length < 6) {
            throw ValidationException("Password is too short")
        }
        if (request.phone.isBlank()) {
            throw ValidationException("Phone number cannot be empty")
        }
        if (request.email.isBlank()) {
            throw ValidationException("Email cannot be empty")
        }
        if (request.password.isBlank()) {
            throw ValidationException("Password cannot be empty")
        }

        val passwordHash = hashString(request.password)

        val birthdayDate = dateParse(request.birthday)

        return userRepository.registerUser(
            name = request.email.substringBefore("@"),
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
            ?: throw AuthenticationException("Invalid email or password")

        val passwordHash = hashString(request.password)

        if (user.passwordHash == passwordHash) {
            return user
        } else
            throw AuthenticationException("Invalid email or password")
    }

    suspend fun updatePassword(
        publicId: String,
        request: UpdatePasswordRequest,
    ) {
        val user = userRepository.findByPublicId(UUID.fromString(publicId))
            ?: throw IllegalArgumentException("User doesn't exist")
        val oldPasswordHash = hashString(request.oldPassword)
        if (user.passwordHash != oldPasswordHash) throw IllegalArgumentException("User password doesn't match")
        if (request.newPassword.isBlank()) throw IllegalArgumentException("New password cannot be empty")
        val newPasswordHash = hashString(request.newPassword)
        userRepository.updatePassword(user.id, newPasswordHash)
    }

    suspend fun updateUserInfo(
        publicId: String,
        request: UpdateUserInfoRequest,
    ) {
        val user = userRepository.findByPublicId(UUID.fromString(publicId))
            ?: throw IllegalArgumentException("User doesn't exist")

        val newName = request.name.takeIf { !it.isNullOrBlank() }
        val newSurname = request.surname.takeIf { !it.isNullOrBlank() }
        val newPhone = request.phone.takeIf { !it.isNullOrBlank() }?.also { phone ->
            if (phone != user.phone) {
                userRepository.findByPhone(phone)?.let {
                    throw IllegalArgumentException("User with phone $phone already exists")
                }
            }
        }
        val newEmail = request.email.takeIf { !it.isNullOrBlank() }?.also { email ->
            if (email != user.email) {
                userRepository.findByEmail(email)?.let {
                    throw IllegalArgumentException("User with email $email already exists")
                }
            }
        }
        val newGender = request.gender.takeIf { !it.isNullOrBlank() }

        val birthday = dateParse(request.birthDate)
        if (newGender != null && newGender != "Male" && newGender != "Female") {
            throw IllegalArgumentException("Incorrect Gender format")
        }

        userRepository.updateUser(
            id = user.id,
            name = newName ?: user.name,
            surname = newSurname ?: user.surname,
            phone = newPhone ?: user.phone,
            email = newEmail ?: user.email,
            birthday = birthday ?: user.birthday,
            gender = newGender ?: user.gender,
        )
    }

    suspend fun getUserInfo(
        publicId: String
    ): UserInfoResponse {
        val user = userRepository.findByPublicId(UUID.fromString(publicId))
            ?: throw IllegalArgumentException("User doesn't exist")
        return UserInfoResponse(
            id = publicId,
            name = user.name,
            surname = user.surname,
            phone = user.phone,
            email = user.email,
            birthDate = user.birthday?.toString(),
            gender = user.gender,
        )
    }
}