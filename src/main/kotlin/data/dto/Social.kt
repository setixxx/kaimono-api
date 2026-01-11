package setixx.software.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateReviewRequest(
    @SerialName("product_public_id")
    val productPublicId: String,

    @SerialName("order_public_id")
    val orderPublicId: String?,

    val rating: Short,
    val comment: String? = null
)

@Serializable
data class ReviewResponse(
    val id: Long,

    @SerialName("user_name")
    val userName: String,

    val rating: Short,
    val comment: String?,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("product_public_id")
    val productPublicId: String? = null,

    @SerialName("product_name")
    val productName: String? = null,

    @SerialName("product_image")
    val productImage: String? = null
)

@Serializable
data class UpdateReviewRequest(
    val rating: Short,
    val comment: String? = null
)

@Serializable
data class AddToWishlistRequest(
    @SerialName("product_public_id")
    val productPublicId: String
)

@Serializable
data class WishlistResponse(
    val items: List<WishlistItemResponse>
)

@Serializable
data class WishlistItemResponse(
    val id: Long,

    @SerialName("product_public_id")
    val productPublicId: String,

    @SerialName("product_name")
    val productName: String,

    @SerialName("product_description")
    val productDescription: String,

    @SerialName("product_image")
    val productImage: String?,

    @SerialName("base_price")
    val basePrice: String,

    @SerialName("is_available")
    val isAvailable: Boolean,

    @SerialName("available_sizes")
    val availableSizes: List<ProductSizeInfo>,

    @SerialName("added_at")
    val addedAt: String
)

@Serializable
data class ProductSizeInfo(
    val id: Long,
    val size: String,

    @SerialName("stock_quantity")
    val stockQuantity: Int,

    @SerialName("price_modifier")
    val priceModifier: String,

    @SerialName("final_price")
    val finalPrice: String
)

@Serializable
data class NotificationResponse(
    val id: Long,
    val type: String,
    val title: String,
    val message: String,

    @SerialName("is_read")
    val isRead: Boolean,

    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class MarkNotificationReadRequest(
    @SerialName("notification_ids")
    val notificationIds: List<Long>
)