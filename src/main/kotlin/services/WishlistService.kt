package setixx.software.services

import setixx.software.data.dto.*
import setixx.software.data.repositories.ProductRepository
import setixx.software.data.repositories.UserRepository
import setixx.software.data.repositories.WishlistRepository
import java.util.UUID

class WishlistService(
    private val wishlistRepository: WishlistRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) {

    suspend fun addToWishlist(
        userPublicId: String,
        request: AddToWishlistRequest
    ): WishlistResponse {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        val product = productRepository.findProductByPublicId(UUID.fromString(request.productPublicId))
            ?: throw IllegalArgumentException("Product not found")

        wishlistRepository.addToWishlist(
            userId = user.id,
            productId = product.id,
            productSizeId = null
        )

        return getWishlist(userPublicId)
    }

    suspend fun getWishlist(userPublicId: String): WishlistResponse {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        val wishlistItems = wishlistRepository.findWishlistByUserId(user.id)

        val items = wishlistItems.map { wishlistItem ->
            val product = productRepository.findProductById(wishlistItem.productId)
                ?: throw IllegalArgumentException("Product not found")

            val imageUrl = productRepository.getPrimaryImageUrl(product.id)

            val availableSizes = productRepository.findProductSizesByProductId(product.id)

            val selectedSize = wishlistItem.productSizeId?.let { sizeId ->
                productRepository.findProductSizeById(sizeId)
            }

            val sizesInfo = availableSizes.map { size ->
                val finalPrice = product.basePrice + size.priceModifier
                ProductSizeInfo(
                    id = size.id,
                    size = size.size,
                    stockQuantity = size.stockQuantity,
                    priceModifier = size.priceModifier.toString(),
                    finalPrice = finalPrice.toString()
                )
            }

            WishlistItemResponse(
                id = wishlistItem.id,
                productPublicId = product.publicId.toString(),
                productName = product.name,
                productDescription = product.description,
                productImage = imageUrl,
                basePrice = product.basePrice.toString(),
                isAvailable = product.isAvailable,
                selectedSizeId = selectedSize?.id,
                selectedSize = selectedSize?.size,
                availableSizes = sizesInfo,
                addedAt = wishlistItem.addedAt.toString()
            )
        }

        return WishlistResponse(items = items)
    }

    suspend fun updateWishlistItemSize(
        userPublicId: String,
        wishlistItemId: Long,
        request: UpdateWishlistItemRequest
    ): WishlistResponse {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        val wishlistItem = wishlistRepository.findWishlistItemById(wishlistItemId, user.id)
            ?: throw IllegalArgumentException("Wishlist item not found")

        val productSize = productRepository.findProductSizeById(request.productSizeId)
            ?: throw IllegalArgumentException("Product size not found")

        if (productSize.productId != wishlistItem.productId) {
            throw IllegalArgumentException("Product size does not belong to this product")
        }

        val updated = wishlistRepository.updateWishlistItemSize(
            id = wishlistItemId,
            userId = user.id,
            productSizeId = request.productSizeId
        )

        if (updated == 0) {
            throw IllegalArgumentException("Failed to update wishlist item")
        }

        return getWishlist(userPublicId)
    }

    suspend fun removeFromWishlist(
        userPublicId: String,
        wishlistItemId: Long
    ): WishlistResponse {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        val deleted = wishlistRepository.removeFromWishlist(wishlistItemId, user.id)

        if (deleted == 0) {
            throw IllegalArgumentException("Wishlist item not found or already removed")
        }

        return getWishlist(userPublicId)
    }

    suspend fun clearWishlist(userPublicId: String): WishlistResponse {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        wishlistRepository.clearWishlist(user.id)

        return WishlistResponse(items = emptyList())
    }
}