package setixx.software.data.repositories

import org.jetbrains.exposed.sql.*
import setixx.software.data.tables.Categories
import setixx.software.models.Category
import setixx.software.utils.dbQuery

class CategoryRepository {

    suspend fun findAllCategories(): List<Category> = dbQuery {
        Categories.selectAll()
            .orderBy(Categories.name to SortOrder.ASC)
            .map { rowToCategory(it) }
    }

    suspend fun findCategoryById(id: Long): Category? = dbQuery {
        Categories.selectAll()
            .where { Categories.id eq id }
            .map { rowToCategory(it) }
            .singleOrNull()
    }

    suspend fun findCategoriesByParentId(parentId: Long?): List<Category> = dbQuery {
        if (parentId == null) {
            Categories.selectAll()
                .where { Categories.parentId.isNull() }
                .orderBy(Categories.name to SortOrder.ASC)
                .map { rowToCategory(it) }
        } else {
            Categories.selectAll()
                .where { Categories.parentId eq parentId }
                .orderBy(Categories.name to SortOrder.ASC)
                .map { rowToCategory(it) }
        }
    }

    private fun rowToCategory(row: ResultRow): Category {
        return Category(
            id = row[Categories.id],
            name = row[Categories.name],
            description = row[Categories.description],
            parentId = row[Categories.parentId],
            createdAt = row[Categories.createdAt],
            updatedAt = row[Categories.updatedAt]
        )
    }
}