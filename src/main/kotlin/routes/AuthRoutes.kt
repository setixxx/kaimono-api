package setixx.software.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import setixx.software.data.dto.RegisterResponse
import setixx.software.data.dto.RegisterUserRequest
import setixx.software.services.UserService

fun Route.authRoutes() {
    val userService by inject<UserService>()

    route("/auth") {
        post("/register") {
            val request = try {
                call.receive<RegisterUserRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                return@post
            }

            try {
                val user = userService.register(request)

                call.respond(
                    HttpStatusCode.Created,
                    RegisterResponse(publicId = user.publicId.toString())
                )
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, "User registration failed")
                e.printStackTrace()
            }
        }
    }
}