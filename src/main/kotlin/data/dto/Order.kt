package setixx.software.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    @SerialName("address_id")
    val addressId: Long,

    @SerialName("payment_method_id")
    val paymentMethodId: Long? = null,

    @SerialName("payment_type")
    val paymentType: String = "card"
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
    val deliveryInfo: DeliveryResponse?,

    @SerialName("payment_info")
    val paymentInfo: PaymentInfoResponse?
)

@Serializable
data class OrderItemResponse(
    @SerialName("product_public_id")
    val productPublicId: String,

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
data class PaymentInfoResponse(
    val id: Long,
    val amount: String,
    val status: String,

    @SerialName("payment_type")
    val paymentType: String,

    @SerialName("transaction_id")
    val transactionId: String?,

    @SerialName("paid_at")
    val paidAt: String?,

    @SerialName("payment_method")
    val paymentMethod: PaymentMethodInfoResponse?
)

@Serializable
data class PaymentMethodInfoResponse(
    @SerialName("card_number_last4")
    val cardNumberLast4: String,

    @SerialName("card_holder_name")
    val cardHolderName: String
)

@Serializable
data class OrderStatusResponse(
    val code: String,
    val name: String,
    val description: String?
)