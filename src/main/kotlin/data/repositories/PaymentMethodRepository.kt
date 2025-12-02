package setixx.software.data.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import setixx.software.data.tables.PaymentMethods
import setixx.software.models.PaymentMethod
import setixx.software.utils.dbQuery
import java.time.Instant

class PaymentMethodRepository {

    suspend fun createPaymentMethod(
        userId: Long,
        cardNumberLast4: String,
        cardHolderName: String,
        expiryMonth: Short,
        expiryYear: Short,
        isDefault: Boolean
    ): PaymentMethod = dbQuery {
        val now = Instant.now()

        val insertStatement = PaymentMethods.insert {
            it[PaymentMethods.userId] = userId
            it[PaymentMethods.cardNumberLast4] = cardNumberLast4
            it[PaymentMethods.cardHolderName] = cardHolderName
            it[PaymentMethods.expiryMonth] = expiryMonth
            it[PaymentMethods.expiryYear] = expiryYear
            it[PaymentMethods.isDefault] = isDefault
            it[PaymentMethods.createdAt] = now
        }

        PaymentMethod(
            id = insertStatement[PaymentMethods.id],
            userId = userId,
            cardNumberLast4 = cardNumberLast4,
            cardHolderName = cardHolderName,
            expiryMonth = expiryMonth,
            expiryYear = expiryYear,
            isDefault = isDefault,
            createdAt = now
        )
    }

    suspend fun findPaymentMethodsByUserId(userId: Long): List<PaymentMethod> = dbQuery {
        PaymentMethods.selectAll()
            .where { PaymentMethods.userId eq userId }
            .orderBy(PaymentMethods.isDefault to SortOrder.DESC)
            .map { rowToPaymentMethod(it) }
    }

    suspend fun findPaymentMethodById(id: Long, userId: Long): PaymentMethod? = dbQuery {
        PaymentMethods.selectAll()
            .where { (PaymentMethods.id eq id) and (PaymentMethods.userId eq userId) }
            .map { rowToPaymentMethod(it) }
            .singleOrNull()
    }

    suspend fun setDefaultPaymentMethod(
        id: Long,
        userId: Long
    ): Int = dbQuery {
        PaymentMethods.update({ (PaymentMethods.id eq id) and (PaymentMethods.userId eq userId) }) {
            it[PaymentMethods.isDefault] = true
        }
    }

    suspend fun deletePaymentMethod(id: Long, userId: Long): Int = dbQuery {
        PaymentMethods.deleteWhere { (PaymentMethods.id eq id) and (PaymentMethods.userId eq userId) }
    }

    private fun rowToPaymentMethod(row: ResultRow): PaymentMethod {
        return PaymentMethod(
            id = row[PaymentMethods.id],
            userId = row[PaymentMethods.userId],
            cardNumberLast4 = row[PaymentMethods.cardNumberLast4],
            cardHolderName = row[PaymentMethods.cardHolderName],
            expiryMonth = row[PaymentMethods.expiryMonth],
            expiryYear = row[PaymentMethods.expiryYear],
            isDefault = row[PaymentMethods.isDefault],
            createdAt = row[PaymentMethods.createdAt]
        )
    }
}