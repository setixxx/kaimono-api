package setixx.software.data.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import setixx.software.data.tables.*
import setixx.software.models.Cart
import setixx.software.models.CartItem
import setixx.software.utils.dbQuery
import java.time.Instant

class CartRepository {

    suspend fun findOrCreateCart(userId: Long): Cart = dbQuery {
        val existing = Carts.selectAll()
            .where { Carts.userId eq userId }
            .map { rowToCart(it) }
            .singleOrNull()

        if (existing != null) {
            return@dbQuery existing
        }

        val now = Instant.now()
        val insertStatement = Carts.insert {
            it[Carts.userId] = userId
            it[Carts.createdAt] = now
            it[Carts.updatedAt] = now
        }

        Cart(
            id = insertStatement[Carts.id],
            userId = userId,
            createdAt = now,
            updatedAt = now
        )
    }

    suspend fun addItemToCart(
        cartId: Long,
        productId: Long,
        productSizeId: Long,
        quantity: Int
    ): CartItem = dbQuery {
        val now = Instant.now()

        val existing = CartItems.selectAll()
            .where {
                (CartItems.cartId eq cartId) and
                        (CartItems.productId eq productId) and
                        (CartItems.productSizeId eq productSizeId)
            }
            .map { rowToCartItem(it) }
            .singleOrNull()

        if (existing != null) {
            val newQuantity = existing.quantity + quantity
            CartItems.update({
                (CartItems.id eq existing.id)
            }) {
                it[CartItems.quantity] = newQuantity
            }

            Carts.update({ Carts.id eq cartId }) {
                it[Carts.updatedAt] = now
            }

            return@dbQuery existing.copy(quantity = newQuantity)
        }

        val insertStatement = CartItems.insert {
            it[CartItems.cartId] = cartId
            it[CartItems.productId] = productId
            it[CartItems.productSizeId] = productSizeId
            it[CartItems.quantity] = quantity
            it[CartItems.addedAt] = now
        }

        Carts.update({ Carts.id eq cartId }) {
            it[Carts.updatedAt] = now
        }

        CartItem(
            id = insertStatement[CartItems.id],
            cartId = cartId,
            productId = productId,
            productSizeId = productSizeId,
            quantity = quantity,
            addedAt = now
        )
    }

    suspend fun getCartItems(cartId: Long): List<CartItem> = dbQuery {
        CartItems.selectAll()
            .where { CartItems.cartId eq cartId }
            .map { rowToCartItem(it) }
    }

    suspend fun updateCartItemQuantity(
        cartItemId: Long,
        cartId: Long,
        quantity: Int
    ): Int = dbQuery {
        val now = Instant.now()

        val updated = CartItems.update({
            (CartItems.id eq cartItemId) and (CartItems.cartId eq cartId)
        }) {
            it[CartItems.quantity] = quantity
        }

        if (updated > 0) {
            Carts.update({ Carts.id eq cartId }) {
                it[Carts.updatedAt] = now
            }
        }

        updated
    }

    suspend fun removeCartItem(cartItemId: Long, cartId: Long): Int = dbQuery {
        val deleted = CartItems.deleteWhere {
            (CartItems.id eq cartItemId) and (CartItems.cartId eq cartId)
        }

        if (deleted > 0) {
            Carts.update({ Carts.id eq cartId }) {
                it[Carts.updatedAt] = Instant.now()
            }
        }

        deleted
    }

    suspend fun clearCart(cartId: Long): Int = dbQuery {
        val deleted = CartItems.deleteWhere { CartItems.cartId eq cartId }

        if (deleted > 0) {
            Carts.update({ Carts.id eq cartId }) {
                it[Carts.updatedAt] = Instant.now()
            }
        }

        deleted
    }

    suspend fun findCartItemById(cartItemId: Long, cartId: Long): CartItem? = dbQuery {
        CartItems.selectAll()
            .where { (CartItems.id eq cartItemId) and (CartItems.cartId eq cartId) }
            .map { rowToCartItem(it) }
            .singleOrNull()
    }

    private fun rowToCart(row: ResultRow): Cart {
        return Cart(
            id = row[Carts.id],
            userId = row[Carts.userId],
            createdAt = row[Carts.createdAt],
            updatedAt = row[Carts.updatedAt]
        )
    }

    private fun rowToCartItem(row: ResultRow): CartItem {
        return CartItem(
            id = row[CartItems.id],
            cartId = row[CartItems.cartId],
            productId = row[CartItems.productId],
            productSizeId = row[CartItems.productSizeId],
            quantity = row[CartItems.quantity],
            addedAt = row[CartItems.addedAt]
        )
    }
}