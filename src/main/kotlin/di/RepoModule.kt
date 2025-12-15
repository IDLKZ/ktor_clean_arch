package kz.idl.di

import kz.idl.data.database.table.permission.PermissionTable
import kz.idl.data.database.table.role.RoleTable
import kz.idl.data.database.table.role_permission.RolePermissionTable
import kz.idl.domain.repository.permission.PermissionRepository
import kz.idl.domain.repository.role.RoleRepository
import kz.idl.domain.repository.role_permission.RolePermissionRepository
import kz.idl.infrastructure.repository.permission.PermissionRepositoryImpl
import kz.idl.infrastructure.repository.role.RoleRepositoryImpl
import kz.idl.infrastructure.repository.role_permission.RolePermissionRepositoryImpl
import org.koin.dsl.module
import kotlin.math.sin


val repositoryModule = module {
    single<RoleRepository> { RoleRepositoryImpl(RoleTable) }
    single<PermissionRepository> { PermissionRepositoryImpl() }
    single<RolePermissionRepository>{ RolePermissionRepositoryImpl(RolePermissionTable) }
}