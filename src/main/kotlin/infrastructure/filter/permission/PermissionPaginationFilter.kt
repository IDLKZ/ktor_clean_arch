package kz.idl.infrastructure.filter.permission

import io.ktor.http.Parameters
import kz.idl.data.database.table.permission.PermissionTable
import kz.idl.domain.filters.BasePaginationFilter
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import kotlin.text.toBooleanStrictOrNull

class PermissionPaginationFilter(
    table: PermissionTable,
    override val orderBy: String = "id",
    override val orderDirection: String = "desc",
    override val showDeleted: Boolean? = null,
    override val includeJoin: Boolean = false,
    override val search: String? = null,
    override val perPage: Int = 20,
    override val page: Int = 1,
    // Специфичные для Permission
    val system: Boolean? = null,
    val administrative: Boolean? = null,
    val permissionIds: List<Long>? = null,
) : BasePaginationFilter<PermissionTable>(table) {
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
        fun fromParameters(params: Parameters, table: PermissionTable): PermissionPaginationFilter {
            return PermissionPaginationFilter(
                table = table,
                orderBy = params["orderBy"] ?: "id",
                orderDirection = params["orderDirection"] ?: "desc",
                showDeleted = params["showDeleted"]?.toBooleanStrictOrNull(),
                search = params["search"],
                system = params["system"]?.toBooleanStrictOrNull(),
                administrative = params["administrative"]?.toBooleanStrictOrNull(),
                includeJoin = params["includeJoin"].toBoolean(),
                perPage = params["perPage"]?.toInt() ?: 20,
                page = params["page"]?.toInt() ?: 1,
            )
        }
    }
}
