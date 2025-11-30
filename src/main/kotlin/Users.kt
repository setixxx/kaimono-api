package setixx.software

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object Users : Table("users") {
    val id = long("id").autoIncrement()
    val publicId = uuid("public_id").uniqueIndex()
    val name = varchar("name", 120)
    val surname = varchar("surname", 120).nullable()
    val phone = varchar("phone", 12)
    val email = varchar("email", 255).uniqueIndex()
    val birthday = date("birthday").nullable()
    val gender = varchar("gender", 20).default("Male")
    val passwordHash = text("password_hash")

    override val primaryKey = PrimaryKey(id)
}