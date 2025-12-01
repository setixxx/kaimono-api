package setixx.software.data.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object ProductImages : Table("product_images") {
    val id = long("id").autoIncrement()
    val productId = long("product_id").references(Products.id, onDelete = ReferenceOption.CASCADE)
    val imageUrl = text("image_url")
    val displayOrder = integer("display_order").default(0)
    val isPrimary = bool("is_primary").default(false)
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}