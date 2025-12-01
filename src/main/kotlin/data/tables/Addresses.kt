package setixx.software.data.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Addresses : Table("addresses") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val city = varchar("city", 100)
    val street = varchar("street", 200)
    val house = varchar("house", 20)
    val apartment = varchar("apartment", 20).nullable()
    val zipCode = varchar("zip_code", 20)
    val additionalInfo = text("additional_info").nullable()
    val isDefault = bool("is_default").default(false)
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}