package kz.idl.domain.usecase.permission

import kz.idl.domain.exception.NotFoundException
import kz.idl.domain.mapper.toPermissionDto
import kz.idl.domain.repository.permission.PermissionRepository

class RestorePermissionUseCase(
    private val permissionRepository: PermissionRepository
) {
    suspend operator fun invoke(id: Long): Boolean {
        // Check if permission exists (including deleted)
        val existingPermission = permissionRepository.findByLongId(
            id = id,
            showDeleted = true,
            includeJoin = false,
            mapper = { it.toPermissionDto() }
        ) ?: throw NotFoundException("permission.not_found")

        // Restore permission
        return permissionRepository.restore(id)
    }
}
