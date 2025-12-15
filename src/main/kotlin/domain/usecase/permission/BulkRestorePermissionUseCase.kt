package kz.idl.domain.usecase.permission

import kz.idl.domain.repository.permission.PermissionRepository

class BulkRestorePermissionUseCase(
    private val permissionRepository: PermissionRepository
) {
    suspend operator fun invoke(ids: List<Long>): Int {
        if (ids.isEmpty()) {
            return 0
        }

        // Bulk restore permissions
        return permissionRepository.bulkRestore(ids)
    }
}
