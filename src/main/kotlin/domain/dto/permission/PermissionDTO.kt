package kz.idl.domain.dto.permission

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kz.idl.data.database.table.permission.PermissionTable
import org.jetbrains.exposed.v1.core.statements.BatchInsertStatement

@Serializable
data class PermissionDto(
    val id: Long,
    val titleRu: String,
    val titleKk: String?,
    val titleEn: String?,
    val descriptionRu: String?,
    val descriptionKk: String?,
    val value: String,
    val system: Boolean,
    val administrative: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

@Serializable
data class PermissionWithPermissionsDto(
    val id: Long,
    val titleRu: String,
    val titleKk: String?,
    val titleEn: String?,
    val value: String,
    val system: Boolean,
    val administrative: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

@Serializable
data class PermissionShortDto(
    val id: Long,
    val titleRu: String,
    val value: String
)

@Serializable
data class PermissionUpdateRequest(
    val id: Long,
    val data: PermissionCreateDTO
)

@Serializable
data class PermissionCreateDTO(
    @field:jakarta.validation.constraints.NotBlank
    @field:jakarta.validation.constraints.Size(max = 280)
    val value: String,

    @field:jakarta.validation.constraints.NotBlank
    @field:jakarta.validation.constraints.Size(max = 255)
    val titleRu: String,

    @field:jakarta.validation.constraints.Size(max = 255)
    val titleKk: String? = null,

    @field:jakarta.validation.constraints.Size(max = 255)
    val titleEn: String? = null,

    val descriptionRu: String? = null,
    val descriptionKk: String? = null,
    val descriptionEn: String? = null,

    val system: Boolean = false,
    val administrative: Boolean = false
): kz.idl.domain.dto.BaseUpdateDTO<PermissionTable>,
   kz.idl.domain.dto.BaseCreateDTO<PermissionTable>,
   kz.idl.domain.dto.BaseOneCreateDTO<PermissionTable> {

    override fun insertBlock(table: PermissionTable, stmt: BatchInsertStatement) {
        stmt[table.value] = value
        stmt[table.titleRu] = titleRu
        stmt[table.titleKk] = titleKk
        stmt[table.titleEn] = titleEn
        stmt[table.descriptionRu] = descriptionRu
        stmt[table.descriptionKk] = descriptionKk
        stmt[table.descriptionEn] = descriptionEn
        stmt[table.system] = system
        stmt[table.administrative] = administrative
    }

    override fun updateBlock(table: PermissionTable, stmt: org.jetbrains.exposed.v1.core.statements.UpdateStatement) {
        stmt[table.value] = value
        stmt[table.titleRu] = titleRu
        stmt[table.titleKk] = titleKk
        stmt[table.titleEn] = titleEn
        stmt[table.descriptionRu] = descriptionRu
        stmt[table.descriptionKk] = descriptionKk
        stmt[table.descriptionEn] = descriptionEn
        stmt[table.system] = system
        stmt[table.administrative] = administrative
    }

    override fun createEntity(table:PermissionTable): org.jetbrains.exposed.v1.core.statements.InsertStatement<Number>.() -> Unit = {
        this[PermissionTable.value] = value
        this[PermissionTable.titleRu] = titleRu
        this[PermissionTable.titleKk] = titleKk
        this[PermissionTable.titleEn] = titleEn
        this[PermissionTable.descriptionRu] = descriptionRu
        this[PermissionTable.descriptionKk] = descriptionKk
        this[PermissionTable.descriptionEn] = descriptionEn
        this[PermissionTable.system] = system
        this[PermissionTable.administrative] = administrative
    }
}