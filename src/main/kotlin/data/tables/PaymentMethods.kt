package setixx.software.data.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object PaymentMethods : Table("payment_methods") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val cardNumberLast4 = varchar("card_number_last4", 4)
    val cardHolderName = varchar("card_holder_name", 100)
    val expiryMonth = short("expiry_month")
    val expiryYear = short("expiry_year")
    val cvv = varchar("cvv", 3)
    val isDefault = bool("is_default").default(false)
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}