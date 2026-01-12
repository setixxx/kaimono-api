package setixx.software.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import setixx.software.data.dto.AddToWishlistRequest
import setixx.software.services.WishlistService

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

fun Route.wishlistRoutes() {
    val wishlistService by inject<WishlistService>()

    route("/wishlist") {
        get {
            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@get

                val wishlist = wishlistService.getWishlist(publicId)
                call.respond(HttpStatusCode.OK, wishlist)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to retrieve wishlist: ${e.message}")
                e.printStackTrace()
            }
        }

        post {
            val request = try {
                call.receive<AddToWishlistRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                return@post
            }

            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@post

                val wishlist = wishlistService.addToWishlist(publicId, request)
                call.respond(HttpStatusCode.OK, wishlist)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to add to wishlist: ${e.message}")
                e.printStackTrace()
            }
        }

        delete("/{publicId}") {
            val productPublicId = call.parameters["publicId"]
            if (productPublicId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Invalid product public ID")
                return@delete
            }

            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@delete

                val wishlist = wishlistService.removeFromWishlist(publicId, productPublicId)
                call.respond(HttpStatusCode.OK, wishlist)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to remove from wishlist: ${e.message}")
                e.printStackTrace()
            }
        }

        delete {
            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@delete

                val wishlist = wishlistService.clearWishlist(publicId)
                call.respond(HttpStatusCode.OK, wishlist)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to clear wishlist: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}