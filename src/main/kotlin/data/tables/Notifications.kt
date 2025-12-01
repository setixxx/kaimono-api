package setixx.software.data.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Notifications : Table("notifications") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val orderId = long("order_id").references(Orders.id, onDelete = ReferenceOption.CASCADE).nullable()
    val type = varchar("type", 50)
    val title = varchar("title", 255)
    val message = text("message")
    val isRead = bool("is_read").default(false)
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}