package setixx.software.data.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Products : Table("products") {
    val id = long("id").autoIncrement()
    val publicId = uuid("public_id").uniqueIndex()
    val name = varchar("name", 255)
    val description = text("description")
    val basePrice = decimal("base_price", 10, 2)
    val isAvailable = bool("is_available").default(true)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")

    override val primaryKey = PrimaryKey(id)
}