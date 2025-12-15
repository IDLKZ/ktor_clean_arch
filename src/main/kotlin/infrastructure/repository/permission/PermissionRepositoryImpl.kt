package kz.idl.infrastructure.repository.permission

import kz.idl.data.database.table.permission.PermissionTable
import kz.idl.domain.repository.permission.PermissionRepository
import kz.idl.infrastructure.repository.BaseRepositoryImpl

class PermissionRepositoryImpl : BaseRepositoryImpl<PermissionTable>(PermissionTable), PermissionRepository {
}
