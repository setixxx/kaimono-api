package setixx.software.data.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object CartItems : Table("cart_items") {
    val id = long("id").autoIncrement()
    val cartId = long("cart_id").references(Carts.id, onDelete = ReferenceOption.CASCADE)
    val productId = long("product_id").references(Products.id, onDelete = ReferenceOption.CASCADE)
    val productSizeId = long("product_size_id").references(ProductSizes.id, onDelete = ReferenceOption.CASCADE)
    val quantity = integer("quantity").default(1)
    val addedAt = timestamp("added_at")

    override val primaryKey = PrimaryKey(id)
    init {
        uniqueIndex(cartId, productId, productSizeId)
    }
}