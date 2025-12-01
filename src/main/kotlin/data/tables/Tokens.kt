package setixx.software.data.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp

object Tokens : Table("refresh_tokens") {
    val id = integer("id").autoIncrement()
    val userId = long("user_id")
        .references(Users.id)
    val tokenHash = varchar("token_hash", 255)
    val expiresAt = timestamp("expires_at")
    val createdAt = timestamp("created_at")
    val deviceInfo = varchar("device_info", 255)

    override val primaryKey = PrimaryKey(id)

}