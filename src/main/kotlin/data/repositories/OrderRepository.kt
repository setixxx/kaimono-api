package setixx.software.data.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import setixx.software.data.tables.*
import setixx.software.models.Order
import setixx.software.models.OrderItem
import setixx.software.models.OrderStatus
import setixx.software.utils.dbQuery
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class OrderRepository {

    suspend fun createOrder(
        userId: Long,
        addressId: Long,
        statusId: Long,
        totalAmount: BigDecimal
    ): Order = dbQuery {
        val now = Instant.now()
        val publicId = UUID.randomUUID()

        val insertStatement = Orders.insert {
            it[Orders.publicId] = publicId
            it[Orders.userId] = userId
            it[Orders.addressId] = addressId
            it[Orders.statusId] = statusId
            it[Orders.totalAmount] = totalAmount
            it[Orders.createdAt] = now
            it[Orders.updatedAt] = now
        }

        Order(
            id = insertStatement[Orders.id],
            publicId = publicId,
            userId = userId,
            addressId = addressId,
            statusId = statusId,
            totalAmount = totalAmount,
            createdAt = now,
            updatedAt = now
        )
    }

    suspend fun createOrderItem(
        orderId: Long,
        productId: Long,
        productSizeId: Long,
        quantity: Int,
        priceAtPurchase: BigDecimal
    ): OrderItem = dbQuery {
        val subtotal = priceAtPurchase * BigDecimal(quantity)

        val insertStatement = OrderItems.insert {
            it[OrderItems.orderId] = orderId
            it[OrderItems.productId] = productId
            it[OrderItems.productSizeId] = productSizeId
            it[OrderItems.quantity] = quantity
            it[OrderItems.priceAtPurchase] = priceAtPurchase
            it[OrderItems.subtotal] = subtotal
        }

        OrderItem(
            id = insertStatement[OrderItems.id],
            orderId = orderId,
            productId = productId,
            productSizeId = productSizeId,
            quantity = quantity,
            priceAtPurchase = priceAtPurchase,
            subtotal = subtotal
        )
    }

    suspend fun findOrderByPublicId(publicId: UUID): Order? = dbQuery {
        Orders.selectAll()
            .where { Orders.publicId eq publicId }
            .map { rowToOrder(it) }
            .singleOrNull()
    }

    suspend fun findOrdersByUserId(userId: Long): List<Order> = dbQuery {
        Orders.selectAll()
            .where { Orders.userId eq userId }
            .orderBy(Orders.createdAt to SortOrder.DESC)
            .map { rowToOrder(it) }
    }

    suspend fun findOrderItemsByOrderId(orderId: Long): List<OrderItem> = dbQuery {
        OrderItems.selectAll()
            .where { OrderItems.orderId eq orderId }
            .map { rowToOrderItem(it) }
    }

    suspend fun findOrderStatusByCode(code: String): OrderStatus? = dbQuery {
        OrderStatuses.selectAll()
            .where { OrderStatuses.code eq code }
            .map { rowToOrderStatus(it) }
            .singleOrNull()
    }

    suspend fun findOrderStatusById(id: Long): OrderStatus? = dbQuery {
        OrderStatuses.selectAll()
            .where { OrderStatuses.id eq id }
            .map { rowToOrderStatus(it) }
            .singleOrNull()
    }

    suspend fun decreaseProductStock(productSizeId: Long, quantity: Int): Int = dbQuery {
        ProductSizes.update({ ProductSizes.id eq productSizeId }) {
            it[ProductSizes.stockQuantity] = ProductSizes.stockQuantity - quantity
        }
    }

    private fun rowToOrder(row: ResultRow): Order {
        return Order(
            id = row[Orders.id],
            publicId = row[Orders.publicId],
            userId = row[Orders.userId],
            addressId = row[Orders.addressId],
            statusId = row[Orders.statusId],
            totalAmount = row[Orders.totalAmount],
            createdAt = row[Orders.createdAt],
            updatedAt = row[Orders.updatedAt]
        )
    }

    private fun rowToOrderItem(row: ResultRow): OrderItem {
        return OrderItem(
            id = row[OrderItems.id],
            orderId = row[OrderItems.orderId],
            productId = row[OrderItems.productId],
            productSizeId = row[OrderItems.productSizeId],
            quantity = row[OrderItems.quantity],
            priceAtPurchase = row[OrderItems.priceAtPurchase],
            subtotal = row[OrderItems.subtotal]
        )
    }

    private fun rowToOrderStatus(row: ResultRow): OrderStatus {
        return OrderStatus(
            id = row[OrderStatuses.id],
            code = row[OrderStatuses.code],
            name = row[OrderStatuses.name],
            description = row[OrderStatuses.description]
        )
    }
}