package kz.idl.domain.usecase.permission

import jakarta.validation.Validator
import kz.idl.data.database.table.permission.PermissionTable
import kz.idl.domain.dto.permission.PermissionCreateDTO
import kz.idl.domain.dto.permission.PermissionDto
import kz.idl.domain.exception.ValidationException
import kz.idl.domain.localization.LocalizedMessage
import kz.idl.domain.mapper.toPermissionDto
import kz.idl.domain.repository.permission.PermissionRepository

class BulkCreatePermissionUseCase(
    private val permissionRepository: PermissionRepository,
    private val validator: Validator
) {
    suspend operator fun invoke(dtos: List<PermissionCreateDTO>): List<PermissionDto> {
        // Validate all DTOs
        dtos.forEach { dto ->
            val violations = validator.validate(dto)
            if (violations.isNotEmpty()) {
                val errors = violations.associate {
                    it.propertyPath.toString() to LocalizedMessage(it.message)
                }
                throw ValidationException("validation.failed", errors)
            }
        }

        // Bulk create permissions using batchInsert
        return permissionRepository.bulkCreate(
            items = dtos,
            insertBlock = { dto -> dto.insertBlock(PermissionTable, this) },
            mapper = { it.toPermissionDto() }
        )
    }
}
