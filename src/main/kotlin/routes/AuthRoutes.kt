package setixx.software.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import setixx.software.data.dto.LoginResponse
import setixx.software.data.dto.LoginUserRequest
import setixx.software.data.dto.RegisterResponse
import setixx.software.data.dto.RegisterUserRequest
import setixx.software.services.JwtService
import setixx.software.services.UserService

fun Route.authRoutes() {
    val userService by inject<UserService>()
    val jwtService by inject<JwtService>()

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
                call.respond(HttpStatusCode.Conflict, "User registration failed " +
                        "${e.message}")
                e.printStackTrace()
            }
        }
        post("/login") {
            val request = try {
                call.receive<LoginUserRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                return@post
            }

            try {
                val user = call.receive<LoginUserRequest>()

                val token: String? = jwtService.createJwtToken(user)

                token?.let {
                    call.respond(hashMapOf("token" to token))
                } ?: call.respond(
                    message = HttpStatusCode.Unauthorized
                )
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, "User login failed")
                e.printStackTrace()
            }
        }
    }
}