package setixx.software.plugins

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.maxAgeDuration
import io.ktor.server.plugins.cors.routing.CORS

fun Application.configureCORS() {
    install(CORS) {
        anyHost()

        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Head)

        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Accept)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.AccessControlAllowHeaders)

        exposeHeader(HttpHeaders.Authorization)
        exposeHeader(HttpHeaders.ContentType)

        allowCredentials = true

        maxAgeDuration = kotlin.time.Duration.parse("24h")
    }
}