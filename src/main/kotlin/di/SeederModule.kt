package kz.idl.di

import io.ktor.server.application.ApplicationEnvironment
import kz.idl.infrastructure.seeder.permission.PermissionSeeder
import kz.idl.infrastructure.seeder.role.RoleSeeder
import kz.idl.infrastructure.seeder.role_permission.RolePermissionSeeder
import org.koin.dsl.module
import kotlin.coroutines.EmptyCoroutineContext.get

fun seederModule(applicationEnvironment: ApplicationEnvironment) = module {
    single { RoleSeeder(get(), applicationEnvironment) }
    single { PermissionSeeder(get(), applicationEnvironment) }
    single { RolePermissionSeeder(get(), get(), get(), applicationEnvironment) }
}