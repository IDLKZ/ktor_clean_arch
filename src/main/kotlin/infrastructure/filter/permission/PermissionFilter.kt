package kz.idl.infrastructure.filter.permission

import io.ktor.http.Parameters
import kz.idl.data.database.table.permission.PermissionTable
import kz.idl.domain.filters.BaseFilter
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList

open class PermissionFilter(
    table: PermissionTable,
    override val orderBy: String = "id",
    override val orderDirection: String = "desc",
    override val showDeleted: Boolean? = null,
    override val search: String? = null,
    override val includeJoin: Boolean = false,
    // Специфичные для Permission
    val system: Boolean? = null,
    val administrative: Boolean? = null,
    val permissionIds: List<Long>? = null,
) : BaseFilter<PermissionTable>(table) {

    override fun getSearchColumns(): List<Column<*>> {
        return listOf(
            table.titleRu,
            table.titleKk,
            table.titleEn,
            table.descriptionRu,
            table.descriptionKk,
            table.descriptionEn,
            table.value,
        )
    }

    override fun getJoinSearchColumns(): List<Column<*>> {
        return emptyList()
    }

    override fun applyFilters(): Op<Boolean>? {
        val conditions = mutableListOf<Op<Boolean>>()
        permissionIds?.let { conditions.add(table.id.inList(it)) }
        system?.let { conditions.add(table.system eq it) }
        administrative?.let { conditions.add(table.administrative eq it) }
        return conditions.reduceOrNull { acc, op -> acc and op }
    }

    companion object {
        fun fromParameters(params: Parameters, table: PermissionTable): PermissionFilter {
            return PermissionFilter(
                table = table,
                orderBy = params["orderBy"] ?: "id",
                orderDirection = params["orderDirection"] ?: "desc",
                showDeleted = params["showDeleted"]?.toBooleanStrictOrNull(),
                search = params["search"],
                system = params["system"]?.toBooleanStrictOrNull(),
                includeJoin = params["includeJoin"].toBoolean(),
                administrative = params["administrative"]?.toBooleanStrictOrNull(),
            )
        }

        fun byValue(value: String, table: PermissionTable = PermissionTable): PermissionFilter {
            return PermissionValueFilter(table, value)
        }
    }
}

private class PermissionValueFilter(
    table: PermissionTable,
    private val value: String
) : PermissionFilter(table) {
    override fun applyFilters(): Op<Boolean> {
        return table.value eq value
    }
}
