package kz.idl.domain.repository
import kz.idl.domain.entity.PaginationMeta
import kz.idl.domain.filters.BaseFilter
import kz.idl.domain.filters.BasePaginationFilter
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.statements.BatchInsertStatement
import org.jetbrains.exposed.v1.core.statements.InsertStatement
import org.jetbrains.exposed.v1.core.statements.UpdateStatement


interface BaseRepository<T: LongIdTable> {
    suspend fun <DTO> findByLongId(
        id: Long,
        includeJoin: Boolean? = false,
        showDeleted: Boolean? = false,
        mapper: (ResultRow) -> DTO  // mapper последним для trailing lambda
    ): DTO?;
    suspend fun <DTO> findOneByFilter(
        filter: BaseFilter<T>,
        mapper: (ResultRow) -> DTO
    ):DTO?

     suspend fun <DTO> findAllWithFilter(
         filter: BaseFilter<T>,
         mapper: (ResultRow) -> DTO
     ):List<DTO>;

    suspend fun <DTO> all(
        includeJoin: Boolean? = false,
        showDeleted: Boolean? = false,
        mapper: (ResultRow) -> DTO
    ):List<DTO>;

      suspend fun count():Long;
      suspend fun countWithFilter(filter: BaseFilter<T>):Long;
      suspend fun <DTO> paginate(
        filter: BasePaginationFilter<T>,
        mapper: (ResultRow) -> DTO): PaginationMeta<DTO>;

    suspend fun <DTO> create(
        insertBlock: InsertStatement<Number>.() -> Unit,
        mapper: (ResultRow) -> DTO
    ):DTO?

    suspend fun <DTO> update(
        id: Long,
        updateBlock: UpdateStatement.() -> Unit,
        mapper: (ResultRow) -> DTO
    ):DTO?

    suspend fun delete(
        id: Long,
        hardDelete: Boolean = false,
    ):Boolean

    suspend fun restore(
        id: Long,
    ):Boolean

    suspend fun <DTO, ITEM> bulkCreate(
        items: List<ITEM>,
        insertBlock: BatchInsertStatement.(ITEM) -> Unit,
        mapper: (ResultRow) -> DTO
    ): List<DTO>

    suspend fun <DTO> bulkUpdate(
        updates: List<Pair<Long, UpdateStatement.() -> Unit>>,
        mapper: (ResultRow) -> DTO
    ): List<DTO>

    suspend fun bulkDelete(
        ids: List<Long>,
        hardDelete: Boolean = false
    ): Int

    suspend fun bulkRestore(
        ids: List<Long>
    ): Int
}