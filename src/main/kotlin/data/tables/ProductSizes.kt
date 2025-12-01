package setixx.software.data.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object ProductSizes : Table("product_sizes") {
    val id = long("id").autoIncrement()
    val productId = long("product_id").references(Products.id, onDelete = ReferenceOption.CASCADE)
    val size = varchar("size", 20)
    val stockQuantity = integer("stock_quantity").default(0)
    val priceModifier = decimal("price_modifier", 10, 2).default(java.math.BigDecimal.ZERO)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")

    override val primaryKey = PrimaryKey(id)
    init {
        uniqueIndex(productId, size)
    }
}