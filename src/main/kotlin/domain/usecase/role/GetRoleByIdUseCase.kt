package kz.idl.domain.usecase.role

import kz.idl.domain.dto.role.RoleWithPermissionsDto
import kz.idl.domain.exception.NotFoundException
import kz.idl.domain.mapper.toRoleWithPermissionDto
import kz.idl.domain.repository.role.RoleRepository

class GetRoleByIdUseCase(
    private val roleRepository: RoleRepository
) {
    suspend operator fun invoke(id: Long, showDeleted:Boolean? = true, includeJoin: Boolean? = false): RoleWithPermissionsDto {
        return roleRepository.findByLongId(id, showDeleted = showDeleted, includeJoin = includeJoin) {
            it.toRoleWithPermissionDto()
        } ?: throw NotFoundException("role.not_found")
    }
}