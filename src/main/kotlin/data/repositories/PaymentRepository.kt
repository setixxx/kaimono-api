package setixx.software.data.repositories

import org.jetbrains.exposed.sql.*
import setixx.software.data.tables.Payments
import setixx.software.models.Payment
import setixx.software.utils.dbQuery
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class PaymentRepository {

    suspend fun createPayment(
        orderId: Long,
        paymentMethodId: Long?,
        amount: BigDecimal,
        status: String,
        paidAt: Instant?
    ): Payment = dbQuery {
        val now = Instant.now()
        val transactionId = "TXN_${UUID.randomUUID()}"

        val insertStatement = Payments.insert {
            it[Payments.orderId] = orderId
            it[Payments.paymentMethodId] = paymentMethodId
            it[Payments.amount] = amount
            it[Payments.status] = status
            it[Payments.transactionId] = transactionId
            it[Payments.paidAt] = paidAt
            it[Payments.createdAt] = now
        }

        Payment(
            id = insertStatement[Payments.id],
            orderId = orderId,
            paymentMethodId = paymentMethodId,
            amount = amount,
            status = status,
            transactionId = transactionId,
            paidAt = paidAt,
            createdAt = now
        )
    }

    suspend fun findPaymentByOrderId(orderId: Long): Payment? = dbQuery {
        Payments.selectAll()
            .where { Payments.orderId eq orderId }
            .map { rowToPayment(it) }
            .singleOrNull()
    }

    suspend fun updatePaymentStatus(
        orderId: Long,
        status: String,
        paidAt: Instant?
    ): Int = dbQuery {
        Payments.update({ Payments.orderId eq orderId }) {
            it[Payments.status] = status
            if (paidAt != null) {
                it[Payments.paidAt] = paidAt
            }
        }
    }

    private fun rowToPayment(row: ResultRow): Payment {
        return Payment(
            id = row[Payments.id],
            orderId = row[Payments.orderId],
            paymentMethodId = row[Payments.paymentMethodId],
            amount = row[Payments.amount],
            status = row[Payments.status],
            transactionId = row[Payments.transactionId],
            paidAt = row[Payments.paidAt],
            createdAt = row[Payments.createdAt]
        )
    }
}