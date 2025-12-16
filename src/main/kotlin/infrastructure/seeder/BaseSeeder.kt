package kz.idl.infrastructure.seeder

import kz.idl.domain.dto.BaseCreateDTO
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

abstract class BaseSeeder<T: LongIdTable, DTO: BaseCreateDTO<T>> {
    abstract suspend fun seed();
    abstract suspend fun getDevData():List<DTO>;
    abstract suspend fun getStageData():List<DTO>;
    abstract suspend fun getProdData():List<DTO>;
}