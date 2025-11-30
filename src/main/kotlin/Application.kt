package setixx.software

import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import setixx.software.config.configureDatabases
import setixx.software.config.configureMonitoring
import setixx.software.config.configureSecurity
import setixx.software.config.configureSerialization

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureSecurity()
    configureSerialization()
    configureDatabases()
    configureMonitoring()
    configureRouting()
}
