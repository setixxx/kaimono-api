package setixx.software.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import setixx.software.data.dto.CreatePaymentMethodRequest
import setixx.software.services.PaymentMethodService

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

fun Route.paymentMethodRoutes() {
    val paymentMethodService by inject<PaymentMethodService>()

    route("/payment-methods") {
        post {
            val request = try {
                call.receive<CreatePaymentMethodRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                return@post
            }

            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@post

                val paymentMethod = paymentMethodService.createPaymentMethod(publicId, request)
                call.respond(HttpStatusCode.Created, paymentMethod)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create payment method: ${e.message}")
                e.printStackTrace()
            }
        }

        get {
            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@get

                val paymentMethods = paymentMethodService.getPaymentMethods(publicId)
                call.respond(HttpStatusCode.OK, paymentMethods)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to retrieve payment methods: ${e.message}")
                e.printStackTrace()
            }
        }

        patch("/{id}/set-default") {
            val paymentMethodId = call.parameters["id"]?.toLongOrNull()
            if (paymentMethodId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid payment method ID")
                return@patch
            }

            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@patch

                val paymentMethod = paymentMethodService.setDefaultPaymentMethod(publicId, paymentMethodId)
                call.respond(HttpStatusCode.OK, paymentMethod)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to set default payment method: ${e.message}")
                e.printStackTrace()
            }
        }

        delete("/{id}") {
            val paymentMethodId = call.parameters["id"]?.toLongOrNull()
            if (paymentMethodId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid payment method ID")
                return@delete
            }

            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@delete

                paymentMethodService.deletePaymentMethod(publicId, paymentMethodId)
                call.respond(HttpStatusCode.OK, mapOf("message" to "Payment method deleted successfully"))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to delete payment method: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}