package kz.idl.infrastructure.repository.role

import kz.idl.data.database.table.permission.PermissionTable
import kz.idl.data.database.table.role.RoleTable
import kz.idl.data.database.table.role_permission.RolePermissionTable
import kz.idl.domain.repository.role.RoleRepository
import kz.idl.infrastructure.repository.BaseRepositoryImpl
import org.jetbrains.exposed.v1.jdbc.Query
import org.jetbrains.exposed.v1.jdbc.selectAll

class RoleRepositoryImpl(
    table: RoleTable
) : BaseRepositoryImpl<RoleTable>(table), RoleRepository {

    override fun baseJoinQuery(): Query =     table
        .leftJoin(RolePermissionTable)
        .leftJoin(PermissionTable)
        .selectAll()

}
