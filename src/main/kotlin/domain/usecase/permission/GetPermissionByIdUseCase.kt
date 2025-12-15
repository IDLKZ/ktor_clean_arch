package kz.idl.domain.usecase.permission

import kz.idl.domain.dto.permission.PermissionDto
import kz.idl.domain.exception.NotFoundException
import kz.idl.domain.mapper.toPermissionDto
import kz.idl.domain.repository.permission.PermissionRepository

class GetPermissionByIdUseCase(
    private val permissionRepository: PermissionRepository
) {
    suspend operator fun invoke(id: Long, showDeleted: Boolean? = false, includeJoin: Boolean? = false): PermissionDto {
        return permissionRepository.findByLongId(id, showDeleted = showDeleted, includeJoin = includeJoin) {
            it.toPermissionDto()
        } ?: throw NotFoundException("permission.not_found")
    }
}
