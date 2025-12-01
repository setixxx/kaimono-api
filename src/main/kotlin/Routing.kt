package setixx.software

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import setixx.software.data.repositories.UserRepository
import setixx.software.routes.authRoutes

fun Application.configureRouting() {
    val userRepository = UserRepository()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        route("/auth") {
            authRoutes()
        }
    }
}