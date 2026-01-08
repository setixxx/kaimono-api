package setixx.software.routes

import com.auth0.jwt.JWT
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import setixx.software.data.dto.UpdatePasswordRequest
import setixx.software.data.dto.UpdatePasswordResponse
import setixx.software.data.dto.UpdateUserInfoRequest
import setixx.software.data.dto.UpdateUserInfoResponse
import setixx.software.services.UserService

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

fun Route.userRoutes() {
    val userService by inject<UserService>()

    route("/user") {
        post("/update-password") {
            val request = try {
                call.receive<UpdatePasswordRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                return@post
            }

            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@post

                userService.updatePassword(publicId, request)
                call.respond(
                    HttpStatusCode.OK,
                    UpdatePasswordResponse("Password updated successfully")
                )
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Password update failed: ${e.message}")
                e.printStackTrace()
            }
        }

        post("/update-user") {
            val request = try {
                call.receive<UpdateUserInfoRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                return@post
            }

            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@post

                userService.updateUserInfo(publicId, request)
                val user = userService.getUserInfo(publicId)
                call.respond(
                    HttpStatusCode.OK,
                    user
                )
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "User update failed: ${e.message}")
                e.printStackTrace()
            }
        }

        get("/me") {
            try {
                val publicId = call.getPublicIdFromAccessToken() ?: return@get
                val user = userService.getUserInfo(publicId)
                call.respond(HttpStatusCode.OK, user)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "User get failed: ${e.message}")
            }
        }
    }
}
