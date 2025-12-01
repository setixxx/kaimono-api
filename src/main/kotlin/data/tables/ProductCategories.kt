package setixx.software.data.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ProductCategories : Table("product_categories") {
    val id = long("id").autoIncrement()
    val productId = long("product_id").references(Products.id, onDelete = ReferenceOption.CASCADE)
    val categoryId = long("category_id").references(Categories.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(id)
    init {
        uniqueIndex(productId, categoryId)
    }
}