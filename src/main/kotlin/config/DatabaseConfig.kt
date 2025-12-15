package kz.idl.config

import io.ktor.server.config.ApplicationConfig

data class DatabaseConfig(
    val jdbcUrl: String,
    val username: String,
    val password: String,
    val driverClassName: String,
    val maximumPoolSize: Int,
    val minimumIdle: Int,
    val idleTimeout: Long,
    val connectionTimeout: Long,
    val maxLifetime: Long,
    val poolName: String
) {
    companion object {
        fun load(config: ApplicationConfig): DatabaseConfig {
            val db = config.config("database")
            return DatabaseConfig(
                jdbcUrl = db.property("jdbcUrl").getString(),
                username = db.property("username").getString(),
                password = db.property("password").getString(),
                driverClassName = db.propertyOrNull("driverClassName")?.getString()
                    ?: "org.postgresql.Driver",
                maximumPoolSize = db.propertyOrNull("maximumPoolSize")?.getString()?.toInt() ?: 10,
                minimumIdle = db.propertyOrNull("minimumIdle")?.getString()?.toInt() ?: 2,
                idleTimeout = db.propertyOrNull("idleTimeout")?.getString()?.toLong() ?: 600_000,
                connectionTimeout = db.propertyOrNull("connectionTimeout")?.getString()?.toLong() ?: 30_000,
                maxLifetime = db.propertyOrNull("maxLifetime")?.getString()?.toLong() ?: 1_800_000,
                poolName = db.propertyOrNull("poolName")?.getString() ?: "MainHikariPool"
            )
        }
    }
}
