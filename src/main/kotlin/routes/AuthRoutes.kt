package setixx.software.routes

import com.auth0.jwt.JWT
import io.ktor.http.*
import io.ktor.server.application.ApplicationCall
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


private suspend fun ApplicationCall.getRefreshToken(): String? {
    val authHeader = request.headers["Authorization"]
    val token = authHeader?.removePrefix("Bearer ")?.trim()

    if (token.isNullOrBlank()) {
        respond(HttpStatusCode.Unauthorized, "Missing or invalid Authorization header")
        return null
    }

    return try {
        val decoded = JWT.decode(token)
        val tokenType = decoded.getClaim("type").asString()

        if (tokenType != "refresh") {
            respond(HttpStatusCode.Unauthorized, "Invalid token type. Expected refresh token")
            return null
        }

        token
    } catch (e: Exception) {
        respond(HttpStatusCode.Unauthorized, "Invalid token format")
        null
    }
}

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
            call.respond(HttpStatusCode.Conflict, "User registration failed")
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
        try {
            val refreshToken = call.getRefreshToken() ?: return@post

            val deviceInfo = call.request.headers["User-Agent"] ?: "Unknown"
            val response: LoginResponse = jwtService.reissueTokens(
                refreshToken = refreshToken,
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
        try {
            val refreshToken = call.getRefreshToken() ?: return@post

            jwtService.logout(refreshToken)
            call.respond(HttpStatusCode.OK, "Logged out successfully")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Logout failed")
            e.printStackTrace()
        }
    }
}