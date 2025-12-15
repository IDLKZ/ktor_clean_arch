package kz.idl.domain.filters

import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

abstract class BasePaginationFilter<T: LongIdTable>(table:T) : BaseFilter<T>(table) {
    open val perPage: Int = 20
    open val page: Int = 1

    val validPage: Int
        get() = if (page <= 0) 1 else page

    val validPerPage: Int
        get() = if (perPage <= 0 || perPage > 100) 20 else perPage

    val offset: Long
        get() = ((validPage - 1) * validPerPage).toLong()

}