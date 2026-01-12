package setixx.software.data.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import setixx.software.data.tables.Products
import setixx.software.data.tables.Wishlist
import setixx.software.models.WishlistItem
import setixx.software.utils.dbQuery
import java.time.Instant
import java.util.UUID

class WishlistRepository {

    suspend fun addToWishlist(
        userId: Long,
        productId: Long
    ): WishlistItem = dbQuery {
        val now = Instant.now()

        val existing = Wishlist.selectAll()
            .where { (Wishlist.userId eq userId) and (Wishlist.productId eq productId) }
            .map { rowToWishlistItem(it) }
            .singleOrNull()

        if (existing != null) {
            return@dbQuery existing
        }

        val insertStatement = Wishlist.insert {
            it[Wishlist.userId] = userId
            it[Wishlist.productId] = productId
            it[Wishlist.addedAt] = now
        }

        WishlistItem(
            id = insertStatement[Wishlist.id],
            userId = userId,
            productId = productId,
            addedAt = now
        )
    }

    suspend fun findWishlistByUserId(userId: Long): List<WishlistItem> = dbQuery {
        Wishlist.selectAll()
            .where { Wishlist.userId eq userId }
            .orderBy(Wishlist.addedAt to SortOrder.DESC)
            .map { rowToWishlistItem(it) }
    }

    suspend fun findWishlistItemById(id: Long, userId: Long): WishlistItem? = dbQuery {
        Wishlist.selectAll()
            .where { (Wishlist.id eq id) and (Wishlist.userId eq userId) }
            .map { rowToWishlistItem(it) }
            .singleOrNull()
    }

    suspend fun removeFromWishlist(id: Long, userId: Long): Int = dbQuery {
        Wishlist.deleteWhere {
            (Wishlist.id eq id) and (Wishlist.userId eq userId)
        }
    }

    suspend fun removeFromWishlistByProductPublicId(userId: Long, productPublicId: UUID): Int = dbQuery {
        val productId = Products.selectAll()
            .where { Products.publicId eq productPublicId }
            .map { it[Products.id] }
            .singleOrNull() ?: return@dbQuery 0

        Wishlist.deleteWhere {
            (Wishlist.userId eq userId) and (Wishlist.productId eq productId)
        }
    }

    suspend fun clearWishlist(userId: Long): Int = dbQuery {
        Wishlist.deleteWhere { Wishlist.userId eq userId }
    }

    private fun rowToWishlistItem(row: ResultRow): WishlistItem {
        return WishlistItem(
            id = row[Wishlist.id],
            userId = row[Wishlist.userId],
            productId = row[Wishlist.productId],
            addedAt = row[Wishlist.addedAt]
        )
    }
}