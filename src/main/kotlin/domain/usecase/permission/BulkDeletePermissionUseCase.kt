package kz.idl.domain.usecase.permission

import kz.idl.domain.repository.permission.PermissionRepository

class BulkDeletePermissionUseCase(
    private val permissionRepository: PermissionRepository
) {
    suspend operator fun invoke(ids: List<Long>, hardDelete: Boolean = false): Int {
        if (ids.isEmpty()) {
            return 0
        }

        // Bulk delete permissions (soft delete by default)
        return permissionRepository.bulkDelete(ids, hardDelete)
    }
}
