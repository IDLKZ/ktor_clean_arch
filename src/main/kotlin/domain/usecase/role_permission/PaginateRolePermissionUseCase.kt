package kz.idl.domain.usecase.role_permission

import kz.idl.domain.dto.role_permission.RolePermissionDTO
import kz.idl.domain.dto.role_permission.RolePermissionWithRelationsDto
import kz.idl.domain.entity.PaginationMeta
import kz.idl.domain.mapper.toRolePermissionDto
import kz.idl.domain.mapper.toRolePermissionWithRelationsDto
import kz.idl.domain.repository.role_permission.RolePermissionRepository
import kz.idl.infrastructure.filter.role_permission.RolePermissionPaginationFilter

class PaginateRolePermissionUseCase(
    private val rolePermissionRepository: RolePermissionRepository
) {
    suspend operator fun invoke(filter: RolePermissionPaginationFilter): PaginationMeta<RolePermissionWithRelationsDto> {
        return rolePermissionRepository.paginate(filter=filter, mapper = {it.toRolePermissionWithRelationsDto()});
    }
}
