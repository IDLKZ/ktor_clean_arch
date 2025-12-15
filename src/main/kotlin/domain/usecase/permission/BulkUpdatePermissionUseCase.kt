package kz.idl.domain.usecase.permission

import jakarta.validation.Validator
import kz.idl.data.database.table.permission.PermissionTable
import kz.idl.domain.dto.permission.PermissionDto
import kz.idl.domain.dto.permission.PermissionUpdateRequest
import kz.idl.domain.exception.ValidationException
import kz.idl.domain.localization.LocalizedMessage
import kz.idl.domain.mapper.toPermissionDto
import kz.idl.domain.repository.permission.PermissionRepository

class BulkUpdatePermissionUseCase(
    private val permissionRepository: PermissionRepository,
    private val validator: Validator
) {
    suspend operator fun invoke(requests: List<PermissionUpdateRequest>): List<PermissionDto> {
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
            request.id to { it: org.jetbrains.exposed.v1.core.statements.UpdateStatement ->
                request.data.updateBlock(PermissionTable, it)
            }
        }

        // Bulk update permissions
        return permissionRepository.bulkUpdate(
            updates = updates,
            mapper = { it.toPermissionDto() }
        )
    }
}
