package setixx.software

import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import setixx.software.data.repositories.user.UserRepository
import setixx.software.routes.authRoutes

fun Application.configureRouting() {
    routing {
        route("/auth") {
            authRoutes()
        }

        authenticate {
            get("/") {
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.payload?.getClaim("email")?.asString()

                call.respondText("Hello, $email! You are authorized.")
            }
        }
    }
}