package setixx.software.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import setixx.software.data.dto.LoginResponse
import setixx.software.data.dto.LoginUserRequest
import setixx.software.data.dto.RefreshTokenRequest
import setixx.software.data.dto.RegisterResponse
import setixx.software.data.dto.RegisterUserRequest
import setixx.software.services.JwtService
import setixx.software.services.UserService

fun Route.authRoutes() {
    val userService by inject<UserService>()
    val jwtService by inject<JwtService>()

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
            call.respond(HttpStatusCode.BadRequest, "Invalid request body ${e.localizedMessage}")
            return@post
        }

        try {
            val deviceInfo = call.request.headers["User-Agent"] ?: "Unknown"
            val accessToken: String? = jwtService.createAccessToken(request)
            val refreshToken: String = jwtService.createRefreshToken(request, deviceInfo)

            accessToken?.let {
                call.respond(LoginResponse(refreshToken, accessToken))
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

    post("/refresh") {
        val request = try {
            call.receive<RefreshTokenRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Invalid request body ${e.localizedMessage}")
            return@post
        }

        try {
            val deviceInfo = call.request.headers["User-Agent"] ?: "Unknown"
            val response: LoginResponse = jwtService.reissueTokens(
                refreshToken = request.refreshToken,
                deviceInfo = deviceInfo
            )

            call.respond(response)
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.Unauthorized, e.message ?: "Invalid refresh token")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Token refresh failed")
            e.printStackTrace()
        }
    }

    post("/logout") {
        val request = try {
            call.receive<RefreshTokenRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Invalid request body")
            return@post
        }

        try {
            jwtService.logout(request.refreshToken)
            call.respond(HttpStatusCode.OK, "Logged out successfully")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Logout failed")
            e.printStackTrace()
        }
    }
}