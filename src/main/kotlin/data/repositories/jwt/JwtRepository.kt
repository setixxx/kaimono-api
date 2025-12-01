package setixx.software.data.repositories.jwt

import org.jetbrains.exposed.sql.insert
import setixx.software.data.tables.Tokens
import setixx.software.models.Token
import setixx.software.utils.dbQuery
import setixx.software.utils.hashString
import java.time.Instant
import java.time.LocalDateTime

class JwtRepository {
    suspend fun saveToken(
        userId: Long,
        token: String,
        expiresAt: Instant,
        createdAt: Instant,
        deviceInfo: String
    ) = dbQuery {
        val tokenHash = hashString(token)

        Tokens.insert {
            it[this.userId] = userId
            it[this.tokenHash] = tokenHash
            it[this.expiresAt] = expiresAt
            it[this.createdAt] = createdAt
            it[this.deviceInfo] = deviceInfo
        }
    }
}