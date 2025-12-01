package setixx.software.data.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Categories : Table("categories") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 100).uniqueIndex()
    val description = text("description").nullable()
    val parentId = long("parent_id").references(id, onDelete = ReferenceOption.SET_NULL).nullable()
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")

    override val primaryKey = PrimaryKey(id)
}