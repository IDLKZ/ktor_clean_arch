package kz.idl.domain.entity

import kotlinx.serialization.Serializable
import kotlin.math.ceil

/**
 * Класс данных с метаданными пагинации.
 */
@Serializable
data class PaginationMeta<T>(
    /**
     * Текущая страница / Current page
     */
    val currentPage: Int,

    /**
     * Записей на странице / Records per page
     */
    val perPage: Int,

    /**
     * Всего записей / Total records
     */
    val totalRecords: Long,

    /**
     * Всего страниц / Total pages
     */
    val totalPages: Int,

    /**
     * Есть следующая страница / Has next page
     */
    val hasNext: Boolean,

    /**
     * Есть предыдущая страница / Has previous page
     */
    val hasPrevious: Boolean,

    /**
     * Номер следующей страницы / Next page number
     */
    val nextPage: Int?,

    /**
     * Номер предыдущей страницы / Previous page number
     */
    val previousPage: Int?,

    val items: List<T>
)

fun <T> buildPaginationMeta(
    items: List<T>,
    currentPage: Int,
    perPage: Int,
    totalRecords: Long
): PaginationMeta<T> {

    val safePage = maxOf(1, currentPage)
    val safePerPage = perPage.coerceIn(1, 100)

    val totalPages = maxOf(
        1,
        ceil(totalRecords.toDouble() / safePerPage).toInt()
    )

    val hasPrevious = safePage > 1
    val hasNext = safePage < totalPages


    return PaginationMeta(
        currentPage = safePage,
        perPage = safePerPage,
        totalRecords = totalRecords,
        totalPages = totalPages,
        hasNext = hasNext,
        hasPrevious = hasPrevious,
        nextPage = if (hasNext) safePage + 1 else null,
        previousPage = if (hasPrevious) safePage - 1 else null,
        items = items
    )
}

