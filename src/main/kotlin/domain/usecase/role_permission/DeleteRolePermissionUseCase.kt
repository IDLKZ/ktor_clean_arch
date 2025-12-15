package kz.idl.domain.usecase.role_permission

import kz.idl.domain.exception.NotFoundException
import kz.idl.domain.mapper.toRolePermissionDto
import kz.idl.domain.repository.role_permission.RolePermissionRepository

class DeleteRolePermissionUseCase(
    private val rolePermissionRepository: RolePermissionRepository
) {
    suspend operator fun invoke(id: Long, hardDelete: Boolean = true): Boolean {
        // Check if mapping exists
        val existing = rolePermissionRepository.findByLongId(
            id = id,
            showDeleted = null,
            includeJoin = false,
            mapper = { it.toRolePermissionDto() }
        ) ?: throw NotFoundException("role_permission.not_found")

        // Delete mapping (hard delete by default for junction tables)
        return rolePermissionRepository.delete(id, hardDelete)
    }
}
