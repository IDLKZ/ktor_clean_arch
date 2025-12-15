package kz.idl.domain.usecase.role

import jakarta.validation.Validator
import kz.idl.data.database.table.role.RoleTable
import kz.idl.domain.dto.role.RoleDto
import kz.idl.domain.dto.role.RoleUpdateRequest
import kz.idl.domain.exception.ValidationException
import kz.idl.domain.localization.LocalizedMessage
import kz.idl.domain.mapper.toRoleDto
import kz.idl.domain.repository.role.RoleRepository
import org.jetbrains.exposed.v1.core.statements.UpdateStatement

class BulkUpdateRoleUseCase(
    private val roleRepository: RoleRepository,
    private val validator: Validator
) {
    suspend operator fun invoke(requests: List<RoleUpdateRequest>): List<RoleDto> {
        // Validate all DTOs
        requests.forEach { request ->
            val violations = validator.validate(request.data)
            if (violations.isNotEmpty()) {
                val errors = violations.associate {
                    it.propertyPath.toString() to LocalizedMessage(it.message)
                }
                throw ValidationException("validation.failed", errors)
            }
        }

        // Create update blocks for each request
        val updates = requests.map { request ->
            request.id to { it: UpdateStatement ->
                request.data.updateBlock(RoleTable, it)
            }
        }

        // Bulk update roles
        return roleRepository.bulkUpdate(
            updates = updates,
            mapper = { it.toRoleDto() }
        )
    }
}
