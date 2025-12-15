package kz.idl.domain.dto

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.statements.BatchInsertStatement
import org.jetbrains.exposed.v1.core.statements.InsertStatement
import org.jetbrains.exposed.v1.core.statements.UpdateStatement

interface BaseCreateDTO<T : LongIdTable> {
    fun insertBlock(table: T, stmt: BatchInsertStatement)
}

interface BaseUpdateDTO<T : LongIdTable> {
    fun updateBlock(table: T, stmt: UpdateStatement)
}

interface BaseOneCreateDTO<T : LongIdTable> {
    fun createEntity(table: T): InsertStatement<Number>.() -> Unit
}