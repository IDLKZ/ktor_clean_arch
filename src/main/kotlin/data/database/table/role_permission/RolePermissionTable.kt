package kz.idl.data.database.table.role_permission

import kz.idl.data.database.table.permission.PermissionTable
import kz.idl.data.database.table.role.RoleTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object RolePermissionTable : LongIdTable("role_permissions") {
    val roleId = reference("role_id", RoleTable, onDelete = ReferenceOption.CASCADE)
    val permissionId = reference("permission_id", PermissionTable, onDelete = ReferenceOption.CASCADE)

    init {
        // Обычные индексы для оптимизации поиска
        index(false, roleId)
        index(false, permissionId)
        // Уникальный индекс для предотвращения дубликатов
        uniqueIndex(roleId, permissionId)
    }
}