package setixx.software.data.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Wishlist : Table("wishlist") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val productId = long("product_id").references(Products.id, onDelete = ReferenceOption.CASCADE)
    val addedAt = timestamp("added_at")

    override val primaryKey = PrimaryKey(id)
    init {
        uniqueIndex(userId, productId)
    }
}