package setixx.software.data.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Reviews : Table("reviews") {
    val id = long("id").autoIncrement()
    val publicId = uuid("public_id").uniqueIndex()
    val userId = long("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val productId = long("product_id").references(Products.id, onDelete = ReferenceOption.CASCADE)
    val orderId = long("order_id").references(Orders.id, onDelete = ReferenceOption.SET_NULL).nullable()
    val rating = short("rating")
    val comment = text("comment").nullable()
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")

    override val primaryKey = PrimaryKey(id)
    init {
        uniqueIndex(userId, productId, orderId)
    }
}