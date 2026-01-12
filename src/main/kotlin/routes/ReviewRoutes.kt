package setixx.software.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import setixx.software.data.dto.CreateReviewRequest
import setixx.software.data.dto.UpdateReviewRequest
import setixx.software.services.ReviewService

private suspend fun ApplicationCall.getPublicIdFromAccessToken(): String? {
    val principal = principal<JWTPrincipal>()

    if (principal == null) {
        respond(HttpStatusCode.Unauthorized, "Missing authentication")
        return null
    }

    val tokenType = principal.payload.getClaim("type").asString()
    if (tokenType != "access") {
        respond(HttpStatusCode.Unauthorized, "Invalid token type. Expected access token")
        return null
    }

    val publicId = principal.payload.getClaim("publicId").asString()
    if (publicId.isNullOrBlank()) {
        respond(HttpStatusCode.Unauthorized, "Invalid token payload")
        return null
    }

    return publicId
}

fun Route.reviewRoutes() {
    val reviewService by inject<ReviewService>()

    route("/reviews") {
        post {
            val request = try {
                call.receive<CreateReviewRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                return@post
            }

            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@post

                val review = reviewService.createReview(publicId, request)
                call.respond(HttpStatusCode.Created, review)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create review: ${e.message}")
                e.printStackTrace()
            }
        }

        get("/my") {
            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@get

                val reviews = reviewService.getUserReviews(publicId)
                call.respond(HttpStatusCode.OK, reviews)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to retrieve reviews: ${e.message}")
                e.printStackTrace()
            }
        }

        patch("/{publicId}") {
            val reviewPublicId = call.parameters["publicId"]
            if (reviewPublicId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Invalid review ID")
                return@patch
            }

            val request = try {
                call.receive<UpdateReviewRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                return@patch
            }

            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@patch

                val review = reviewService.updateReview(publicId, reviewPublicId, request)
                call.respond(HttpStatusCode.OK, review)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to update review: ${e.message}")
                e.printStackTrace()
            }
        }

        delete("/{publicId}") {
            val reviewPublicId = call.parameters["publicId"]
            if (reviewPublicId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Invalid review ID")
                return@delete
            }

            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@delete

                reviewService.deleteReview(publicId, reviewPublicId)
                call.respond(HttpStatusCode.OK, mapOf("message" to "Review deleted successfully"))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to delete review: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}

fun Route.publicReviewRoutes() {
    val reviewService by inject<ReviewService>()

    route("/products") {
        get("/{publicId}/reviews") {
            val productPublicId = call.parameters["publicId"]
            if (productPublicId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Invalid product ID")
                return@get
            }

            try {
                val reviews = reviewService.getProductReviews(productPublicId)
                call.respond(HttpStatusCode.OK, reviews)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to retrieve reviews: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}