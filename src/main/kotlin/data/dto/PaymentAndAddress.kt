package setixx.software.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateAddressRequest(
    val city: String,
    val street: String,
    val house: String,
    val apartment: String? = null,

    @SerialName("zip_code")
    val zipCode: String,

    @SerialName("additional_info")
    val additionalInfo: String? = null,

    @SerialName("is_default")
    val isDefault: Boolean = false
)

@Serializable
data class AddressResponse(
    val id: Long,
    val city: String,
    val street: String,
    val house: String,
    val apartment: String?,

    @SerialName("zip_code")
    val zipCode: String,

    @SerialName("additional_info")
    val additionalInfo: String?,

    @SerialName("is_default")
    val isDefault: Boolean
)

@Serializable
data class CreatePaymentMethodRequest(
    @SerialName("card_number")
    val cardNumber: String,

    @SerialName("card_holder_name")
    val cardHolderName: String,

    @SerialName("expiry_month")
    val expiryMonth: Short,

    @SerialName("expiry_year")
    val expiryYear: Short,

    val cvv: String,

    @SerialName("is_default")
    val isDefault: Boolean = false
)

@Serializable
data class PaymentMethodResponse(
    val id: Long,

    @SerialName("payment_type")
    val paymentType: String,

    @SerialName("card_number_last4")
    val cardNumberLast4: String? = null,

    @SerialName("card_holder_name")
    val cardHolderName: String? = null,

    @SerialName("expiry_month")
    val expiryMonth: Short? = null,

    @SerialName("expiry_year")
    val expiryYear: Short? = null,

    val cvv: String? = null,

    @SerialName("is_default")
    val isDefault: Boolean
)