package kz.idl.domain.usecase.role

import kz.idl.domain.repository.role.RoleRepository

class BulkRestoreRoleUseCase(
    private val roleRepository: RoleRepository
) {
    suspend operator fun invoke(ids: List<Long>): Int {
        if (ids.isEmpty()) {
            return 0
        }

        // Bulk restore roles
        return roleRepository.bulkRestore(ids)
    }
}
