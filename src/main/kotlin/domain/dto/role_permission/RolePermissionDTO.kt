package kz.idl.domain.dto.role_permission

import kotlinx.serialization.Serializable
import kz.idl.data.database.table.role_permission.RolePermissionTable
import kz.idl.domain.dto.BaseCreateDTO
import kz.idl.domain.dto.BaseOneCreateDTO
import kz.idl.domain.dto.BaseUpdateDTO
import kz.idl.domain.dto.permission.PermissionDto
import kz.idl.domain.dto.role.RoleDto
import org.jetbrains.exposed.v1.core.statements.BatchInsertStatement
import org.jetbrains.exposed.v1.core.statements.InsertStatement
import org.jetbrains.exposed.v1.core.statements.UpdateStatement

@Serializable
data class RolePermissionDTO(
    val id: Long,
    val roleId: Long,
    val permissionId: Long,
)

@Serializable
data class RolePermissionWithRelationsDto(
    val id: Long,
    val roleId: Long,
    val permissionId: Long,
    val role: RoleDto?,
    val permission: PermissionDto?
)

@Serializable
data class RolePermissionCDTO (
    val roleId: Long,
    val permissionId: Long
) : BaseUpdateDTO<RolePermissionTable>, BaseCreateDTO<RolePermissionTable>, BaseOneCreateDTO<RolePermissionTable>{
    override fun updateBlock(
        table: RolePermissionTable,
        stmt: UpdateStatement
    ) {
        stmt[table.roleId] = roleId
        stmt[table.permissionId] = permissionId
    }

    override fun insertBlock(
        table: RolePermissionTable,
        stmt: BatchInsertStatement
    ) {
        stmt[table.roleId] = roleId
        stmt[table.permissionId] = permissionId
    }

    override fun createEntity(table: RolePermissionTable): InsertStatement<Number>.() -> Unit  = {
        this[RolePermissionTable.roleId] = roleId
        this[RolePermissionTable.permissionId] = permissionId
    }

}
