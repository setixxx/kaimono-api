package setixx.software.data.repositories

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import setixx.software.data.tables.Tokens
import setixx.software.models.Token
import setixx.software.utils.dbQuery
import setixx.software.utils.hashString
import java.time.Instant

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

    suspend fun findByToken(token: String): Token? = dbQuery {
        val tokenHash = hashString(token)
        Tokens.selectAll()
            .where { Tokens.tokenHash eq tokenHash }
            .map { rowToToken(it) }
            .singleOrNull()
    }

    suspend fun findTokenByUser(
        userId: Long,
    ): Token? = dbQuery {
        Tokens.selectAll()
            .where { Tokens.userId eq userId }
            .map { rowToToken(it) }
            .singleOrNull()
    }

    suspend fun deleteToken(token: String): Int = dbQuery {
        val tokenHash = hashString(token)
        Tokens.deleteWhere { Tokens.tokenHash eq tokenHash }
    }

    suspend fun deleteTokenByUser(
        userId: Long,
    ) = dbQuery {
        Tokens.deleteWhere { Tokens.userId eq userId }
    }

    private fun rowToToken(row: ResultRow): Token {
        return Token(
            id = row[Tokens.id].toLong(),
            userId = row[Tokens.userId],
            tokenHash = row[Tokens.tokenHash],
            expiresAt = row[Tokens.expiresAt],
            createdAt = row[Tokens.createdAt],
            deviceInfo = row[Tokens.deviceInfo]
        )
    }
}