package setixx.software.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import setixx.software.di.appModule
import setixx.software.module

fun Application.configureKoin() {
    install(Koin) {
        modules(module {
            single { this@configureKoin }
        })
        modules(appModule)
    }
}