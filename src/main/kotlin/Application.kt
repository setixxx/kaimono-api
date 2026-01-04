package setixx.software

import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import org.koin.ktor.ext.inject
import setixx.software.config.configureDatabases
import setixx.software.config.configureMonitoring
import setixx.software.config.configureSecurity
import setixx.software.config.configureSerialization
import setixx.software.data.repositories.UserRepository
import setixx.software.plugins.configureCORS
import setixx.software.plugins.configureKoin
import setixx.software.services.JwtService
import setixx.software.services.UserService

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    val jwtService by inject<JwtService>()
    configureKoin()
    configureCORS()
    configureSerialization()
    configureSecurity(jwtService)
    configureDatabases()
    configureMonitoring()
    configureRouting()
}
