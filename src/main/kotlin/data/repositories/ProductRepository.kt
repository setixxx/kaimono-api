package setixx.software.data.repositories

import org.jetbrains.exposed.sql.*
import setixx.software.data.tables.Categories
import setixx.software.data.tables.ProductCategories
import setixx.software.data.tables.ProductImages
import setixx.software.data.tables.ProductSizes
import setixx.software.data.tables.Products
import setixx.software.data.tables.Reviews
import setixx.software.models.Category
import setixx.software.models.Product
import setixx.software.models.ProductImage
import setixx.software.models.ProductSize
import setixx.software.utils.dbQuery
import java.math.BigDecimal
import java.math.RoundingMode
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

    suspend fun getProductAverageRating(productId: Long): BigDecimal? = dbQuery {
        Reviews
            .select(Reviews.rating.avg())
            .where { Reviews.productId eq productId }
            .map { it[Reviews.rating.avg()] }
            .singleOrNull()
            ?.setScale(1, RoundingMode.HALF_UP)
    }

    suspend fun getProductReviewCount(productId: Long): Long = dbQuery {
        Reviews.selectAll()
            .where { Reviews.productId eq productId }
            .count()
    }

    suspend fun findProductSizeByProductIdAndSize(productId: Long, sizeName: String): ProductSize? = dbQuery {
        ProductSizes.selectAll()
            .where { (ProductSizes.productId eq productId) and (ProductSizes.size eq sizeName) }
            .map { rowToProductSize(it) }
            .singleOrNull()
    }

    suspend fun searchProducts(
        query: String?,
        categoryIds: List<Long>?,
        minPrice: Double?,
        maxPrice: Double?,
        inStock: Boolean?,
        sortBy: String?,
        sortOrder: String?,
        offset: Int,
        limit: Int
    ): List<Product> = dbQuery {

        var selectQuery = Products.selectAll()

        if (!query.isNullOrBlank()) {
            selectQuery = selectQuery.andWhere {
                (Products.name.lowerCase() like "%${query.lowercase()}%") or
                        (Products.description.lowerCase() like "%${query.lowercase()}%")
            }
        }

        if (!categoryIds.isNullOrEmpty()) {
            val productIdsInCategories = ProductCategories
                .select(ProductCategories.productId)
                .where { ProductCategories.categoryId inList categoryIds }
                .map { it[ProductCategories.productId] }

            if (productIdsInCategories.isNotEmpty()) {
                selectQuery = selectQuery.andWhere { Products.id inList productIdsInCategories }
            } else {
                return@dbQuery emptyList()
            }
        }

        if (minPrice != null) {
            selectQuery = selectQuery.andWhere {
                Products.basePrice greaterEq BigDecimal(minPrice)
            }
        }
        if (maxPrice != null) {
            selectQuery = selectQuery.andWhere {
                Products.basePrice lessEq BigDecimal(maxPrice)
            }
        }

        if (inStock == true) {
            val productIdsInStock = ProductSizes
                .select(ProductSizes.productId)
                .where { ProductSizes.stockQuantity greater 0 }
                .map { it[ProductSizes.productId] }
                .distinct()

            if (productIdsInStock.isNotEmpty()) {
                selectQuery = selectQuery.andWhere { Products.id inList productIdsInStock }
            } else {
                return@dbQuery emptyList()
            }
        }

        selectQuery = selectQuery.andWhere { Products.isAvailable eq true }

        selectQuery = when (sortBy?.lowercase()) {
            "price" -> {
                if (sortOrder?.lowercase() == "desc") {
                    selectQuery.orderBy(Products.basePrice to SortOrder.DESC)
                } else {
                    selectQuery.orderBy(Products.basePrice to SortOrder.ASC)
                }
            }
            "name" -> {
                if (sortOrder?.lowercase() == "desc") {
                    selectQuery.orderBy(Products.name to SortOrder.DESC)
                } else {
                    selectQuery.orderBy(Products.name to SortOrder.ASC)
                }
            }
            "created" -> {
                if (sortOrder?.lowercase() == "desc") {
                    selectQuery.orderBy(Products.createdAt to SortOrder.DESC)
                } else {
                    selectQuery.orderBy(Products.createdAt to SortOrder.ASC)
                }
            }
            else -> {
                selectQuery.orderBy(Products.createdAt to SortOrder.DESC)
            }
        }

        selectQuery
            .limit(limit).offset(offset.toLong())
            .map { rowToProduct(it) }
    }

    suspend fun countProducts(
        query: String?,
        categoryIds: List<Long>?,
        minPrice: Double?,
        maxPrice: Double?,
        inStock: Boolean?
    ): Long = dbQuery {

        var countQuery = Products.selectAll()

        if (!query.isNullOrBlank()) {
            countQuery = countQuery.andWhere {
                (Products.name.lowerCase() like "%${query.lowercase()}%") or
                        (Products.description.lowerCase() like "%${query.lowercase()}%")
            }
        }

        if (!categoryIds.isNullOrEmpty()) {
            val productIdsInCategories = ProductCategories
                .select(ProductCategories.productId)
                .where { ProductCategories.categoryId inList categoryIds }
                .map { it[ProductCategories.productId] }

            if (productIdsInCategories.isEmpty()) {
                return@dbQuery 0L
            }
            countQuery = countQuery.andWhere { Products.id inList productIdsInCategories }
        }

        if (minPrice != null) {
            countQuery = countQuery.andWhere {
                Products.basePrice greaterEq BigDecimal(minPrice)
            }
        }
        if (maxPrice != null) {
            countQuery = countQuery.andWhere {
                Products.basePrice lessEq BigDecimal(maxPrice)
            }
        }

        if (inStock == true) {
            val productIdsInStock = ProductSizes
                .select(ProductSizes.productId)
                .where { ProductSizes.stockQuantity greater 0 }
                .map { it[ProductSizes.productId] }
                .distinct()

            if (productIdsInStock.isEmpty()) {
                return@dbQuery 0L
            }
            countQuery = countQuery.andWhere { Products.id inList productIdsInStock }
        }

        countQuery = countQuery.andWhere { Products.isAvailable eq true }

        countQuery.count()
    }

    suspend fun findCategoriesByProductId(productId: Long): List<Category> = dbQuery {
        (Categories innerJoin ProductCategories)
            .selectAll()
            .where { ProductCategories.productId eq productId }
            .map { rowToCategory(it) }
    }

    private fun rowToCategory(row: ResultRow): Category {
        return Category(
            id = row[Categories.id],
            name = row[Categories.name],
            description = row[Categories.description],
            parentId = row[Categories.parentId],
            createdAt = row[Categories.createdAt],
            updatedAt = row[Categories.updatedAt]
        )
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