package setixx.software.data.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import setixx.software.data.tables.Wishlist
import setixx.software.models.WishlistItem
import setixx.software.utils.dbQuery
import java.time.Instant

class WishlistRepository {

    suspend fun addToWishlist(
        userId: Long,
        productId: Long,
        productSizeId: Long?
    ): WishlistItem = dbQuery {
        val now = Instant.now()

        val existing = Wishlist.selectAll()
            .where { (Wishlist.userId eq userId) and (Wishlist.productId eq productId) }
            .map { rowToWishlistItem(it) }
            .singleOrNull()

        if (existing != null) {
            Wishlist.update({
                (Wishlist.userId eq userId) and (Wishlist.productId eq productId)
            }) {
                it[Wishlist.productSizeId] = productSizeId
            }

            return@dbQuery existing.copy(productSizeId = productSizeId)
        }

        val insertStatement = Wishlist.insert {
            it[Wishlist.userId] = userId
            it[Wishlist.productId] = productId
            it[Wishlist.productSizeId] = productSizeId
            it[Wishlist.addedAt] = now
        }

        WishlistItem(
            id = insertStatement[Wishlist.id],
            userId = userId,
            productId = productId,
            productSizeId = productSizeId,
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

    suspend fun updateWishlistItemSize(
        id: Long,
        userId: Long,
        productSizeId: Long?
    ): Int = dbQuery {
        Wishlist.update({
            (Wishlist.id eq id) and (Wishlist.userId eq userId)
        }) {
            it[Wishlist.productSizeId] = productSizeId
        }
    }

    suspend fun removeFromWishlist(id: Long, userId: Long): Int = dbQuery {
        Wishlist.deleteWhere {
            (Wishlist.id eq id) and (Wishlist.userId eq userId)
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
            productSizeId = row[Wishlist.productSizeId],
            addedAt = row[Wishlist.addedAt]
        )
    }
}