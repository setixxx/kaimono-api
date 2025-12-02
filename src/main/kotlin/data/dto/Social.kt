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
    val createdAt: String
)

@Serializable
data class UpdateReviewRequest(
    val rating: Short? = null,
    val comment: String? = null
)

@Serializable
data class AddToWishlistRequest(
    @SerialName("product_public_id")
    val productPublicId: String
)

@Serializable
data class WishlistItemResponse(
    val id: Long,
    val product: ProductResponse,

    @SerialName("added_at")
    val addedAt: String
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