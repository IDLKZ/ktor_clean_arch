package kz.idl.domain.usecase.role_permission

import kz.idl.domain.dto.role_permission.RolePermissionWithRelationsDto
import kz.idl.domain.exception.NotFoundException
import kz.idl.domain.mapper.toRolePermissionWithRelationsDto
import kz.idl.domain.repository.role_permission.RolePermissionRepository

class GetRolePermissionByIdUseCase(
    private val rolePermissionRepository: RolePermissionRepository
) {
    suspend operator fun invoke(id: Long, includeJoin: Boolean? = false): RolePermissionWithRelationsDto {
        return rolePermissionRepository.findByLongId(
            id = id,
            showDeleted = false,
            includeJoin = includeJoin ?: false
        ) { it.toRolePermissionWithRelationsDto() }
            ?: throw NotFoundException("role_permission.not_found")
    }
}
