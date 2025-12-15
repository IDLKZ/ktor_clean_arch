package kz.idl.data.database.table.generic

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

abstract class BasicLongTable(name: String):LongIdTable(name) {
    val createdAt: Column<LocalDateTime> = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt: Column<LocalDateTime> = datetime("updated_at").defaultExpression(CurrentDateTime)
}
abstract class SoftDeleteAtTable(name: String) : BasicLongTable(name) {
    val deletedAt: Column<LocalDateTime?> = datetime("deleted_at").nullable()
}

abstract class SoftIsDeleteTable(name: String) : BasicLongTable(name) {
    val isDeleted: Column<Boolean> = bool("is_deleted").default(false)
}