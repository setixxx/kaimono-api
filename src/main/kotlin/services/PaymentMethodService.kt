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

    companion object {
        const val CASH_PAYMENT_METHOD_ID = -1L
    }

    suspend fun createPaymentMethod(
        userPublicId: String,
        request: CreatePaymentMethodRequest
    ): PaymentMethodResponse {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        if (request.cardNumber.length !in 13..19) {
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

        if (request.cvv.length != 3) {
            throw IllegalArgumentException("CVV must be 3 digits")
        }
        if (!request.cvv.all { it.isDigit() }) {
            throw IllegalArgumentException("CVV must contain only digits")
        }

        val last4 = request.cardNumber.takeLast(4)

        val paymentMethod = paymentMethodRepository.createPaymentMethod(
            userId = user.id,
            cardNumberLast4 = last4,
            cardHolderName = request.cardHolderName,
            expiryMonth = request.expiryMonth,
            expiryYear = request.expiryYear,
            cvv = request.cvv,
            isDefault = request.isDefault
        )

        return PaymentMethodResponse(
            id = paymentMethod.id,
            paymentType = "card",
            cardNumberLast4 = paymentMethod.cardNumberLast4,
            cardHolderName = paymentMethod.cardHolderName,
            expiryMonth = paymentMethod.expiryMonth,
            expiryYear = paymentMethod.expiryYear,
            cvv = paymentMethod.cvv,
            isDefault = paymentMethod.isDefault
        )
    }

    suspend fun getPaymentMethods(userPublicId: String): List<PaymentMethodResponse> {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        val cardMethods = paymentMethodRepository.findPaymentMethodsByUserId(user.id)

        val cashMethod = PaymentMethodResponse(
            id = CASH_PAYMENT_METHOD_ID,
            paymentType = "cash",
            cardNumberLast4 = null,
            cardHolderName = null,
            expiryMonth = null,
            expiryYear = null,
            cvv = null,
            isDefault = cardMethods.isEmpty() || cardMethods.none { it.isDefault }
        )

        val cardMethodResponses = cardMethods.map { method ->
            PaymentMethodResponse(
                id = method.id,
                paymentType = "card",
                cardNumberLast4 = method.cardNumberLast4,
                cardHolderName = method.cardHolderName,
                expiryMonth = method.expiryMonth,
                expiryYear = method.expiryYear,
                cvv = method.cvv,
                isDefault = method.isDefault
            )
        }

        return listOf(cashMethod) + cardMethodResponses
    }

    suspend fun setDefaultPaymentMethod(
        userPublicId: String,
        paymentMethodId: Long
    ): PaymentMethodResponse {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        if (paymentMethodId == CASH_PAYMENT_METHOD_ID) {
            paymentMethodRepository.unsetAllDefaultPaymentMethods(user.id)

            return PaymentMethodResponse(
                id = CASH_PAYMENT_METHOD_ID,
                paymentType = "cash",
                cardNumberLast4 = null,
                cardHolderName = null,
                expiryMonth = null,
                expiryYear = null,
                cvv = null,
                isDefault = true
            )
        }

        val updated = paymentMethodRepository.setDefaultPaymentMethod(paymentMethodId, user.id)

        if (updated == 0) {
            throw IllegalArgumentException("Failed to update payment method")
        }

        val updatedMethod = paymentMethodRepository.findPaymentMethodById(paymentMethodId, user.id)
            ?: throw IllegalArgumentException("Payment method not found after update")

        return PaymentMethodResponse(
            id = updatedMethod.id,
            paymentType = "card",
            cardNumberLast4 = updatedMethod.cardNumberLast4,
            cardHolderName = updatedMethod.cardHolderName,
            expiryMonth = updatedMethod.expiryMonth,
            expiryYear = updatedMethod.expiryYear,
            cvv = updatedMethod.cvv,
            isDefault = updatedMethod.isDefault
        )
    }

    suspend fun deletePaymentMethod(
        userPublicId: String,
        paymentMethodId: Long
    ) {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        if (paymentMethodId == CASH_PAYMENT_METHOD_ID) {
            throw IllegalArgumentException("Cannot delete cash payment method")
        }

        val deleted = paymentMethodRepository.deletePaymentMethod(paymentMethodId, user.id)

        if (deleted == 0) {
            throw IllegalArgumentException("Payment method not found or already deleted")
        }
    }
}