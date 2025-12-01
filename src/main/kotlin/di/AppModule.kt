package setixx.software.di

import io.ktor.server.application.Application
import org.koin.dsl.module
import setixx.software.data.repositories.UserRepository
import setixx.software.services.JwtService
import setixx.software.services.UserService

val appModule = module {
    single { UserRepository() }
    single { UserService(get()) }
    single { JwtService(get(), get(), get()) }
}