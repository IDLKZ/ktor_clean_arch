package kz.idl.domain.dto.role
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kz.idl.data.database.table.role.RoleTable
import kz.idl.domain.dto.BaseCreateDTO
import kz.idl.domain.dto.BaseOneCreateDTO
import kz.idl.domain.dto.BaseUpdateDTO
import kz.idl.domain.dto.permission.PermissionDto
import org.jetbrains.exposed.v1.core.statements.BatchInsertStatement
import org.jetbrains.exposed.v1.core.statements.InsertStatement
import org.jetbrains.exposed.v1.core.statements.UpdateStatement

@Serializable
data class RoleDto(
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
data class RoleWithPermissionsDto(
    val id: Long,
    val titleRu: String,
    val titleKk: String?,
    val titleEn: String?,
    val value: String,
    val system: Boolean,
    val administrative: Boolean,
    val permissions: List<PermissionDto>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

@Serializable
data class RoleShortDto(
    val id: Long,
    val titleRu: String,
    val value: String
)

@Serializable
data class RoleUpdateRequest(
    val id: Long,
    val data: RoleCreateDTO
)


@Serializable
data class RoleCreateDTO(

    @field:NotBlank
    @field:Size(max = 280)
    val value: String,

    @field:NotBlank
    @field:Size(max = 255)
    val titleRu: String,

    @field:Size(max = 255)
    val titleKk: String? = null,

    @field:Size(max = 255)
    val titleEn: String? = null,

    val descriptionRu: String? = null,

    val descriptionKk: String? = null,

    val descriptionEn: String? = null,

    val system: Boolean = false,
    val administrative: Boolean = false

): BaseUpdateDTO<RoleTable>, BaseCreateDTO<RoleTable>, BaseOneCreateDTO<RoleTable> {
//    @AssertTrue(message = "Role cannot be both system and administrative")
//    fun isBusinessRuleValid(): Boolean =
//        !(system && administrative)

    override fun insertBlock(table: RoleTable, stmt: BatchInsertStatement) {
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

    override fun updateBlock(table: RoleTable, stmt: UpdateStatement) {
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

    override fun createEntity(table: RoleTable): InsertStatement<Number>.() -> Unit = {
        this[RoleTable.value] = value
        this[RoleTable.titleRu] = titleRu
        this[RoleTable.titleKk] = titleKk
        this[RoleTable.titleEn] = titleEn
        this[RoleTable.descriptionRu] = descriptionRu
        this[RoleTable.descriptionKk] = descriptionKk
        this[RoleTable.descriptionEn] = descriptionEn
        this[RoleTable.system] = system
        this[RoleTable.administrative] = administrative
    }
}

