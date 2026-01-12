package setixx.software.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddToCartRequest(
    @SerialName("product_public_id")
    val productPublicId: String,
    val size: String,
    val quantity: Int = 1
)

@Serializable
data class UpdateCartItemRequest(
    val size: String,

    val quantity: Int
)

@Serializable
data class CartResponse(
    val id: Long,
    val items: List<CartItemResponse>,

    @SerialName("total_price")
    val totalPrice: String
)

@Serializable
data class CartItemResponse(
    val id: Long,

    @SerialName("product_public_id")
    val productPublicId: String,

    @SerialName("product_name")
    val productName: String,

    @SerialName("product_image")
    val productImage: String?,

    val size: String,
    val quantity: Int,

    @SerialName("price_per_item")
    val pricePerItem: String,

    val subtotal: String
)