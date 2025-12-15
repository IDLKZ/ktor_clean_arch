package kz.idl.domain.usecase.permission

import jakarta.validation.Validator
import kz.idl.data.database.table.permission.PermissionTable
import kz.idl.domain.dto.permission.PermissionCreateDTO
import kz.idl.domain.dto.permission.PermissionDto
import kz.idl.domain.exception.ConflictException
import kz.idl.domain.exception.InternalServerException
import kz.idl.domain.exception.ValidationException
import kz.idl.domain.localization.LocalizedMessage
import kz.idl.domain.mapper.toPermissionDto
import kz.idl.domain.repository.permission.PermissionRepository
import kz.idl.infrastructure.filter.permission.PermissionFilter

class CreatePermissionUseCase(
    private val permissionRepository: PermissionRepository,
    private val validator: Validator
) {
    suspend operator fun invoke(dto: PermissionCreateDTO): PermissionDto {
        // Validate DTO
        val violations = validator.validate(dto)
        if (violations.isNotEmpty()) {
            val errors = violations.associate {
                it.propertyPath.toString() to LocalizedMessage(it.message)
            }
            throw ValidationException("validation.failed", errors)
        }

        // Check if permission with this value already exists
        val existingPermission = permissionRepository.findOneByFilter(
            filter = PermissionFilter.byValue(dto.value),
            mapper = { it.toPermissionDto() }
        )

        if (existingPermission != null) {
            throw ConflictException("permission.value_already_exists", dto.value)
        }

        // Create permission
        return permissionRepository.create(
            insertBlock = dto.createEntity(PermissionTable),
            mapper = { it.toPermissionDto() }
        ) ?: throw InternalServerException("permission.creation_failed")
    }
}
