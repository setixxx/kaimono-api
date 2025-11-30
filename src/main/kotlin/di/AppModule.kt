package setixx.software.di

import org.koin.dsl.module
import setixx.software.data.repositories.UserRepository
import setixx.software.services.UserService

val appModule = module {
    single { UserRepository() }
    single { UserService(get()) }
}