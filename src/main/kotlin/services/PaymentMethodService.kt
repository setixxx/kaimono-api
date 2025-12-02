package setixx.software.services

import setixx.software.data.dto.CreatePaymentMethodRequest
import setixx.software.data.dto.PaymentMethodResponse
import setixx.software.data.repositories.PaymentMethodRepository
import setixx.software.data.repositories.UserRepository
import java.time.Year
import java.util.UUID

class PaymentMethodService(
    private val paymentMethodRepository: PaymentMethodRepository,
    private val userRepository: UserRepository
) {

    suspend fun createPaymentMethod(
        userPublicId: String,
        request: CreatePaymentMethodRequest
    ): PaymentMethodResponse {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        if (request.cardNumber.length < 13 || request.cardNumber.length > 19) {
            throw IllegalArgumentException("Invalid card number length")
        }
        if (!request.cardNumber.all { it.isDigit() }) {
            throw IllegalArgumentException("Card number must contain only digits")
        }

        if (request.cardHolderName.isBlank()) {
            throw IllegalArgumentException("Card holder name cannot be empty")
        }

        val currentYear = Year.now().value.toShort()
        if (request.expiryMonth !in 1..12) {
            throw IllegalArgumentException("Invalid expiry month")
        }
        if (request.expiryYear < currentYear) {
            throw IllegalArgumentException("Card has expired")
        }

        val last4 = request.cardNumber.takeLast(4)

        val paymentMethod = paymentMethodRepository.createPaymentMethod(
            userId = user.id,
            cardNumberLast4 = last4,
            cardHolderName = request.cardHolderName,
            expiryMonth = request.expiryMonth,
            expiryYear = request.expiryYear,
            isDefault = request.isDefault
        )

        return PaymentMethodResponse(
            id = paymentMethod.id,
            cardNumberLast4 = paymentMethod.cardNumberLast4,
            cardHolderName = paymentMethod.cardHolderName,
            expiryMonth = paymentMethod.expiryMonth,
            expiryYear = paymentMethod.expiryYear,
            isDefault = paymentMethod.isDefault
        )
    }

    suspend fun getPaymentMethods(userPublicId: String): List<PaymentMethodResponse> {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        val paymentMethods = paymentMethodRepository.findPaymentMethodsByUserId(user.id)

        return paymentMethods.map { method ->
            PaymentMethodResponse(
                id = method.id,
                cardNumberLast4 = method.cardNumberLast4,
                cardHolderName = method.cardHolderName,
                expiryMonth = method.expiryMonth,
                expiryYear = method.expiryYear,
                isDefault = method.isDefault
            )
        }
    }

    suspend fun setDefaultPaymentMethod(
        userPublicId: String,
        paymentMethodId: Long
    ): PaymentMethodResponse {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        val existingMethod = paymentMethodRepository.findPaymentMethodById(paymentMethodId, user.id)
            ?: throw IllegalArgumentException("Payment method not found")

        val updated = paymentMethodRepository.setDefaultPaymentMethod(paymentMethodId, user.id)

        if (updated == 0) {
            throw IllegalArgumentException("Failed to update payment method")
        }

        val updatedMethod = paymentMethodRepository.findPaymentMethodById(paymentMethodId, user.id)
            ?: throw IllegalArgumentException("Payment method not found after update")

        return PaymentMethodResponse(
            id = updatedMethod.id,
            cardNumberLast4 = updatedMethod.cardNumberLast4,
            cardHolderName = updatedMethod.cardHolderName,
            expiryMonth = updatedMethod.expiryMonth,
            expiryYear = updatedMethod.expiryYear,
            isDefault = updatedMethod.isDefault
        )
    }

    suspend fun deletePaymentMethod(
        userPublicId: String,
        paymentMethodId: Long
    ) {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        val deleted = paymentMethodRepository.deletePaymentMethod(paymentMethodId, user.id)

        if (deleted == 0) {
            throw IllegalArgumentException("Payment method not found or already deleted")
        }
    }
}