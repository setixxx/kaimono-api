package setixx.software.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject
import setixx.software.data.dto.RegisterResponse
import setixx.software.data.dto.UpdatePasswordRequest
import setixx.software.data.dto.UpdatePasswordResponse
import setixx.software.data.dto.UpdateUserInfoRequest
import setixx.software.data.dto.UpdateUserInfoResponse
import setixx.software.services.UserService

fun Route.userRoutes() {
    val userService by inject<UserService>()

    post("/update-password") {
        val request = try {
            call.receive<UpdatePasswordRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Invalid request body")
            return@post
        }

        try {
            val principal = call.principal<JWTPrincipal>()
            val email = principal!!.payload.getClaim("email").asString()
            val passwordUpdate = userService.updatePassword(email, request)
            call.respond(
                HttpStatusCode.OK,
                UpdatePasswordResponse("Password updated successfully")
            )
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, "User registration failed " +
                    "${e.message}")
            e.printStackTrace()
        }
    }

    post("/update-user"){
        val request = try {
            call.receive<UpdateUserInfoRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Invalid request body")
            return@post
        }

        try {
            val principal = call.principal<JWTPrincipal>()
            val email = principal!!.payload.getClaim("email").asString()
            val userUpdate = userService.updateUserInfo(email, request)
            call.respond(
                HttpStatusCode.OK,
                UpdateUserInfoResponse("User updated successfully")
            )
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, "Invalid request body")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, "User update failed")
            e.printStackTrace()
        }
    }
}