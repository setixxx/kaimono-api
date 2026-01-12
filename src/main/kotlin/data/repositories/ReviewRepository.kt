package setixx.software.data.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import setixx.software.data.tables.Reviews
import setixx.software.models.Review
import setixx.software.utils.dbQuery
import java.time.Instant
import java.util.UUID

class ReviewRepository {

    suspend fun createReview(
        userId: Long,
        productId: Long,
        orderId: Long?,
        rating: Short,
        comment: String?
    ): Review = dbQuery {
        val now = Instant.now()
        val publicId = UUID.randomUUID()

        val insertStatement = Reviews.insert {
            it[Reviews.publicId] = publicId
            it[Reviews.userId] = userId
            it[Reviews.productId] = productId
            it[Reviews.orderId] = orderId
            it[Reviews.rating] = rating
            it[Reviews.comment] = comment
            it[Reviews.createdAt] = now
            it[Reviews.updatedAt] = now
        }

        Review(
            id = insertStatement[Reviews.id],
            publicId = publicId,
            userId = userId,
            productId = productId,
            orderId = orderId,
            rating = rating,
            comment = comment,
            createdAt = now,
            updatedAt = now
        )
    }

    suspend fun findReviewsByProductId(productId: Long): List<Review> = dbQuery {
        Reviews.selectAll()
            .where { Reviews.productId eq productId }
            .orderBy(Reviews.createdAt to SortOrder.DESC)
            .map { rowToReview(it) }
    }

    suspend fun findReviewsByUserId(userId: Long): List<Review> = dbQuery {
        Reviews.selectAll()
            .where { Reviews.userId eq userId }
            .orderBy(Reviews.createdAt to SortOrder.DESC)
            .map { rowToReview(it) }
    }

    suspend fun findReviewByPublicId(publicId: UUID): Review? = dbQuery {
        Reviews.selectAll()
            .where { Reviews.publicId eq publicId }
            .map { rowToReview(it) }
            .singleOrNull()
    }

    suspend fun updateReview(
        publicId: UUID,
        userId: Long,
        rating: Short?,
        comment: String?
    ): Int = dbQuery {
        val now = Instant.now()

        Reviews.update({ (Reviews.publicId eq publicId) and (Reviews.userId eq userId) }) {
            rating?.let { value -> it[Reviews.rating] = value }
            comment?.let { value -> it[Reviews.comment] = value }
            it[Reviews.updatedAt] = now
        }
    }

    suspend fun deleteReview(publicId: UUID, userId: Long): Int = dbQuery {
        Reviews.deleteWhere { (Reviews.publicId eq publicId) and (Reviews.userId eq userId) }
    }

    private fun rowToReview(row: ResultRow): Review {
        return Review(
            id = row[Reviews.id],
            publicId = row[Reviews.publicId],
            userId = row[Reviews.userId],
            productId = row[Reviews.productId],
            orderId = row[Reviews.orderId],
            rating = row[Reviews.rating],
            comment = row[Reviews.comment],
            createdAt = row[Reviews.createdAt],
            updatedAt = row[Reviews.updatedAt]
        )
    }
}