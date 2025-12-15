package kz.idl.domain.usecase.role_permission

import kz.idl.data.database.table.role_permission.RolePermissionTable
import kz.idl.domain.dto.role_permission.RolePermissionCDTO
import kz.idl.domain.dto.role_permission.RolePermissionDTO
import kz.idl.domain.mapper.toRolePermissionDto
import kz.idl.domain.repository.role_permission.RolePermissionRepository

class BulkCreateRolePermissionUseCase(
    private val rolePermissionRepository: RolePermissionRepository
) {
    suspend operator fun invoke(dtos: List<RolePermissionCDTO>): List<RolePermissionDTO> {
        if (dtos.isEmpty()) {
            return emptyList()
        }

        // Bulk create role-permission mappings
        return rolePermissionRepository.bulkCreate(
            items = dtos,
            insertBlock = { dto -> dto.insertBlock(RolePermissionTable, this) },
            mapper = { it.toRolePermissionDto() }
        )
    }
}
