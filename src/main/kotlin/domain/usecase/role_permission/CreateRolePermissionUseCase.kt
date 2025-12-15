package kz.idl.domain.usecase.role_permission

import kz.idl.data.database.table.role_permission.RolePermissionTable
import kz.idl.domain.dto.role_permission.RolePermissionCDTO
import kz.idl.domain.dto.role_permission.RolePermissionDTO
import kz.idl.domain.exception.ConflictException
import kz.idl.domain.exception.InternalServerException
import kz.idl.domain.mapper.toRolePermissionDto
import kz.idl.domain.repository.role_permission.RolePermissionRepository
import kz.idl.infrastructure.filter.role_permission.RolePermissionFilter

class CreateRolePermissionUseCase(
    private val rolePermissionRepository: RolePermissionRepository
) {
    suspend operator fun invoke(dto: RolePermissionCDTO): RolePermissionDTO {
        // Check if this role-permission mapping already exists
        val existing = rolePermissionRepository.findOneByFilter(
            filter = RolePermissionFilter.byRoleAndPermission(dto.roleId, dto.permissionId),
            mapper = { it.toRolePermissionDto() }
        )

        if (existing != null) {
            throw ConflictException("role_permission.already_exists")
        }

        // Create role-permission mapping
        return rolePermissionRepository.create(
            insertBlock = dto.createEntity(RolePermissionTable),
            mapper = { it.toRolePermissionDto() }
        ) ?: throw InternalServerException("role_permission.creation_failed")
    }
}
