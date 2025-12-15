package kz.idl.domain.filters

import kotlinx.html.B
import kz.idl.data.database.table.generic.SoftDeleteAtTable
import kz.idl.data.database.table.generic.SoftIsDeleteTable
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.LikeEscapeOp
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.isNotNull
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.core.or

abstract class BaseFilter<T : LongIdTable>(
    protected val table: T
) {
    // Параметры фильтрации — open для переопределения или передачи
    open val orderBy: String = "id"
    open val orderDirection: String = "desc"
    open val includeJoin: Boolean = false
    open val showDeleted: Boolean? = null
    open val search: String? = null

    // Наследники определяют колонки для поиска
    abstract fun getSearchColumns(): List<Column<*>>

    open fun getJoinSearchColumns(): List<Column<*>> = emptyList()

    // Наследники добавляют свои фильтры
    abstract fun applyFilters(): Op<Boolean>?

    // Поиск по нескольким колонкам
    fun buildSearchCondition(): Op<Boolean>? {
        val searchValue = search?.takeIf { it.isNotBlank() }?.lowercase() ?: return null
        val columns = if (includeJoin) {
            getSearchColumns() + getJoinSearchColumns()
        } else {
            getSearchColumns()
        }
        return columns
            .mapNotNull { column ->
                when (column) {
                    else -> {
                        val stringCol = column as? Column<String>
                            ?: (column as? Column<String?>)
                        stringCol?.lowerCase()?.like("%$searchValue%")
                    }
                }
            }
            .takeIf { it.isNotEmpty() }
            ?.reduceOrNull { acc, op -> (acc or op) as LikeEscapeOp }
    }

    // Получить колонку для сортировки
    fun getOrderColumn(): Column<*> {
        return table.columns.find { it.name == orderBy } ?: table.id
    }

    fun getOrderDirection(): SortOrder {
        if(orderDirection.lowercase() == "desc"){
            return SortOrder.DESC
        }
        else {
            return SortOrder.ASC
        }
    }


    // Собрать все условия
    open fun buildConditions(): Op<Boolean>? {
        val conditions = mutableListOf<Op<Boolean>>()

        buildSearchCondition()?.let { conditions.add(it) }
        applyFilters()?.let { conditions.add(it) }
        buildDeletedCondition()?.let { conditions.add(it) }

        return conditions.reduceOrNull { acc, op -> acc and op }
    }

    fun buildDeletedCondition(): Op<Boolean>? {
        return when {
            table is SoftDeleteAtTable && showDeleted != null -> {
                if (showDeleted == true) table.deletedAt.isNotNull() else table.deletedAt.isNull()
            }
            table is SoftIsDeleteTable && showDeleted != null -> {
                table.isDeleted eq showDeleted!!
            }
            else -> null
        }
    }
}