package setixx.software.config

import io.ktor.server.application.Application
import io.ktor.server.application.log
import org.flywaydb.core.Flyway

fun Application.runFlyway() {
    val url = environment.config.property("postgres.url").getString()
    val user = environment.config.property("postgres.user").getString()
    val password = environment.config.property("postgres.password").getString()

    if (url.contains("postgresql")) {
        val flyway = Flyway.configure()
            .dataSource(url, user, password)
            .load()
        try {
            flyway.migrate()
            log.info("Flyway migrations applied successfully")
        } catch (e: Exception) {
            log.error("Flyway migration failed", e)
            throw e
        }
    }
}