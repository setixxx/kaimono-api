package setixx.software.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import setixx.software.data.dto.AddToCartRequest
import setixx.software.data.dto.UpdateCartItemRequest
import setixx.software.services.CartService

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

fun Route.cartRoutes() {
    val cartService by inject<CartService>()

    route("/cart") {
        get {
            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@get

                val cart = cartService.getCart(publicId)
                call.respond(HttpStatusCode.OK, cart)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to retrieve cart: ${e.message}")
                e.printStackTrace()
            }
        }

        post("/items") {
            val request = try {
                call.receive<AddToCartRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                return@post
            }

            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@post

                val cart = cartService.addToCart(publicId, request)
                call.respond(HttpStatusCode.OK, cart)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to add item to cart: ${e.message}")
                e.printStackTrace()
            }
        }

        patch("/items/{publicId}") {
            val productPublicId = call.parameters["publicId"]
            if (productPublicId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Invalid product public ID")
                return@patch
            }

            val request = try {
                call.receive<UpdateCartItemRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                return@patch
            }

            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@patch

                val cart = cartService.updateCartItemByProductPublicId(publicId, productPublicId, request)
                call.respond(HttpStatusCode.OK, cart)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to update cart item: ${e.message}")
                e.printStackTrace()
            }
        }

        delete("/items/{publicId}") {
            val productPublicId = call.parameters["publicId"]
            if (productPublicId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Invalid product public ID")
                return@delete
            }

            val size = call.request.queryParameters["size"]
            if (size.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Size parameter is required")
                return@delete
            }

            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@delete

                val cart = cartService.removeCartItemByProductPublicId(publicId, productPublicId, size)
                call.respond(HttpStatusCode.OK, cart)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to remove cart item: ${e.message}")
                e.printStackTrace()
            }
        }

        delete {
            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@delete

                val cart = cartService.clearCart(publicId)
                call.respond(HttpStatusCode.OK, cart)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to clear cart: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}