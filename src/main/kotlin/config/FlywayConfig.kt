package kz.idl.config

import io.ktor.server.config.ApplicationConfig

data class FlywayConfig(
    val enabled: Boolean,
    val locations: String,
    val baselineOnMigrate: Boolean,
    val validateOnMigrate: Boolean
) {
    companion object {
        fun load(config: ApplicationConfig): FlywayConfig {
            val fw = config.config("flyway")
            return FlywayConfig(
                enabled = fw.propertyOrNull("enabled")?.getString()?.toBoolean() ?: true,
                locations = fw.propertyOrNull("locations")?.getString() ?: "classpath:db/migration",
                baselineOnMigrate = fw.propertyOrNull("baselineOnMigrate")?.getString()?.toBoolean() ?: true,
                validateOnMigrate = fw.propertyOrNull("validateOnMigrate")?.getString()?.toBoolean() ?: true
            )
        }
    }
}
