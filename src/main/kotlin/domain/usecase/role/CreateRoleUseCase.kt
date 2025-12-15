package kz.idl.domain.usecase.role

import jakarta.validation.Validator
import kz.idl.data.database.table.role.RoleTable
import kz.idl.domain.dto.role.RoleCreateDTO
import kz.idl.domain.dto.role.RoleDto
import kz.idl.domain.exception.ConflictException
import kz.idl.domain.exception.InternalServerException
import kz.idl.domain.exception.ValidationException
import kz.idl.domain.localization.LocalizedMessage
import kz.idl.domain.mapper.toRoleDto
import kz.idl.domain.repository.role.RoleRepository
import kz.idl.infrastructure.filter.role.RoleFilter

class CreateRoleUseCase(
    private val roleRepository: RoleRepository,
    private val validator: Validator
) {
    suspend operator fun invoke(dto: RoleCreateDTO): RoleDto {
        // Validate DTO
        val violations = validator.validate(dto)
        if (violations.isNotEmpty()) {
            val errors = violations.associate {
                it.propertyPath.toString() to LocalizedMessage(it.message)
            }
            throw ValidationException("validation.failed", errors)
        }

        // Check if role with this value already exists
        val existingRole = roleRepository.findOneByFilter(
            filter = RoleFilter.byValue(dto.value),
            mapper = { it.toRoleDto() }
        )

        if (existingRole != null) {
            throw ConflictException("role.value_already_exists", dto.value)
        }

        // Create role
        return roleRepository.create(
            insertBlock = dto.createEntity(RoleTable),
            mapper = { it.toRoleDto() }
        ) ?: throw InternalServerException("role.creation_failed")
    }
}
