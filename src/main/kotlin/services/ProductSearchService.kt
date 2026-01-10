package setixx.software.services

import setixx.software.data.dto.*
import setixx.software.data.repositories.ProductRepository
import setixx.software.models.Product
import java.util.UUID

class ProductSearchService(
    private val productRepository: ProductRepository
) {

    suspend fun searchProducts(
        query: String?,
        categoryIds: List<Long>?,
        minPrice: Double?,
        maxPrice: Double?,
        inStock: Boolean?,
        sortBy: String?,
        sortOrder: String?,
        page: Int,
        pageSize: Int
    ): ProductListResponse {

        val validPage = if (page < 1) 1 else page
        val validPageSize = when {
            pageSize < 1 -> 10
            pageSize > 100 -> 100
            else -> pageSize
        }

        val products = productRepository.searchProducts(
            query = query,
            categoryIds = categoryIds,
            minPrice = minPrice,
            maxPrice = maxPrice,
            inStock = inStock,
            sortBy = sortBy,
            sortOrder = sortOrder,
            offset = (validPage - 1) * validPageSize,
            limit = validPageSize
        )

        val totalCount = productRepository.countProducts(
            query = query,
            categoryIds = categoryIds,
            minPrice = minPrice,
            maxPrice = maxPrice,
            inStock = inStock
        )

        val productResponses = products.map { product ->
            mapProductToResponse(product)
        }

        return ProductListResponse(
            products = productResponses,
            totalCount = totalCount,
            page = validPage,
            pageSize = validPageSize
        )
    }

    suspend fun getProductByPublicId(publicId: String): ProductResponse {
        val product = productRepository.findProductByPublicId(UUID.fromString(publicId))
            ?: throw IllegalArgumentException("Product not found")

        return mapProductToResponse(product)
    }

    private suspend fun mapProductToResponse(product: Product): ProductResponse {
        val sizes = productRepository.findProductSizesByProductId(product.id)
        val images = productRepository.findProductImagesByProductId(product.id)
        val categories = productRepository.findCategoriesByProductId(product.id)
        val averageRating = productRepository.getProductAverageRating(product.id)
        val reviewCount = productRepository.getProductReviewCount(product.id)

        return ProductResponse(
            id = product.id,
            publicId = product.publicId.toString(),
            name = product.name,
            description = product.description,
            basePrice = product.basePrice.toString(),
            isAvailable = product.isAvailable,
            categories = categories.map { category ->
                CategoryResponse(
                    id = category.id,
                    name = category.name,
                    description = category.description,
                    parentId = category.parentId
                )
            },
            sizes = sizes.map { size ->
                ProductSizeResponse(
                    id = size.id,
                    size = size.size,
                    stockQuantity = size.stockQuantity,
                    priceModifier = size.priceModifier.toString()
                )
            },
            images = images.map { image ->
                ProductImageResponse(
                    id = image.id,
                    imageUrl = image.imageUrl,
                    isPrimary = image.isPrimary ?: false,
                    displayOrder = image.displayOrder ?: 0
                )
            },
            averageRating = averageRating?.toDouble(),
            reviewCount = reviewCount
        )
    }
}