package kz.idl.domain.usecase.role

import kz.idl.domain.repository.role.RoleRepository

class BulkDeleteRoleUseCase(
    private val roleRepository: RoleRepository
) {
    suspend operator fun invoke(ids: List<Long>, hardDelete: Boolean = false): Int {
        if (ids.isEmpty()) {
            return 0
        }

        // Bulk delete roles (soft delete by default)
        return roleRepository.bulkDelete(ids, hardDelete)
    }
}
