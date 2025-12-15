package kz.idl.domain.usecase.role

import kz.idl.domain.exception.NotFoundException
import kz.idl.domain.mapper.toRoleDto
import kz.idl.domain.repository.role.RoleRepository

class RestoreRoleUseCase(
    private val roleRepository: RoleRepository
) {
    suspend operator fun invoke(id: Long): Boolean {
        // Check if role exists (including deleted)
        val existingRole = roleRepository.findByLongId(
            id = id,
            showDeleted = true,
            includeJoin = false,
            mapper = { it.toRoleDto() }
        ) ?: throw NotFoundException("role.not_found")

        // Restore role
        return roleRepository.restore(id)
    }
}
