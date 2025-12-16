package kz.idl.infrastructure.seeder.role

import domain.enum.Environment
import infrastructure.utils.EnvironmentUtils
import io.ktor.server.application.*
import kz.idl.data.database.table.role.RoleTable
import kz.idl.domain.dto.role.RoleCreateDTO
import kz.idl.domain.mapper.toRoleDto
import kz.idl.domain.repository.role.RoleRepository
import kz.idl.infrastructure.filter.role.RoleFilter
import kz.idl.infrastructure.seeder.BaseSeeder
import org.slf4j.LoggerFactory

class RoleSeeder(
    private val roleRepository: RoleRepository,
    private val applicationEnvironment: ApplicationEnvironment
) : BaseSeeder<RoleTable, RoleCreateDTO>() {
    private val logger = LoggerFactory.getLogger(RoleSeeder::class.java)
    private val env = EnvironmentUtils.getEnvironmentOrDefault(applicationEnvironment)

    override suspend fun seed() {
        logger.info("Starting role seeding for environment: ${env.value}")

        val data = when (env) {
            Environment.DEV -> getDevData()
            Environment.STAGE -> getStageData()
            Environment.PROD -> getProdData()
        }

        // If no roles exist at all, insert all
        if (shouldSeed()) {
            logger.info("No roles found in database, creating all ${data.size} roles")
            roleRepository.bulkCreate(
                items = data,
                insertBlock = { dto -> dto.insertBlock(RoleTable, this) },
                mapper = { it.toRoleDto() }
            )
            logger.info("Successfully seeded ${data.size} roles for ${env.value} environment")
            return
        }

        // Get existing roles
        val existingRoles = roleRepository.all(showDeleted = false) { it.toRoleDto() }
        val existingValues = existingRoles.map { it.value }.toSet()

        logger.info("Found ${existingRoles.size} existing roles in database")

        // Filter out roles that already exist
        val rolesToCreate = data.filter { it.value !in existingValues }

        if (rolesToCreate.isEmpty()) {
            logger.info("All roles already exist, skipping seeding")
            return
        }

        logger.info("Creating ${rolesToCreate.size} new roles")

        roleRepository.bulkCreate(
            items = rolesToCreate,
            insertBlock = { dto -> dto.insertBlock(RoleTable, this) },
            mapper = { it.toRoleDto() }
        )

        logger.info("Successfully seeded ${rolesToCreate.size} roles for ${env.value} environment")
    }

    override suspend fun getDevData(): List<RoleCreateDTO> {
        return listOf(
            RoleCreateDTO(
                value = "admin",
                titleRu = "Администратор",
                titleKk = "Әкімші",
                titleEn = "Administrator",
                descriptionRu = "Полный доступ к системе",
                descriptionKk = "Жүйеге толық қол жеткізу",
                descriptionEn = "Full system access",
                system = true,
                administrative = true
            ),
            RoleCreateDTO(
                value = "user",
                titleRu = "Пользователь",
                titleKk = "Пайдаланушы",
                titleEn = "User",
                descriptionRu = "Обычный пользователь",
                descriptionKk = "Қарапайым пайдаланушы",
                descriptionEn = "Regular user",
                system = true,
                administrative = false
            ),
            RoleCreateDTO(
                value = "moderator",
                titleRu = "Модератор",
                titleKk = "Модератор",
                titleEn = "Moderator",
                descriptionRu = "Модератор контента",
                descriptionKk = "Мазмұн модераторы",
                descriptionEn = "Content moderator",
                system = false,
                administrative = false
            ),
            RoleCreateDTO(
                value = "guest",
                titleRu = "Гость",
                titleKk = "Қонақ",
                titleEn = "Guest",
                descriptionRu = "Гостевой доступ",
                descriptionKk = "Қонақ қол жеткізуі",
                descriptionEn = "Guest access",
                system = true,
                administrative = false
            )
        )
    }

    override suspend fun getStageData(): List<RoleCreateDTO> {
        return listOf(
            RoleCreateDTO(
                value = "admin",
                titleRu = "Администратор",
                titleKk = "Әкімші",
                titleEn = "Administrator",
                descriptionRu = "Полный доступ к системе",
                descriptionKk = "Жүйеге толық қол жеткізу",
                descriptionEn = "Full system access",
                system = true,
                administrative = true
            ),
            RoleCreateDTO(
                value = "user",
                titleRu = "Пользователь",
                titleKk = "Пайдаланушы",
                titleEn = "User",
                descriptionRu = "Обычный пользователь",
                descriptionKk = "Қарапайым пайдаланушы",
                descriptionEn = "Regular user",
                system = true,
                administrative = false
            )
        )
    }

    override suspend fun getProdData(): List<RoleCreateDTO> {
        return listOf(
            RoleCreateDTO(
                value = "admin",
                titleRu = "Администратор",
                titleKk = "Әкімші",
                titleEn = "Administrator",
                descriptionRu = "Администратор системы",
                descriptionKk = "Жүйе әкімшісі",
                descriptionEn = "System administrator",
                system = true,
                administrative = true
            ),
            RoleCreateDTO(
                value = "user",
                titleRu = "Пользователь",
                titleKk = "Пайдаланушы",
                titleEn = "User",
                descriptionRu = "Пользователь системы",
                descriptionKk = "Жүйе пайдаланушысы",
                descriptionEn = "System user",
                system = true,
                administrative = false
            )
        )
    }

    private suspend fun shouldSeed(): Boolean {
        val roleFilter = RoleFilter(
            showDeleted = null,
            table = RoleTable
        )
        return roleRepository.countWithFilter(roleFilter) == 0L
    }
}