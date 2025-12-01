package setixx.software.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    @SerialName("address_id")
    val addressId: Long
)

@Serializable
data class OrderResponse(
    @SerialName("public_id")
    val publicId: String,

    val status: String,

    @SerialName("total_amount")
    val totalAmount: String,

    @SerialName("created_at")
    val createdAt: String,

    val items: List<OrderItemResponse>,

    @SerialName("delivery_info")
    val deliveryInfo: DeliveryResponse?
)

@Serializable
data class OrderItemResponse(
    @SerialName("product_name")
    val productName: String,

    val size: String,
    val quantity: Int,

    @SerialName("price_at_purchase")
    val priceAtPurchase: String,

    val subtotal: String
)

@Serializable
data class DeliveryResponse(
    @SerialName("tracking_number")
    val trackingNumber: String?,

    val status: String,

    @SerialName("estimated_date")
    val estimatedDate: String?,

    val address: AddressResponse
)

@Serializable
data class OrderStatusResponse(
    val code: String,
    val name: String,
    val description: String?
)