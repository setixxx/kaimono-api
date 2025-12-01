package setixx.software.data.tables

import org.jetbrains.exposed.sql.Table

object OrderStatuses : Table("order_statuses") {
    val id = long("id").autoIncrement()
    val code = varchar("code", 50).uniqueIndex()
    val name = varchar("name", 100)
    val description = text("description").nullable()

    override val primaryKey = PrimaryKey(id)
}