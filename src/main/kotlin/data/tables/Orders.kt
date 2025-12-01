package setixx.software.data.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Orders : Table("orders") {
    val id = long("id").autoIncrement()
    val publicId = uuid("public_id").uniqueIndex()
    val userId = long("user_id").references(Users.id, onDelete = ReferenceOption.RESTRICT)
    val addressId = long("address_id").references(Addresses.id, onDelete = ReferenceOption.RESTRICT)
    val statusId = long("status_id").references(OrderStatuses.id, onDelete = ReferenceOption.RESTRICT)
    val totalAmount = decimal("total_amount", 10, 2)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")

    override val primaryKey = PrimaryKey(id)
}