package setixx.software.data.repositories

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import setixx.software.data.tables.Users
import setixx.software.models.User
import setixx.software.utils.dbQuery
import java.time.LocalDate
import java.util.UUID

class UserRepository {

    suspend fun registerUser(
        name: String,
        surname: String?,
        phone: String,
        email: String,
        birthday: LocalDate?,
        gender: String?,
        passwordHash: String
    ): User = dbQuery {
        val newPublicId = UUID.randomUUID()
        val finalGender = gender ?: "Male"

        val insertStatement = Users.insert {
            it[publicId] = newPublicId
            it[this.name] = name
            it[this.surname] = surname
            it[this.phone] = phone
            it[this.email] = email
            it[this.birthday] = birthday
            it[this.gender] = finalGender
            it[this.passwordHash] = passwordHash
        }

        User(
            id = insertStatement[Users.id],
            publicId = newPublicId,
            name = name,
            surname = surname,
            phone = phone,
            email = email,
            birthday = birthday,
            gender = finalGender,
            passwordHash = passwordHash
        )
    }

    suspend fun findByEmail(email: String): User? = dbQuery {
        Users.selectAll()
            .where { Users.email eq email }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    suspend fun updatePassword(
        email: String,
        passwordHash: String,
    ) = dbQuery {
        Users.update({ Users.email eq email }) {
            it[Users.passwordHash] = passwordHash
        }
    }

    private fun rowToUser(row: ResultRow): User {
        return User(
            id = row[Users.id],
            publicId = row[Users.publicId],
            name = row[Users.name],
            surname = row[Users.surname],
            phone = row[Users.phone],
            email = row[Users.email],
            birthday = row[Users.birthday],
            gender = row[Users.gender],
            passwordHash = row[Users.passwordHash]
        )
    }
}