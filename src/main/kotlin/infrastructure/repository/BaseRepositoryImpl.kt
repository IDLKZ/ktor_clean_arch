package kz.idl.infrastructure.repository

import kz.idl.data.database.table.generic.SoftDeleteAtTable
import kz.idl.data.database.table.generic.SoftIsDeleteTable
import kz.idl.domain.repository.BaseRepository
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.jdbc.Query
import org.jetbrains.exposed.v1.jdbc.selectAll
import kotlinx.coroutines.Dispatchers
import kz.idl.domain.dto.BaseCreateDTO
import kz.idl.domain.entity.PaginationMeta
import kz.idl.domain.entity.buildPaginationMeta
import kz.idl.domain.filters.BaseFilter
import kz.idl.domain.filters.BasePaginationFilter
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.isNotNull
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.statements.InsertStatement
import org.jetbrains.exposed.v1.core.statements.UpdateStatement
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.v1.jdbc.update
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.slf4j.LoggerFactory

abstract class BaseRepositoryImpl<T : LongIdTable>(
    protected val table: T
) : BaseRepository<T> {
    private val logger = LoggerFactory.getLogger(BaseRepositoryImpl::class.java)
    // Без join
    protected open fun baseQuery(): Query = table.selectAll()

    // С join — наследники переопределяют
    protected open fun baseJoinQuery(): Query = table.selectAll()

    private fun chooseQuery(includeJoin: Boolean?):Query{
        if(includeJoin != null){
            if(includeJoin){
                return baseJoinQuery()
            }
        }
        return baseQuery()
    }

    override suspend fun <DTO> findByLongId(
        id: Long,
        includeJoin: Boolean?,
        showDeleted: Boolean?,
        mapper: (ResultRow) -> DTO  // mapper последним для trailing lambda
    ): DTO? = dbQuery {
            val deletedCondition = useShowDeleted(showDeleted)
            val query = chooseQuery(includeJoin)
            query
                .where { table.id eq id and (deletedCondition ?: Op.TRUE) }
                .singleOrNull()
                ?.let(mapper)

    }

    override suspend fun <DTO> findOneByFilter(
        filter: BaseFilter<T>,
        mapper: (ResultRow) -> DTO
    ):DTO? = dbQuery {
        val query = if(filter.includeJoin) baseJoinQuery() else baseQuery()
        filter.buildConditions()?.let { query.where { it } }
        query
            .orderBy(filter.getOrderColumn() to filter.getOrderDirection())
            .singleOrNull()
            ?.let(mapper)
    }

    override suspend fun <DTO> findAllWithFilter(
        filter: BaseFilter<T>,
        mapper: (ResultRow) -> DTO): List<DTO>
    {
        val base = if (filter.includeJoin) baseJoinQuery() else baseQuery()
        val query = filter.buildConditions()
            ?.let { base.where { it } }
            ?: base
        return query
            .orderBy(filter.getOrderColumn() to filter.getOrderDirection())
            .map(mapper)
    }

    override suspend fun <DTO> all(
        includeJoin: Boolean?,
        showDeleted: Boolean?,
        mapper: (ResultRow) -> DTO
    ):List<DTO>{
        val base = chooseQuery(includeJoin)
        val query = useShowDeleted(showDeleted)
            ?.let { base.where { it } }
            ?: base
        return query.map(mapper)
    }

    override suspend fun count():Long{
       return table.selectAll().count()
    }

    override suspend fun countWithFilter(filter: BaseFilter<T>): Long{
        val base = if (filter.includeJoin) baseJoinQuery() else baseQuery()
        val query = filter.buildConditions()
            ?.let { base.where { it } }
            ?: base
        return query.count()
    }


    override suspend fun <DTO> paginate(
        filter: BasePaginationFilter<T>,
        mapper: (ResultRow) -> DTO
    ): PaginationMeta<DTO> = dbQuery {  // ← Добавлено dbQuery
        val base = if (filter.includeJoin) baseJoinQuery() else baseQuery()
        val page = filter.validPage
        val perPage = filter.validPerPage

        val query = filter.buildConditions()
            ?.let { base.where { it } }
            ?: base

        val orderedQuery = query.orderBy(filter.getOrderColumn() to filter.getOrderDirection())
        val count = orderedQuery.count()
        val data = orderedQuery
            .limit(perPage.toInt())
            .offset((((page - 1) * perPage).toLong()))
            .map(mapper)

        buildPaginationMeta(
            items = data,
            currentPage = page,
            perPage = perPage,
            totalRecords = count
        )
    }

    override suspend fun <DTO> create(
        insertBlock: InsertStatement<Number>.() -> Unit,
        mapper: (ResultRow) -> DTO
    ):DTO? = dbQuery{
        table.insert {
            it.insertBlock()
        }.resultedValues!!
            .single()
            .let(mapper)
    }

    override suspend fun <DTO> update(
        id: Long,
        updateBlock: UpdateStatement.() -> Unit,
        mapper: (ResultRow) -> DTO
    ):DTO? = dbQuery {
        val updated:Int =  table.update({ table.id eq id }) {
            it.updateBlock()
        }
        updated
            .takeIf { it > 0 }
            ?.let {
                baseQuery()
                    .where { table.id eq id }
                    .singleOrNull()
                    ?.let(mapper)
            }
    }

    override suspend fun delete(
        id: Long,
        hardDelete: Boolean
    ): Boolean = dbQuery {
        when {
            !isUsingSoftDelete() -> hardDelete(id)
            hardDelete -> hardDelete(id)
            else -> softDelete(id)
        }
    }

    override suspend fun restore(
        id: Long
    ) : Boolean = dbQuery {
        when {
            !isUsingSoftDelete() -> false
            else -> restoreSoftDelete(id)
        }
    }


    override suspend fun <DTO, ITEM> bulkCreate(
        items: List<ITEM>,
        insertBlock: org.jetbrains.exposed.v1.core.statements.BatchInsertStatement.(ITEM) -> Unit,
        mapper: (ResultRow) -> DTO
    ): List<DTO> = dbQuery {
        if (items.isEmpty()) return@dbQuery emptyList()

        table.batchInsert(items) { item ->
            insertBlock(item)
        }.map(mapper)
    }

    override suspend fun <DTO> bulkUpdate(
        updates: List<Pair<Long, UpdateStatement.() -> Unit>>,
        mapper: (ResultRow) -> DTO
    ): List<DTO> = dbQuery {
        updates.mapNotNull { (id, updateBlock) ->
            val updated = table.update({ table.id eq id }) {
                it.updateBlock()
            }
            if (updated > 0) {
                baseQuery()
                    .where { table.id eq id }
                    .singleOrNull()
                    ?.let(mapper)
            } else null
        }
    }

    override suspend fun bulkDelete(
        ids: List<Long>,
        hardDelete: Boolean
    ): Int = dbQuery {
        if (hardDelete || !isUsingSoftDelete()) {
            table.deleteWhere { table.id inList ids }
        } else {
            when (table) {
                is SoftDeleteAtTable ->
                    table.update(
                        where = { table.id inList ids }
                    ) {
                        it[deletedAt] = CurrentDateTime
                    }

                is SoftIsDeleteTable ->
                    table.update(
                        where = { table.id inList ids }
                    ) {
                        it[isDeleted] = true
                    }

                else -> 0
            }
        }
    }

    override suspend fun bulkRestore(ids: List<Long>): Int = dbQuery {
        when (table) {
            is SoftDeleteAtTable ->
                table.update(
                    where = { table.id inList ids }
                ) {
                    it[deletedAt] = null
                }

            is SoftIsDeleteTable ->
                table.update(
                    where = { table.id inList ids }
                ) {
                    it[isDeleted] = false
                }

            else -> 0
        }
    }


    private fun useShowDeleted(showDeleted: Boolean?): Op<Boolean>? {
        return when {
            table is SoftDeleteAtTable && showDeleted != null -> {
                if (showDeleted) table.deletedAt.isNotNull() else table.deletedAt.isNull()
            }
            table is SoftIsDeleteTable && showDeleted != null -> {
                table.isDeleted eq showDeleted
            }
            else -> null
        }
    }

    private suspend fun softDelete(id: Long): Boolean = dbQuery {
        when (table) {
            is SoftDeleteAtTable ->
                table.update({ table.id eq id }) {
                    it[deletedAt] = CurrentDateTime
                } > 0

            is SoftIsDeleteTable ->
                table.update({ table.id eq id }) {
                    it[isDeleted] = true
                } > 0

            else -> false
        }
    }

    private suspend fun restoreSoftDelete(id:Long): Boolean = dbQuery {
        when (table) {
            is SoftDeleteAtTable ->
                table.update({ table.id eq id }) {
                    it[deletedAt] = null
                } > 0

            is SoftIsDeleteTable ->
                table.update({ table.id eq id }) {
                    it[isDeleted] = false
                } > 0

            else -> false
        }
    }


    private suspend fun hardDelete(id: Long): Boolean = dbQuery {
        table.deleteWhere { table.id eq id}.let { if(it > 0) true else false }
    }


    private fun isUsingSoftDelete():Boolean{
        return when(table){
            is SoftDeleteAtTable -> true
            is SoftIsDeleteTable -> true
            else -> false
        }
    }


    protected suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) {
            addLogger(StdOutSqlLogger)
            try{
                block()
            }
            catch (e: Exception){
                logger.error(e.message)
                throw e
            }
        }
}