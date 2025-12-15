package kz.idl.domain.mapper

import kz.idl.data.database.table.permission.PermissionTable
import kz.idl.data.database.table.role.RoleTable
import kz.idl.domain.dto.permission.PermissionDto
import kz.idl.domain.dto.role.RoleDto
import kz.idl.domain.dto.role.RoleShortDto
import kz.idl.domain.dto.role.RoleWithPermissionsDto
import org.jetbrains.exposed.v1.core.ResultRow
import kotlin.collections.mapNotNull


fun ResultRow.toRoleDto() = RoleDto(
    id = this[RoleTable.id].value,
    titleRu = this[RoleTable.titleRu],
    titleKk = this[RoleTable.titleKk],
    titleEn = this[RoleTable.titleEn],
    descriptionRu = this[RoleTable.descriptionRu],
    descriptionKk = this[RoleTable.descriptionKk],
    value = this[RoleTable.value],
    system = this[RoleTable.system],
    administrative = this[RoleTable.administrative],
    createdAt = this[RoleTable.createdAt],
    updatedAt = this[RoleTable.updatedAt]
)

fun ResultRow.toRoleWithPermissionDto() = RoleWithPermissionsDto(
    id = this[RoleTable.id].value,
    titleRu = this[RoleTable.titleRu],
    titleKk = this[RoleTable.titleKk],
    titleEn = this[RoleTable.titleEn],
    value = this[RoleTable.value],
    system = this[RoleTable.system],
    administrative = this[RoleTable.administrative],
    permissions =this.getOrNull(PermissionTable.id)?.let {
        listOf(this.toPermissionDto())
    } ?: emptyList(),
    createdAt = this[RoleTable.createdAt],
    updatedAt = this[RoleTable.updatedAt]
)

fun ResultRow.toRoleShortDto() = RoleShortDto(
    id = this[RoleTable.id].value,
    titleRu = this[RoleTable.titleRu],
    value = this[RoleTable.value]
)

fun ResultRow.toPermissionDto() = PermissionDto(
    id = this[PermissionTable.id].value,
    titleRu = this[PermissionTable.titleRu],
    titleKk = this[PermissionTable.titleKk],
    titleEn = this[PermissionTable.titleEn],
    descriptionRu = this[PermissionTable.descriptionRu],
    descriptionKk = this[PermissionTable.descriptionKk],
    value = this[PermissionTable.value],
    system = this[PermissionTable.system],
    administrative = this[PermissionTable.administrative],
    createdAt = this[PermissionTable.createdAt],
    updatedAt = this[PermissionTable.updatedAt]
)

// Для join результатов с группировкой
fun List<ResultRow>.toRoleWithPermissionsDto(): RoleWithPermissionsDto? {
    val first = firstOrNull() ?: return null

    return RoleWithPermissionsDto(
        id = first[RoleTable.id].value,
        titleRu = first[RoleTable.titleRu],
        titleKk = first[RoleTable.titleKk],
        titleEn = first[RoleTable.titleEn],
        value = first[RoleTable.value],
        system = first[RoleTable.system],
        administrative = first[RoleTable.administrative],
        permissions = mapNotNull { row ->
            row.getOrNull(PermissionTable.id)?.let {
                row.toPermissionDto()
            }
        }.distinctBy { it.id},
        createdAt = first[RoleTable.createdAt],
        updatedAt = first[RoleTable.updatedAt]
    )
}