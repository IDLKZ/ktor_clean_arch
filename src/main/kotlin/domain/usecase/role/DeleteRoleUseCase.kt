package kz.idl.domain.usecase.role

import kz.idl.domain.exception.NotFoundException
import kz.idl.domain.mapper.toRoleDto
import kz.idl.domain.repository.role.RoleRepository

class DeleteRoleUseCase(
    private val roleRepository: RoleRepository
) {
    suspend operator fun invoke(id: Long, hardDelete: Boolean = false): Boolean {
        // Check if role exists
        val existingRole = roleRepository.findByLongId(
            id = id,
            showDeleted = null,
            includeJoin = false,
            mapper = { it.toRoleDto() }
        ) ?: throw NotFoundException("role.not_found")

        // Delete role (soft delete by default)
        return roleRepository.delete(id, hardDelete)
    }
}
