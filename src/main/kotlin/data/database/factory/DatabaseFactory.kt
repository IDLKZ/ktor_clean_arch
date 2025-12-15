package kz.idl.data.database.factory

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kz.idl.config.DatabaseConfig
import org.jetbrains.exposed.v1.jdbc.Database
import org.slf4j.LoggerFactory
import javax.sql.DataSource

class DatabaseFactory(private val config: DatabaseConfig) {
    private val logger = LoggerFactory.getLogger(DatabaseFactory::class.java)
    private lateinit var dataSource: HikariDataSource
    private lateinit var database: Database

    fun init(): Database {
        dataSource = createDataSource()
        database = Database.connect(dataSource)
        logger.info("✅ Database connection pool initialized: ${config.poolName}")
        return database
    }

    private fun createDataSource(): HikariDataSource {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = config.jdbcUrl
            username = config.username
            password = config.password
            driverClassName = config.driverClassName
            maximumPoolSize = config.maximumPoolSize
            minimumIdle = config.minimumIdle
            idleTimeout = config.idleTimeout
            connectionTimeout = config.connectionTimeout
            maxLifetime = config.maxLifetime
            poolName = config.poolName
            connectionTestQuery = "SELECT 1"
            isAutoCommit = true
        }
        return HikariDataSource(hikariConfig)
    }

    fun getDatabase(): Database = database

    fun getDataSource(): DataSource = dataSource

    fun close() {
        if (::dataSource.isInitialized && !dataSource.isClosed) {
            dataSource.close()
            logger.info("🔒 Database connection pool closed")
        }
    }

}