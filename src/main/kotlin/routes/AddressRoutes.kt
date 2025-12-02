package setixx.software.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import setixx.software.data.dto.CreateAddressRequest
import setixx.software.services.AddressService

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

fun Route.addressRoutes() {
    val addressService by inject<AddressService>()

    route("/addresses") {
        post {
            val request = try {
                call.receive<CreateAddressRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                return@post
            }

            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@post

                val address = addressService.createAddress(publicId, request)
                call.respond(HttpStatusCode.Created, address)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create address: ${e.message}")
                e.printStackTrace()
            }
        }

        get {
            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@get

                val addresses = addressService.getAddresses(publicId)
                call.respond(HttpStatusCode.OK, addresses)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to retrieve addresses: ${e.message}")
                e.printStackTrace()
            }
        }

        patch("/{id}/set-default") {
            val addressId = call.parameters["id"]?.toLongOrNull()
            if (addressId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid address ID")
                return@patch
            }

            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@patch

                val address = addressService.setDefaultAddress(publicId, addressId)
                call.respond(HttpStatusCode.OK, address)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to set default address: ${e.message}")
                e.printStackTrace()
            }
        }

        delete("/{id}") {
            val addressId = call.parameters["id"]?.toLongOrNull()
            if (addressId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid address ID")
                return@delete
            }

            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@delete

                addressService.deleteAddress(publicId, addressId)
                call.respond(HttpStatusCode.OK, mapOf("message" to "Address deleted successfully"))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to delete address: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}