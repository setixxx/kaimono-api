package setixx.software.services

import setixx.software.data.dto.AddToCartRequest
import setixx.software.data.dto.CartItemResponse
import setixx.software.data.dto.CartResponse
import setixx.software.data.dto.UpdateCartItemRequest
import setixx.software.data.repositories.CartRepository
import setixx.software.data.repositories.ProductRepository
import setixx.software.data.repositories.UserRepository
import java.math.BigDecimal
import java.util.UUID

class CartService(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) {

    suspend fun addToCart(
        userPublicId: String,
        request: AddToCartRequest
    ): CartResponse {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        val product = productRepository.findProductByPublicId(UUID.fromString(request.productPublicId))
            ?: throw IllegalArgumentException("Product not found")

        if (!product.isAvailable) {
            throw IllegalArgumentException("Product is not available")
        }

        val productSize = productRepository.findProductSizeById(request.sizeId)
            ?: throw IllegalArgumentException("Product size not found")

        if (productSize.productId != product.id) {
            throw IllegalArgumentException("Product size does not belong to this product")
        }

        if (productSize.stockQuantity < request.quantity) {
            throw IllegalArgumentException("Not enough stock available")
        }

        if (request.quantity <= 0) {
            throw IllegalArgumentException("Quantity must be greater than 0")
        }

        val cart = cartRepository.findOrCreateCart(user.id)

        cartRepository.addItemToCart(
            cartId = cart.id,
            productId = product.id,
            productSizeId = request.sizeId,
            quantity = request.quantity
        )

        return getCart(userPublicId)
    }

    suspend fun getCart(userPublicId: String): CartResponse {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        val cart = cartRepository.findOrCreateCart(user.id)
        val cartItems = cartRepository.getCartItems(cart.id)

        var totalPrice = BigDecimal.ZERO
        val items = cartItems.map { cartItem ->
            val product = productRepository.findProductById(cartItem.productId)
                ?: throw IllegalArgumentException("Product not found")

            val productSize = productRepository.findProductSizeById(cartItem.productSizeId)
                ?: throw IllegalArgumentException("Product size not found")

            val imageUrl = productRepository.getPrimaryImageUrl(product.id)

            val pricePerItem = product.basePrice + productSize.priceModifier
            val subtotal = pricePerItem * BigDecimal(cartItem.quantity)

            totalPrice += subtotal

            CartItemResponse(
                id = cartItem.id,
                productId = product.id,
                productName = product.name,
                productImage = imageUrl,
                size = productSize.size,
                quantity = cartItem.quantity,
                pricePerItem = pricePerItem.toString(),
                subtotal = subtotal.toString()
            )
        }

        return CartResponse(
            id = cart.id,
            items = items,
            totalPrice = totalPrice.toString()
        )
    }

    suspend fun updateCartItem(
        userPublicId: String,
        cartItemId: Long,
        request: UpdateCartItemRequest
    ): CartResponse {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        val cart = cartRepository.findOrCreateCart(user.id)

        val cartItem = cartRepository.findCartItemById(cartItemId, cart.id)
            ?: throw IllegalArgumentException("Cart item not found")

        if (request.quantity <= 0) {
            throw IllegalArgumentException("Quantity must be greater than 0")
        }

        val productSize = productRepository.findProductSizeById(cartItem.productSizeId)
            ?: throw IllegalArgumentException("Product size not found")

        if (productSize.stockQuantity < request.quantity) {
            throw IllegalArgumentException("Not enough stock available")
        }

        val updated = cartRepository.updateCartItemQuantity(
            cartItemId = cartItemId,
            cartId = cart.id,
            quantity = request.quantity
        )

        if (updated == 0) {
            throw IllegalArgumentException("Failed to update cart item")
        }

        return getCart(userPublicId)
    }

    suspend fun removeCartItem(
        userPublicId: String,
        cartItemId: Long
    ): CartResponse {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        val cart = cartRepository.findOrCreateCart(user.id)

        val deleted = cartRepository.removeCartItem(cartItemId, cart.id)

        if (deleted == 0) {
            throw IllegalArgumentException("Cart item not found or already removed")
        }

        return getCart(userPublicId)
    }

    suspend fun clearCart(userPublicId: String): CartResponse {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        val cart = cartRepository.findOrCreateCart(user.id)

        cartRepository.clearCart(cart.id)

        return getCart(userPublicId)
    }
}