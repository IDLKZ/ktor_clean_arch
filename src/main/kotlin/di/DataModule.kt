package kz.idl.di

import kz.idl.config.DatabaseConfig
import kz.idl.config.FlywayConfig
import kz.idl.data.database.factory.DatabaseFactory
import org.koin.dsl.module

fun dataModule(dbConfig: DatabaseConfig, flywayConfig: FlywayConfig) = module {
    single { dbConfig }
    single { flywayConfig }
    single(createdAtStart = true) { DatabaseFactory(get()).also { it.init() } }
    single { get<DatabaseFactory>().getDatabase() }
}