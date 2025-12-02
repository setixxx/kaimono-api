package setixx.software

import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import setixx.software.data.repositories.UserRepository
import setixx.software.routes.addressRoutes
import setixx.software.routes.authRoutes
import setixx.software.routes.cartRoutes
import setixx.software.routes.paymentMethodRoutes
import setixx.software.routes.userRoutes

fun Application.configureRouting() {
    routing {
        route("/auth") {
            authRoutes()
        }

        authenticate {
            userRoutes()
            addressRoutes()
            paymentMethodRoutes()
            cartRoutes()

            get("/") {
                val principal = call.principal<JWTPrincipal>()
                val publicId = principal?.payload?.getClaim("publicId")?.asString()

                call.respondText("Hello, $publicId! You are authorized.")
            }
        }
    }
}