package kz.idl.infrastructure.seeder.permission

import domain.enum.Environment
import infrastructure.utils.EnvironmentUtils
import io.ktor.server.application.*
import kz.idl.data.database.table.permission.PermissionTable
import kz.idl.domain.dto.permission.PermissionCreateDTO
import kz.idl.domain.mapper.toPermissionDto
import kz.idl.domain.repository.permission.PermissionRepository
import kz.idl.infrastructure.filter.permission.PermissionFilter
import kz.idl.infrastructure.seeder.BaseSeeder
import org.slf4j.LoggerFactory

class PermissionSeeder(
    private val permissionRepository: PermissionRepository,
    private val applicationEnvironment: ApplicationEnvironment
) : BaseSeeder<PermissionTable, PermissionCreateDTO>() {
    private val logger = LoggerFactory.getLogger(PermissionSeeder::class.java)
    private val env = EnvironmentUtils.getEnvironmentOrDefault(applicationEnvironment)

    override suspend fun seed() {
        logger.info("Starting permission seeding for environment: ${env.value}")

        val data = when (env) {
            Environment.DEV -> getDevData()
            Environment.STAGE -> getStageData()
            Environment.PROD -> getProdData()
        }

        // If no permissions exist at all, insert all
        if (shouldSeed()) {
            logger.info("No permissions found in database, creating all ${data.size} permissions")
            permissionRepository.bulkCreate(
                items = data,
                insertBlock = { dto -> dto.insertBlock(PermissionTable, this) },
                mapper = { it.toPermissionDto() }
            )
            logger.info("Successfully seeded ${data.size} permissions for ${env.value} environment")
            return
        }

        // Get existing permissions
        val existingPermissions = permissionRepository.all(showDeleted = false) { it.toPermissionDto() }
        val existingValues = existingPermissions.map { it.value }.toSet()

        logger.info("Found ${existingPermissions.size} existing permissions in database")

        // Filter out permissions that already exist
        val permissionsToCreate = data.filter { it.value !in existingValues }

        if (permissionsToCreate.isEmpty()) {
            logger.info("All permissions already exist, skipping seeding")
            return
        }

        logger.info("Creating ${permissionsToCreate.size} new permissions")

        permissionRepository.bulkCreate(
            items = permissionsToCreate,
            insertBlock = { dto -> dto.insertBlock(PermissionTable, this) },
            mapper = { it.toPermissionDto() }
        )

        logger.info("Successfully seeded ${permissionsToCreate.size} permissions for ${env.value} environment")
    }

    override suspend fun getDevData(): List<PermissionCreateDTO> {
        return listOf(
            // Role permissions
            PermissionCreateDTO(
                value = "create-role",
                titleRu = "Создание ролей",
                titleKk = "Рөлдерді жасау",
                titleEn = "Create roles",
                descriptionRu = "Разрешение на создание новых ролей в системе",
                descriptionKk = "Жүйеде жаңа рөлдер жасауға рұқсат",
                descriptionEn = "Permission to create new roles in the system",
                system = true,
                administrative = true
            ),
            PermissionCreateDTO(
                value = "update-role",
                titleRu = "Редактирование ролей",
                titleKk = "Рөлдерді өңдеу",
                titleEn = "Update roles",
                descriptionRu = "Разрешение на изменение существующих ролей",
                descriptionKk = "Қолданыстағы рөлдерді өзгертуге рұқсат",
                descriptionEn = "Permission to modify existing roles",
                system = true,
                administrative = true
            ),
            PermissionCreateDTO(
                value = "delete-role",
                titleRu = "Удаление ролей",
                titleKk = "Рөлдерді жою",
                titleEn = "Delete roles",
                descriptionRu = "Разрешение на удаление ролей из системы",
                descriptionKk = "Жүйеден рөлдерді жоюға рұқсат",
                descriptionEn = "Permission to delete roles from the system",
                system = true,
                administrative = true
            ),
            PermissionCreateDTO(
                value = "manage-role",
                titleRu = "Управление ролями",
                titleKk = "Рөлдерді басқару",
                titleEn = "Manage roles",
                descriptionRu = "Полное управление ролями (создание, изменение, удаление)",
                descriptionKk = "Рөлдерді толық басқару (жасау, өзгерту, жою)",
                descriptionEn = "Full role management (create, update, delete)",
                system = true,
                administrative = true
            ),
            // Permission permissions
            PermissionCreateDTO(
                value = "create-permission",
                titleRu = "Создание разрешений",
                titleKk = "Рұқсаттарды жасау",
                titleEn = "Create permissions",
                descriptionRu = "Разрешение на создание новых разрешений в системе",
                descriptionKk = "Жүйеде жаңа рұқсаттар жасауға рұқсат",
                descriptionEn = "Permission to create new permissions in the system",
                system = true,
                administrative = true
            ),
            PermissionCreateDTO(
                value = "update-permission",
                titleRu = "Редактирование разрешений",
                titleKk = "Рұқсаттарды өңдеу",
                titleEn = "Update permissions",
                descriptionRu = "Разрешение на изменение существующих разрешений",
                descriptionKk = "Қолданыстағы рұқсаттарды өзгертуге рұқсат",
                descriptionEn = "Permission to modify existing permissions",
                system = true,
                administrative = true
            ),
            PermissionCreateDTO(
                value = "delete-permission",
                titleRu = "Удаление разрешений",
                titleKk = "Рұқсаттарды жою",
                titleEn = "Delete permissions",
                descriptionRu = "Разрешение на удаление разрешений из системы",
                descriptionKk = "Жүйеден рұқсаттарды жоюға рұқсат",
                descriptionEn = "Permission to delete permissions from the system",
                system = true,
                administrative = true
            ),
            PermissionCreateDTO(
                value = "manage-permission",
                titleRu = "Управление разрешениями",
                titleKk = "Рұқсаттарды басқару",
                titleEn = "Manage permissions",
                descriptionRu = "Полное управление разрешениями (создание, изменение, удаление)",
                descriptionKk = "Рұқсаттарды толық басқару (жасау, өзгерту, жою)",
                descriptionEn = "Full permission management (create, update, delete)",
                system = true,
                administrative = true
            )
        )
    }

    override suspend fun getStageData(): List<PermissionCreateDTO> {
        return listOf(
            // Role permissions
            PermissionCreateDTO(
                value = "create-role",
                titleRu = "Создание ролей",
                titleKk = "Рөлдерді жасау",
                titleEn = "Create roles",
                descriptionRu = "Разрешение на создание новых ролей",
                descriptionKk = "Жаңа рөлдер жасауға рұқсат",
                descriptionEn = "Permission to create new roles",
                system = true,
                administrative = true
            ),
            PermissionCreateDTO(
                value = "update-role",
                titleRu = "Редактирование ролей",
                titleKk = "Рөлдерді өңдеу",
                titleEn = "Update roles",
                descriptionRu = "Разрешение на изменение ролей",
                descriptionKk = "Рөлдерді өзгертуге рұқсат",
                descriptionEn = "Permission to modify roles",
                system = true,
                administrative = true
            ),
            PermissionCreateDTO(
                value = "delete-role",
                titleRu = "Удаление ролей",
                titleKk = "Рөлдерді жою",
                titleEn = "Delete roles",
                descriptionRu = "Разрешение на удаление ролей",
                descriptionKk = "Рөлдерді жоюға рұқсат",
                descriptionEn = "Permission to delete roles",
                system = true,
                administrative = true
            ),
            PermissionCreateDTO(
                value = "manage-role",
                titleRu = "Управление ролями",
                titleKk = "Рөлдерді басқару",
                titleEn = "Manage roles",
                descriptionRu = "Полное управление ролями",
                descriptionKk = "Рөлдерді толық басқару",
                descriptionEn = "Full role management",
                system = true,
                administrative = true
            ),
            // Permission permissions
            PermissionCreateDTO(
                value = "create-permission",
                titleRu = "Создание разрешений",
                titleKk = "Рұқсаттарды жасау",
                titleEn = "Create permissions",
                descriptionRu = "Разрешение на создание новых разрешений",
                descriptionKk = "Жаңа рұқсаттар жасауға рұқсат",
                descriptionEn = "Permission to create new permissions",
                system = true,
                administrative = true
            ),
            PermissionCreateDTO(
                value = "update-permission",
                titleRu = "Редактирование разрешений",
                titleKk = "Рұқсаттарды өңдеу",
                titleEn = "Update permissions",
                descriptionRu = "Разрешение на изменение разрешений",
                descriptionKk = "Рұқсаттарды өзгертуге рұқсат",
                descriptionEn = "Permission to modify permissions",
                system = true,
                administrative = true
            ),
            PermissionCreateDTO(
                value = "delete-permission",
                titleRu = "Удаление разрешений",
                titleKk = "Рұқсаттарды жою",
                titleEn = "Delete permissions",
                descriptionRu = "Разрешение на удаление разрешений",
                descriptionKk = "Рұқсаттарды жоюға рұқсат",
                descriptionEn = "Permission to delete permissions",
                system = true,
                administrative = true
            ),
            PermissionCreateDTO(
                value = "manage-permission",
                titleRu = "Управление разрешениями",
                titleKk = "Рұқсаттарды басқару",
                titleEn = "Manage permissions",
                descriptionRu = "Полное управление разрешениями",
                descriptionKk = "Рұқсаттарды толық басқару",
                descriptionEn = "Full permission management",
                system = true,
                administrative = true
            )
        )
    }

    override suspend fun getProdData(): List<PermissionCreateDTO> {
        return listOf(
            // Role permissions
            PermissionCreateDTO(
                value = "create-role",
                titleRu = "Создание ролей",
                titleKk = "Рөлдерді жасау",
                titleEn = "Create roles",
                system = true,
                administrative = true
            ),
            PermissionCreateDTO(
                value = "update-role",
                titleRu = "Редактирование ролей",
                titleKk = "Рөлдерді өңдеу",
                titleEn = "Update roles",
                system = true,
                administrative = true
            ),
            PermissionCreateDTO(
                value = "delete-role",
                titleRu = "Удаление ролей",
                titleKk = "Рөлдерді жою",
                titleEn = "Delete roles",
                system = true,
                administrative = true
            ),
            PermissionCreateDTO(
                value = "manage-role",
                titleRu = "Управление ролями",
                titleKk = "Рөлдерді басқару",
                titleEn = "Manage roles",
                system = true,
                administrative = true
            ),
            // Permission permissions
            PermissionCreateDTO(
                value = "create-permission",
                titleRu = "Создание разрешений",
                titleKk = "Рұқсаттарды жасау",
                titleEn = "Create permissions",
                system = true,
                administrative = true
            ),
            PermissionCreateDTO(
                value = "update-permission",
                titleRu = "Редактирование разрешений",
                titleKk = "Рұқсаттарды өңдеу",
                titleEn = "Update permissions",
                system = true,
                administrative = true
            ),
            PermissionCreateDTO(
                value = "delete-permission",
                titleRu = "Удаление разрешений",
                titleKk = "Рұқсаттарды жою",
                titleEn = "Delete permissions",
                system = true,
                administrative = true
            ),
            PermissionCreateDTO(
                value = "manage-permission",
                titleRu = "Управление разрешениями",
                titleKk = "Рұқсаттарды басқару",
                titleEn = "Manage permissions",
                system = true,
                administrative = true
            )
        )
    }

    private suspend fun shouldSeed(): Boolean {
        val permissionFilter = PermissionFilter(
            showDeleted = null,
            table = PermissionTable
        )
        return permissionRepository.countWithFilter(permissionFilter) == 0L
    }
}
