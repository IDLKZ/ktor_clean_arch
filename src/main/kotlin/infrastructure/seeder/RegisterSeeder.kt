package kz.idl.infrastructure.seeder

import io.ktor.server.application.ApplicationEnvironment
import kotlinx.coroutines.Dispatchers
import kz.idl.infrastructure.seeder.permission.PermissionSeeder
import kz.idl.infrastructure.seeder.role.RoleSeeder
import kz.idl.infrastructure.seeder.role_permission.RolePermissionSeeder
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class RegisterSeeder() : KoinComponent {

    val roleSeeder = get<RoleSeeder>()
    val permissionSeeder = get<PermissionSeeder>()
    val rolePermissionSeeder = get<RolePermissionSeeder>()

    suspend fun runSeeder(){
        newSuspendedTransaction(Dispatchers.IO) {
            roleSeeder.seed()
            permissionSeeder.seed()
            rolePermissionSeeder.seed()
        }
    }
}