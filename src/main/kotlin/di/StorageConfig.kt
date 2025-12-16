package kz.idl.di

import io.ktor.server.config.ApplicationConfig
import kz.idl.config.StorageConfig
import kz.idl.domain.service.file.FileService
import kz.idl.infrastructure.service.file.FileServiceImpl
import org.koin.dsl.module

fun storageModule(config: ApplicationConfig) = module {
    single { StorageConfig.load(config) }
    single<FileService> { FileServiceImpl(get()) }
}