package kz.idl.domain.usecase.role

import jakarta.validation.Validator
import kz.idl.data.database.table.role.RoleTable
import kz.idl.domain.dto.role.RoleCreateDTO
import kz.idl.domain.dto.role.RoleDto
import kz.idl.domain.exception.ValidationException
import kz.idl.domain.localization.LocalizedMessage
import kz.idl.domain.mapper.toRoleDto
import kz.idl.domain.repository.role.RoleRepository

class BulkCreateRoleUseCase(
    private val roleRepository: RoleRepository,
    private val validator: Validator
) {
    suspend operator fun invoke(dtos: List<RoleCreateDTO>): List<RoleDto> {
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

        // Bulk create roles using batchInsert
        return roleRepository.bulkCreate(
            items = dtos,
            insertBlock = { dto -> dto.insertBlock(RoleTable, this) },
            mapper = { it.toRoleDto() }
        )
    }
}
