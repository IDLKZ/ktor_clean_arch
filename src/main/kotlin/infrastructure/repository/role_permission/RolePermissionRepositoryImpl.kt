package kz.idl.infrastructure.repository.role_permission

import kz.idl.data.database.table.permission.PermissionTable
import kz.idl.data.database.table.role.RoleTable
import kz.idl.data.database.table.role_permission.RolePermissionTable
import kz.idl.domain.repository.role_permission.RolePermissionRepository
import kz.idl.infrastructure.repository.BaseRepositoryImpl
import org.jetbrains.exposed.v1.jdbc.Query
import org.jetbrains.exposed.v1.jdbc.selectAll

class RolePermissionRepositoryImpl constructor(table: RolePermissionTable) : BaseRepositoryImpl<RolePermissionTable>(table), RolePermissionRepository {
    override fun baseJoinQuery(): Query =     table
        .leftJoin(RoleTable)
        .leftJoin(PermissionTable)
        .selectAll()
}