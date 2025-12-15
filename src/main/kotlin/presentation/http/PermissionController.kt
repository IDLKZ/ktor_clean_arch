package kz.idl.presentation.http

import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kz.idl.data.database.table.permission.PermissionTable
import kz.idl.domain.dto.permission.PermissionCreateDTO
import kz.idl.domain.dto.permission.PermissionUpdateRequest
import kz.idl.domain.usecase.permission.BulkCreatePermissionUseCase
import kz.idl.domain.usecase.permission.BulkDeletePermissionUseCase
import kz.idl.domain.usecase.permission.BulkRestorePermissionUseCase
import kz.idl.domain.usecase.permission.BulkUpdatePermissionUseCase
import kz.idl.domain.usecase.permission.CreatePermissionUseCase
import kz.idl.domain.usecase.permission.DeletePermissionUseCase
import kz.idl.domain.usecase.permission.GetPermissionByIdUseCase
import kz.idl.domain.usecase.permission.PaginatePermissionUseCase
import kz.idl.domain.usecase.permission.RestorePermissionUseCase
import kz.idl.domain.usecase.permission.UpdatePermissionByIdUseCase
import kz.idl.infrastructure.filter.permission.PermissionPaginationFilter
import kz.idl.presentation.response.ResponseHelper.created
import kz.idl.presentation.response.ResponseHelper.success
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.text.toBooleanStrictOrNull
import kotlin.text.toLong
import kotlin.text.toLongOrNull

class PermissionController : KoinComponent {
    private val paginatePermissionUseCase by inject<PaginatePermissionUseCase>()
    private val getPermissionByIdUseCase by inject<GetPermissionByIdUseCase>()
    private val createPermissionUseCase by inject<CreatePermissionUseCase>()
    private val updatePermissionUseCase by inject<UpdatePermissionByIdUseCase>()
    private val deletePermissionUseCase by inject<DeletePermissionUseCase>()
    private val restorePermissionUseCase by inject<RestorePermissionUseCase>()
    private val bulkCreatePermissionUseCase by inject<BulkCreatePermissionUseCase>()
    private val bulkUpdatePermissionUseCase by inject<BulkUpdatePermissionUseCase>()
    private val bulkDeletePermissionUseCase by inject<BulkDeletePermissionUseCase>()
    private val bulkRestorePermissionUseCase by inject<BulkRestorePermissionUseCase>()

    fun register(route: Route, routeName: String = "/permissions") {
        route.route(routeName) {
            get {
                val filter = PermissionPaginationFilter.fromParameters(call.request.queryParameters, table = PermissionTable)
                val permissions = paginatePermissionUseCase(filter)
                call.success(permissions, "OK", 200)
            }

            post("/create") {
                val dto = call.receive<PermissionCreateDTO>()
                val permission = createPermissionUseCase(dto)
                call.created(permission, "Permission created successfully")
            }

            put("/update/{id}") {
                val id = call.parameters["id"]?.toLong() ?: throw BadRequestException("provide_id")
                val dto = call.receive<PermissionCreateDTO>()
                val permission = updatePermissionUseCase(id, dto)
                call.created(permission, "Permission updated successfully")
            }

            get("/{id}") {
                val includeJoin = call.request.queryParameters["includeJoin"]?.toBooleanStrictOrNull()
                val showDeleted = call.request.queryParameters["showDeleted"]?.toBooleanStrictOrNull()
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: throw BadRequestException("Invalid ID")
                val permission = getPermissionByIdUseCase(id = id, showDeleted = showDeleted, includeJoin = includeJoin)
                call.success(permission)
            }

            delete("/delete/{id}") {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: throw BadRequestException("Invalid ID")
                val hardDelete = call.request.queryParameters["hardDelete"]?.toBooleanStrictOrNull() ?: false
                val result = deletePermissionUseCase(id = id, hardDelete = hardDelete)
                call.success(result, "Permission deleted successfully")
            }

            patch("/restore/{id}") {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: throw BadRequestException("Invalid ID")
                val result = restorePermissionUseCase(id = id)
                call.success(result, "Permission restored successfully")
            }

            // Bulk operations
            post("/bulk/create") {
                val dtos = call.receive<List<PermissionCreateDTO>>()
                val permissions = bulkCreatePermissionUseCase(dtos)
                call.created(permissions, "Permissions created successfully")
            }

            put("/bulk/update") {
                val requests = call.receive<List<PermissionUpdateRequest>>()
                val permissions = bulkUpdatePermissionUseCase(requests)
                call.success(permissions, "Permissions updated successfully")
            }

            delete("/bulk/delete") {
                val ids = call.receive<List<Long>>()
                val hardDelete = call.request.queryParameters["hardDelete"]?.toBooleanStrictOrNull() ?: false
                val count = bulkDeletePermissionUseCase(ids, hardDelete)
                call.success(count, "Deleted $count permissions successfully")
            }

            patch("/bulk/restore") {
                val ids = call.receive<List<Long>>()
                val count = bulkRestorePermissionUseCase(ids)
                call.success(count, "Restored $count permissions successfully")
            }
        }
    }
}
