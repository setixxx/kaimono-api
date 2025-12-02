package setixx.software.data.repositories

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import setixx.software.data.tables.Deliveries
import setixx.software.models.Delivery
import setixx.software.utils.dbQuery
import java.time.Instant
import java.time.LocalDate

class DeliveryRepository {

    suspend fun createDelivery(
        orderId: Long,
        addressId: Long,
        estimatedDeliveryDate: LocalDate?,
        status: String = "pending"
    ): Delivery = dbQuery {
        val now = Instant.now()

        val insertStatement = Deliveries.insert {
            it[Deliveries.orderId] = orderId
            it[Deliveries.addressId] = addressId
            it[Deliveries.estimatedDeliveryDate] = estimatedDeliveryDate
            it[Deliveries.status] = status
            it[Deliveries.createdAt] = now
            it[Deliveries.updatedAt] = now
        }

        Delivery(
            id = insertStatement[Deliveries.id],
            orderId = orderId,
            addressId = addressId,
            trackingNumber = null,
            estimatedDeliveryDate = estimatedDeliveryDate,
            actualDeliveryDate = null,
            status = status,
            createdAt = now,
            updatedAt = now
        )
    }

    suspend fun findDeliveryByOrderId(orderId: Long): Delivery? = dbQuery {
        Deliveries.selectAll()
            .where { Deliveries.orderId eq orderId }
            .map { rowToDelivery(it) }
            .singleOrNull()
    }

    private fun rowToDelivery(row: ResultRow): Delivery {
        return Delivery(
            id = row[Deliveries.id],
            orderId = row[Deliveries.orderId],
            addressId = row[Deliveries.addressId],
            trackingNumber = row[Deliveries.trackingNumber],
            estimatedDeliveryDate = row[Deliveries.estimatedDeliveryDate],
            actualDeliveryDate = row[Deliveries.actualDeliveryDate],
            status = row[Deliveries.status],
            createdAt = row[Deliveries.createdAt],
            updatedAt = row[Deliveries.updatedAt]
        )
    }
}