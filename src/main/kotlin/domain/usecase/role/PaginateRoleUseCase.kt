package kz.idl.domain.usecase.role
import kz.idl.domain.dto.role.RoleWithPermissionsDto
import kz.idl.domain.entity.PaginationMeta
import kz.idl.domain.mapper.toRoleWithPermissionDto
import kz.idl.domain.repository.role.RoleRepository
import kz.idl.infrastructure.filter.role.RolePaginationFilter

class PaginateRoleUseCase (
    private val roleRepository: RoleRepository
) {

    suspend operator fun invoke(filter: RolePaginationFilter): PaginationMeta<RoleWithPermissionsDto> {
        val roles = roleRepository.paginate(filter, mapper = { it.toRoleWithPermissionDto() })
        return roles
    }
}