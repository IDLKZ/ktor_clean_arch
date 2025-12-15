package kz.idl.data.database.table.permission

import kotlinx.datetime.LocalDateTime
import kz.idl.data.database.table.generic.SoftDeleteAtTable
import kz.idl.shared.constraints.DataConstraints
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

object PermissionTable : SoftDeleteAtTable("permissions") {
    val titleRu: Column<String> = varchar("title_ru", DataConstraints.StandardVarcharLength)
    val titleKk: Column<String?> = varchar("title_kk", DataConstraints.StandardVarcharLength).nullable()
    val titleEn: Column<String?> = varchar("title_en", DataConstraints.StandardVarcharLength).nullable()
    val descriptionRu: Column<String?> = text("description_ru").nullable()
    val descriptionKk: Column<String?> = text("description_kk").nullable()
    val descriptionEn: Column<String?> = text("description_en").nullable()
    val value: Column<String> = varchar("value", DataConstraints.StandardUniqueValueLength).uniqueIndex("idx_permissions_value")
    val system: Column<Boolean> = bool("system").default(false).index("idx_roles_system")
    val administrative: Column<Boolean> = bool("administrative").default(false).index("idx_permissions_administrative")
}