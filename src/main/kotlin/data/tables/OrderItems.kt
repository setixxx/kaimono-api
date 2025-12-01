package setixx.software.data.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object OrderItems : Table("order_items") {
    val id = long("id").autoIncrement()
    val orderId = long("order_id").references(Orders.id, onDelete = ReferenceOption.CASCADE)
    val productId = long("product_id").references(Products.id, onDelete = ReferenceOption.RESTRICT)
    val productSizeId = long("product_size_id").references(ProductSizes.id, onDelete = ReferenceOption.RESTRICT)
    val quantity = integer("quantity")
    val priceAtPurchase = decimal("price_at_purchase", 10, 2)
    val subtotal = decimal("subtotal", 10, 2)

    override val primaryKey = PrimaryKey(id)
}