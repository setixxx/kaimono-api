package setixx.software.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import setixx.software.data.dto.CreateOrderRequest
import setixx.software.services.OrderService

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

fun Route.orderRoutes() {
    val orderService by inject<OrderService>()

    route("/orders") {
        post {
            val request = try {
                call.receive<CreateOrderRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                return@post
            }

            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@post

                val order = orderService.createOrder(publicId, request)
                call.respond(HttpStatusCode.Created, order)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create order: ${e.message}")
                e.printStackTrace()
            }
        }

        get {
            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@get

                val orders = orderService.getUserOrders(publicId)
                call.respond(HttpStatusCode.OK, orders)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to retrieve orders: ${e.message}")
                e.printStackTrace()
            }
        }

        get("/{publicId}") {
            val orderPublicId = call.parameters["publicId"]
            if (orderPublicId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Invalid order ID")
                return@get
            }

            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@get

                val order = orderService.getOrder(publicId, orderPublicId)
                call.respond(HttpStatusCode.OK, order)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to retrieve order: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}