package setixx.software.data.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp

object Deliveries : Table("deliveries") {
    val id = long("id").autoIncrement()
    val orderId = long("order_id").references(Orders.id, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val addressId = long("address_id").references(Addresses.id, onDelete = ReferenceOption.RESTRICT)
    val trackingNumber = varchar("tracking_number", 100).nullable()
    val estimatedDeliveryDate = date("estimated_delivery_date").nullable()
    val actualDeliveryDate = date("actual_delivery_date").nullable()
    val status = varchar("status", 50)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")

    override val primaryKey = PrimaryKey(id)
}