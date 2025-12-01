package setixx.software.data.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Payments : Table("payments") {
    val id = long("id").autoIncrement()
    val orderId = long("order_id").references(Orders.id, onDelete = ReferenceOption.CASCADE)
    val paymentMethodId = long("payment_method_id").references(PaymentMethods.id, onDelete = ReferenceOption.SET_NULL).nullable()
    val amount = decimal("amount", 10, 2)
    val status = varchar("status", 50)
    val transactionId = varchar("transaction_id", 255).nullable()
    val paidAt = timestamp("paid_at").nullable()
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}