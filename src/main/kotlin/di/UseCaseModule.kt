package kz.idl.di

import kz.idl.domain.usecase.permission.*
import kz.idl.domain.usecase.role.BulkCreateRoleUseCase
import kz.idl.domain.usecase.role.BulkDeleteRoleUseCase
import kz.idl.domain.usecase.role.BulkRestoreRoleUseCase
import kz.idl.domain.usecase.role.BulkUpdateRoleUseCase
import kz.idl.domain.usecase.role.CreateRoleUseCase
import kz.idl.domain.usecase.role.DeleteRoleUseCase
import kz.idl.domain.usecase.role.GetRoleByIdUseCase
import kz.idl.domain.usecase.role.PaginateRoleUseCase
import kz.idl.domain.usecase.role.RestoreRoleUseCase
import kz.idl.domain.usecase.role.UpdateRoleByIdCase
import kz.idl.domain.usecase.role_permission.*
import org.koin.dsl.module

val useCaseModule = module {
    // Role Use Cases
    factory { PaginateRoleUseCase(get()) }
    factory { GetRoleByIdUseCase(get()) }
    factory { CreateRoleUseCase(get(), get()) }
    factory { UpdateRoleByIdCase(get(), get()) }
    factory { DeleteRoleUseCase(get()) }
    factory { RestoreRoleUseCase(get()) }
    factory { BulkCreateRoleUseCase(get(), get()) }
    factory { BulkUpdateRoleUseCase(get(), get()) }
    factory { BulkDeleteRoleUseCase(get()) }
    factory { BulkRestoreRoleUseCase(get()) }

    // Permission Use Cases
    factory { PaginatePermissionUseCase(get()) }
    factory { GetPermissionByIdUseCase(get()) }
    factory { CreatePermissionUseCase(get(), get()) }
    factory { UpdatePermissionByIdUseCase(get(), get()) }
    factory { DeletePermissionUseCase(get()) }
    factory { RestorePermissionUseCase(get()) }
    factory { BulkCreatePermissionUseCase(get(), get()) }
    factory { BulkUpdatePermissionUseCase(get(), get()) }
    factory { BulkDeletePermissionUseCase(get()) }
    factory { BulkRestorePermissionUseCase(get()) }

    // RolePermission Use Cases
    factory { PaginateRolePermissionUseCase(get()) }
    factory { GetRolePermissionByIdUseCase(get()) }
    factory { CreateRolePermissionUseCase(get()) }
    factory { DeleteRolePermissionUseCase(get()) }
    factory { BulkCreateRolePermissionUseCase(get()) }
    factory { BulkDeleteRolePermissionUseCase(get()) }
}