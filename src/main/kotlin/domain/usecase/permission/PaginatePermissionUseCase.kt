package kz.idl.domain.usecase.permission

import kz.idl.domain.dto.permission.PermissionDto
import kz.idl.domain.entity.PaginationMeta
import kz.idl.domain.mapper.toPermissionDto
import kz.idl.domain.repository.permission.PermissionRepository
import kz.idl.infrastructure.filter.permission.PermissionPaginationFilter

class PaginatePermissionUseCase(
    private val permissionRepository: PermissionRepository
) {
    suspend operator fun invoke(filter: PermissionPaginationFilter): PaginationMeta<PermissionDto> {
        return permissionRepository.paginate(filter) { it.toPermissionDto() }
    }
}
