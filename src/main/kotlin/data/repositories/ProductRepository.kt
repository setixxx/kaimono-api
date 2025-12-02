package setixx.software.data.repositories

import org.jetbrains.exposed.sql.*
import setixx.software.data.tables.ProductImages
import setixx.software.data.tables.ProductSizes
import setixx.software.data.tables.Products
import setixx.software.models.Product
import setixx.software.models.ProductImage
import setixx.software.models.ProductSize
import setixx.software.utils.dbQuery
import java.util.UUID

class ProductRepository {

    suspend fun findProductByPublicId(publicId: UUID): Product? = dbQuery {
        Products.selectAll()
            .where { Products.publicId eq publicId }
            .map { rowToProduct(it) }
            .singleOrNull()
    }

    suspend fun findProductById(id: Long): Product? = dbQuery {
        Products.selectAll()
            .where { Products.id eq id }
            .map { rowToProduct(it) }
            .singleOrNull()
    }

    suspend fun findProductSizeById(sizeId: Long): ProductSize? = dbQuery {
        ProductSizes.selectAll()
            .where { ProductSizes.id eq sizeId }
            .map { rowToProductSize(it) }
            .singleOrNull()
    }

    suspend fun findProductSizesByProductId(productId: Long): List<ProductSize> = dbQuery {
        ProductSizes.selectAll()
            .where { ProductSizes.productId eq productId }
            .map { rowToProductSize(it) }
    }

    suspend fun findProductImagesByProductId(productId: Long): List<ProductImage> = dbQuery {
        ProductImages.selectAll()
            .where { ProductImages.productId eq productId }
            .orderBy(ProductImages.displayOrder to SortOrder.ASC)
            .map { rowToProductImage(it) }
    }

    suspend fun getPrimaryImageUrl(productId: Long): String? = dbQuery {
        ProductImages.selectAll()
            .where { (ProductImages.productId eq productId) and (ProductImages.isPrimary eq true) }
            .map { it[ProductImages.imageUrl] }
            .singleOrNull()
            ?: ProductImages.selectAll()
                .where { ProductImages.productId eq productId }
                .orderBy(ProductImages.displayOrder to SortOrder.ASC)
                .limit(1)
                .map { it[ProductImages.imageUrl] }
                .singleOrNull()
    }

    private fun rowToProduct(row: ResultRow): Product {
        return Product(
            id = row[Products.id],
            publicId = row[Products.publicId],
            name = row[Products.name],
            description = row[Products.description],
            basePrice = row[Products.basePrice],
            isAvailable = row[Products.isAvailable],
            createdAt = row[Products.createdAt],
            updatedAt = row[Products.updatedAt]
        )
    }

    private fun rowToProductSize(row: ResultRow): ProductSize {
        return ProductSize(
            id = row[ProductSizes.id],
            productId = row[ProductSizes.productId],
            size = row[ProductSizes.size],
            stockQuantity = row[ProductSizes.stockQuantity],
            priceModifier = row[ProductSizes.priceModifier],
            createdAt = row[ProductSizes.createdAt],
            updatedAt = row[ProductSizes.updatedAt]
        )
    }

    private fun rowToProductImage(row: ResultRow): ProductImage {
        return ProductImage(
            id = row[ProductImages.id],
            productId = row[ProductImages.productId],
            imageUrl = row[ProductImages.imageUrl],
            displayOrder = row[ProductImages.displayOrder],
            isPrimary = row[ProductImages.isPrimary],
            createdAt = row[ProductImages.createdAt]
        )
    }
}