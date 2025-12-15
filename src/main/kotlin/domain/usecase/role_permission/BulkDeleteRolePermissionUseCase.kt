package kz.idl.domain.usecase.role_permission

import kz.idl.domain.repository.role_permission.RolePermissionRepository

class BulkDeleteRolePermissionUseCase(
    private val rolePermissionRepository: RolePermissionRepository
) {
    suspend operator fun invoke(ids: List<Long>, hardDelete: Boolean = true): Int {
        if (ids.isEmpty()) {
            return 0
        }

        // Bulk delete role-permission mappings (hard delete by default)
        return rolePermissionRepository.bulkDelete(ids, hardDelete)
    }
}
