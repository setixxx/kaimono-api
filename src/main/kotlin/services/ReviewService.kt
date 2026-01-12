package setixx.software.services

import setixx.software.data.dto.CreateReviewRequest
import setixx.software.data.dto.ReviewResponse
import setixx.software.data.dto.UpdateReviewRequest
import setixx.software.data.repositories.*
import setixx.software.utils.dbQuery
import java.util.UUID

class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository
) {

    suspend fun createReview(
        userPublicId: String,
        request: CreateReviewRequest
    ): ReviewResponse = dbQuery {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        val product = productRepository.findProductByPublicId(UUID.fromString(request.productPublicId))
            ?: throw IllegalArgumentException("Product not found")

        if (request.rating !in 1..5) {
            throw IllegalArgumentException("Rating must be between 1 and 5")
        }

        var orderId: Long? = null

        if (!request.orderPublicId.isNullOrBlank()) {
            val order = orderRepository.findOrderByPublicId(UUID.fromString(request.orderPublicId))
                ?: throw IllegalArgumentException("Order not found")

            if (order.userId != user.id) {
                throw IllegalArgumentException("Order does not belong to this user")
            }

            orderId = order.id
        }

        val review = reviewRepository.createReview(
            userId = user.id,
            productId = product.id,
            orderId = orderId,
            rating = request.rating,
            comment = request.comment
        )

        ReviewResponse(
            id = review.id,
            publicId = review.publicId.toString(),
            userName = user.name,
            rating = review.rating,
            comment = review.comment,
            createdAt = review.createdAt.toString(),
            productPublicId = product.publicId.toString()
        )
    }

    suspend fun getProductReviews(productPublicId: String): List<ReviewResponse> {
        val product = productRepository.findProductByPublicId(UUID.fromString(productPublicId))
            ?: throw IllegalArgumentException("Product not found")

        val reviews = reviewRepository.findReviewsByProductId(product.id)

        return reviews.map { review ->
            val reviewUser = userRepository.findById(review.userId)
                ?: throw IllegalArgumentException("User not found")

            ReviewResponse(
                id = review.id,
                publicId = review.publicId.toString(),
                userName = reviewUser.name,
                rating = review.rating,
                comment = review.comment,
                createdAt = review.createdAt.toString()
            )
        }
    }

    suspend fun getUserReviews(userPublicId: String): List<ReviewResponse> {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        val reviews = reviewRepository.findReviewsByUserId(user.id)

        return reviews.map { review ->
            val product = productRepository.findProductById(review.productId)
                ?: throw IllegalArgumentException("Product not found")

            val imageUrl = productRepository.getPrimaryImageUrl(product.id)

            ReviewResponse(
                id = review.id,
                publicId = review.publicId.toString(),
                userName = user.name,
                rating = review.rating,
                comment = review.comment,
                createdAt = review.createdAt.toString(),
                productPublicId = product.publicId.toString(),
                productName = product.name,
                productImage = imageUrl
            )
        }
    }

    suspend fun updateReview(
        userPublicId: String,
        reviewPublicId: String,
        request: UpdateReviewRequest
    ): ReviewResponse {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        val existingReview = reviewRepository.findReviewByPublicId(UUID.fromString(reviewPublicId))
            ?: throw IllegalArgumentException("Review not found")

        if (existingReview.userId != user.id) {
            throw IllegalArgumentException("Access denied")
        }

        request.rating.let {
            if (it !in 1..5) {
                throw IllegalArgumentException("Rating must be between 1 and 5")
            }
        }

        val updated = reviewRepository.updateReview(
            publicId = UUID.fromString(reviewPublicId),
            userId = user.id,
            rating = request.rating,
            comment = request.comment
        )

        if (updated == 0) {
            throw IllegalArgumentException("Failed to update review")
        }

        val updatedReview = reviewRepository.findReviewByPublicId(UUID.fromString(reviewPublicId))
            ?: throw IllegalArgumentException("Review not found after update")

        val product = productRepository.findProductById(updatedReview.productId)
            ?: throw IllegalArgumentException("Product not found")

        return ReviewResponse(
            id = updatedReview.id,
            publicId = updatedReview.publicId.toString(),
            userName = user.name,
            rating = updatedReview.rating,
            comment = updatedReview.comment,
            createdAt = updatedReview.createdAt.toString(),
            productPublicId = product.publicId.toString()
        )
    }

    suspend fun deleteReview(
        userPublicId: String,
        reviewPublicId: String
    ) {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        val deleted = reviewRepository.deleteReview(UUID.fromString(reviewPublicId), user.id)

        if (deleted == 0) {
            throw IllegalArgumentException("Review not found or access denied")
        }
    }
}