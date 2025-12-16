package kz.idl

import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking
import kz.idl.config.DatabaseConfig
import kz.idl.config.FlywayConfig
import kz.idl.data.database.factory.FlywayMigration
import kz.idl.di.dataModule
import kz.idl.di.repositoryModule
import kz.idl.di.seederModule
import kz.idl.di.storageModule
import kz.idl.di.useCaseModule
import kz.idl.di.validationModule
import kz.idl.infrastructure.plugin.configureExceptionHandler
import kz.idl.infrastructure.plugins.configureLocalization
import kz.idl.infrastructure.plugins.configureSerialization
import kz.idl.infrastructure.seeder.RegisterSeeder
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureLocalization()
    configureSerialization()
    val dbConfig = DatabaseConfig.load(environment.config)
    val flywayConfig = FlywayConfig.load(environment.config)
    configureExceptionHandler()
    // Migrations first
    FlywayMigration.run(dbConfig, flywayConfig)


    // Koin DI
    install(Koin) {
        slf4jLogger()
        modules(
            //Импорт модулей
            dataModule(dbConfig, flywayConfig),
            repositoryModule,
            useCaseModule,
            validationModule,
            seederModule(environment),
            storageModule(environment.config)
        )
    }
    runBlocking { RegisterSeeder().runSeeder() }
    // Graceful shutdown
    environment.monitor.subscribe(ApplicationStopped) {}

    configureRouting()
}
