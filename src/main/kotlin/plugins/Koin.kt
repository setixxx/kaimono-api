package setixx.software.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.ktor.plugin.Koin
import setixx.software.di.appModule

fun Application.configureKoin() {
    install(Koin) {
        modules(appModule)
    }
}