package kz.idl.infrastructure.seeder.role_permission

import domain.enum.Environment
import infrastructure.utils.EnvironmentUtils
import io.ktor.server.application.*
import kz.idl.data.database.table.permission.PermissionTable
import kz.idl.data.database.table.role.RoleTable
import kz.idl.data.database.table.role_permission.RolePermissionTable
import kz.idl.domain.dto.permission.PermissionDto
import kz.idl.domain.dto.role.RoleDto
import kz.idl.domain.dto.role_permission.RolePermissionCDTO
import kz.idl.domain.mapper.toPermissionDto
import kz.idl.domain.mapper.toRoleDto
import kz.idl.domain.mapper.toRolePermissionDto
import kz.idl.domain.repository.permission.PermissionRepository
import kz.idl.domain.repository.role.RoleRepository
import kz.idl.domain.repository.role_permission.RolePermissionRepository
import kz.idl.infrastructure.filter.role.RoleFilter
import kz.idl.infrastructure.filter.role_permission.RolePermissionFilter
import kz.idl.infrastructure.seeder.BaseSeeder
import org.slf4j.LoggerFactory

class RolePermissionSeeder(
    private val rolePermissionRepository: RolePermissionRepository,
    private val roleRepository: RoleRepository,
    private val permissionRepository: PermissionRepository,
    private val applicationEnvironment: ApplicationEnvironment
) : BaseSeeder<RolePermissionTable, RolePermissionCDTO>() {
    private val logger = LoggerFactory.getLogger(RolePermissionSeeder::class.java)
    private val env = EnvironmentUtils.getEnvironmentOrDefault(applicationEnvironment)

    override suspend fun seed() {
        logger.info("Starting role-permission seeding for environment: ${env.value}")

        // Find admin role
        val adminRole = findAdminRole()
        if (adminRole == null) {
            logger.warn("Admin role not found, skipping role-permission seeding")
            return
        }

        logger.info("Found admin role with ID: ${adminRole.id}")

        // Get all permissions
        val allPermissions = permissionRepository.all(showDeleted = false) { it.toPermissionDto() }
        if (allPermissions.isEmpty()) {
            logger.warn("No permissions found, skipping role-permission seeding")
            return
        }

        logger.info("Found ${allPermissions.size} permissions")

        // Get existing role-permission relations for admin
        val existingRelations = getExistingRelationsForRole(adminRole.id)
        val existingPermissionIds = existingRelations.map { it.permissionId }.toSet()

        logger.info("Admin role already has ${existingPermissionIds.size} permissions assigned")

        // Filter out permissions that are already assigned
        val permissionsToAssign = allPermissions.filter { it.id !in existingPermissionIds }

        if (permissionsToAssign.isEmpty()) {
            logger.info("All permissions already assigned to admin role")
            return
        }

        logger.info("Assigning ${permissionsToAssign.size} new permissions to admin role")

        // Create role-permission relations
        val data = permissionsToAssign.map { permission ->
            RolePermissionCDTO(
                roleId = adminRole.id,
                permissionId = permission.id
            )
        }

        rolePermissionRepository.bulkCreate(
            items = data,
            insertBlock = { dto -> dto.insertBlock(RolePermissionTable, this) },
            mapper = { it.toRolePermissionDto() }
        )

        logger.info("Successfully assigned ${permissionsToAssign.size} permissions to admin role")
    }

    override suspend fun getDevData(): List<RolePermissionCDTO> {
        // Not used - we populate dynamically based on existing roles and permissions
        return emptyList()
    }

    override suspend fun getStageData(): List<RolePermissionCDTO> {
        // Not used - we populate dynamically based on existing roles and permissions
        return emptyList()
    }

    override suspend fun getProdData(): List<RolePermissionCDTO> {
        // Not used - we populate dynamically based on existing roles and permissions
        return emptyList()
    }

    private suspend fun findAdminRole(): RoleDto? {
        val filter = RoleFilter.byValue("admin", RoleTable)
        return roleRepository.findOneByFilter(filter) { it.toRoleDto() }
    }

    private suspend fun getExistingRelationsForRole(roleId: Long): List<kz.idl.domain.dto.role_permission.RolePermissionDTO> {
        val filter = RolePermissionFilter(
            table = RolePermissionTable,
            roleId = roleId
        )
        return rolePermissionRepository.findAllWithFilter(filter) { it.toRolePermissionDto() }
    }
}
