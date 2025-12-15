package kz.idl.domain.mapper

import kz.idl.data.database.table.permission.PermissionTable
import kz.idl.data.database.table.role.RoleTable
import kz.idl.data.database.table.role_permission.RolePermissionTable
import kz.idl.domain.dto.role_permission.RolePermissionDTO
import kz.idl.domain.dto.role_permission.RolePermissionWithRelationsDto
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toRolePermissionDto(): RolePermissionDTO = RolePermissionDTO(
    id = this[RolePermissionTable.id].value,
    roleId = this[RolePermissionTable.roleId].value,
    permissionId = this[RolePermissionTable.permissionId].value
)

fun ResultRow.toRolePermissionWithRelationsDto(): RolePermissionWithRelationsDto = RolePermissionWithRelationsDto(
    id = this[RolePermissionTable.id].value,
    roleId = this[RolePermissionTable.roleId].value,
    permissionId = this[RolePermissionTable.permissionId].value,
    role = this.getOrNull(RoleTable.id)?.let { this.toRoleDto() },
    permission = this.getOrNull(PermissionTable.id)?.let { this.toPermissionDto() }
)
