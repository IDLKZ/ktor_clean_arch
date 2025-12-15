package kz.idl.domain.usecase.permission

import kz.idl.domain.exception.NotFoundException
import kz.idl.domain.mapper.toPermissionDto
import kz.idl.domain.repository.permission.PermissionRepository

class DeletePermissionUseCase(
    private val permissionRepository: PermissionRepository
) {
    suspend operator fun invoke(id: Long, hardDelete: Boolean = false): Boolean {
        // Check if permission exists
        val existingPermission = permissionRepository.findByLongId(
            id = id,
            showDeleted = null,
            includeJoin = false,
            mapper = { it.toPermissionDto() }
        ) ?: throw NotFoundException("permission.not_found")

        // Delete permission (soft delete by default)
        return permissionRepository.delete(id, hardDelete)
    }
}
