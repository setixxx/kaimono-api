package setixx.software.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import javax.sql.DataSource

fun Application.configureDatabases() {
    runFlyway()
    val dataSource: DataSource = connectToPostgres()
    Database.connect(dataSource)
}

fun Application.connectToPostgres(): DataSource {
    val driver = environment.config.propertyOrNull("postgres.driver")?.getString() ?: "org.postgresql.Driver"

    val config = HikariConfig().apply {
        driverClassName = driver
        jdbcUrl = environment.config.property("postgres.url").getString()
        username = environment.config.property("postgres.user").getString()
        password = environment.config.property("postgres.password").getString()
        maximumPoolSize = 10
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }
    return HikariDataSource(config)
}
