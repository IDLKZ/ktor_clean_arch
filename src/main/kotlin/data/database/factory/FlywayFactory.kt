package kz.idl.data.database.factory

import kz.idl.config.DatabaseConfig
import kz.idl.config.FlywayConfig
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory

object FlywayMigration {

    private val logger = LoggerFactory.getLogger(FlywayMigration::class.java)

    fun run(dbConfig: DatabaseConfig, flywayConfig: FlywayConfig) {
        if (!flywayConfig.enabled) {
            logger.info("⏭️ Flyway migrations disabled")
            return
        }

        logger.info("🚀 Running Flyway migrations...")

        val flyway = Flyway.configure()
            .dataSource(dbConfig.jdbcUrl, dbConfig.username, dbConfig.password)
            .locations(flywayConfig.locations)
            .baselineOnMigrate(flywayConfig.baselineOnMigrate)
            .validateOnMigrate(flywayConfig.validateOnMigrate)
            .load()

        try {
            logMigrationStatus(flyway)
            val result = flyway.migrate()

            if (result.migrationsExecuted > 0) {
                logger.info("✅ Migrations applied: ${result.migrationsExecuted}")
                logger.info("📦 Target schema version: ${result.targetSchemaVersion}")
            } else {
                logger.info("✅ Database is up to date")
            }
        } catch (e: Exception) {
            logger.error("❌ Migration failed: ${e.message}", e)
            throw e
        }
    }

    private fun logMigrationStatus(flyway: Flyway) {
        val info = flyway.info()
        if (info.all().isNotEmpty()) {
            logger.info("📊 Migration status:")
            info.all().forEach { migration ->
                logger.info("  - ${migration.version}: ${migration.description} [${migration.state}]")
            }
        }
    }
}