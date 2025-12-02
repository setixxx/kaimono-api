package setixx.software.di

import io.ktor.server.application.Application
import org.koin.dsl.module
import setixx.software.data.repositories.AddressRepository
import setixx.software.data.repositories.CartRepository
import setixx.software.data.repositories.DeliveryRepository
import setixx.software.data.repositories.JwtRepository
import setixx.software.data.repositories.OrderRepository
import setixx.software.data.repositories.PaymentMethodRepository
import setixx.software.data.repositories.ProductRepository
import setixx.software.data.repositories.ReviewRepository
import setixx.software.data.repositories.UserRepository
import setixx.software.services.AddressService
import setixx.software.services.CartService
import setixx.software.services.JwtService
import setixx.software.services.OrderService
import setixx.software.services.PaymentMethodService
import setixx.software.services.ReviewService
import setixx.software.services.UserService

val appModule = module {
    single { UserRepository() }
    single { JwtRepository() }
    single { AddressRepository() }
    single { PaymentMethodRepository() }
    single { CartRepository() }
    single { ProductRepository() }
    single { OrderRepository() }
    single { DeliveryRepository() }
    single { ReviewRepository() }

    single { UserService(get()) }
    single { JwtService(get(), get(), get(), get()) }
    single { AddressService(get(), get()) }
    single { PaymentMethodService(get(), get()) }
    single { CartService(get(), get(), get()) }
    single { OrderService(get(), get(), get(), get(), get(), get()) }
    single { ReviewService(get(), get(), get(), get()) }
}