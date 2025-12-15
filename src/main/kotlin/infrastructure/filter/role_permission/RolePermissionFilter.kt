package kz.idl.infrastructure.filter.role_permission

import io.ktor.http.Parameters
import kz.idl.data.database.table.role_permission.RolePermissionTable
import kz.idl.domain.filters.BaseFilter
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList

open class RolePermissionFilter(
    table: RolePermissionTable,
    override val orderBy: String = "id",
    override val orderDirection: String = "desc",
    override val showDeleted: Boolean? = null,
    override val search: String? = null,
    override val includeJoin: Boolean = false,
    val roleId: Long? = null,
    val permissionId: Long? = null,
    val roleIds: List<Long>? = null,
    val permissionIds: List<Long>? = null,
) : BaseFilter<RolePermissionTable>(table) {

    override fun getSearchColumns(): List<Column<*>> {
        return emptyList()
    }

    override fun getJoinSearchColumns(): List<Column<*>> {
        return emptyList()
    }

    override fun applyFilters(): Op<Boolean>? {
        val conditions = mutableListOf<Op<Boolean>>()
        roleId?.let { conditions.add(table.roleId eq it) }
        permissionId?.let { conditions.add(table.permissionId eq it) }
        roleIds?.let { conditions.add(table.roleId.inList(it)) }
        permissionIds?.let { conditions.add(table.permissionId.inList(it)) }
        return conditions.reduceOrNull { acc, op -> acc and op }
    }

    companion object {
        fun fromParameters(params: Parameters, table: RolePermissionTable): RolePermissionFilter {
            return RolePermissionFilter(
                table = table,
                orderBy = params["orderBy"] ?: "id",
                orderDirection = params["orderDirection"] ?: "desc",
                showDeleted = params["showDeleted"]?.toBooleanStrictOrNull(),
                search = params["search"],
                includeJoin = params["includeJoin"]?.toBoolean() ?: false,
                roleId = params["roleId"]?.toLongOrNull(),
                permissionId = params["permissionId"]?.toLongOrNull(),
            )
        }

        fun byRoleAndPermission(roleId: Long, permissionId: Long, table: RolePermissionTable = RolePermissionTable): RolePermissionFilter {
            return RolePermissionByRoleAndPermissionFilter(table, roleId, permissionId)
        }
    }
}

private class RolePermissionByRoleAndPermissionFilter(
    table: RolePermissionTable,
    roleIdValue: Long,
    permissionIdValue: Long
) : RolePermissionFilter(table, roleId = roleIdValue, permissionId = permissionIdValue) {
    override fun applyFilters(): Op<Boolean> {
        return (table.roleId eq roleId!!) and (table.permissionId eq permissionId!!)
    }
}
