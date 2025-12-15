package kz.idl.presentation.http

import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kz.idl.data.database.table.role_permission.RolePermissionTable
import kz.idl.domain.dto.role_permission.RolePermissionCDTO
import kz.idl.domain.usecase.role_permission.BulkCreateRolePermissionUseCase
import kz.idl.domain.usecase.role_permission.BulkDeleteRolePermissionUseCase
import kz.idl.domain.usecase.role_permission.CreateRolePermissionUseCase
import kz.idl.domain.usecase.role_permission.DeleteRolePermissionUseCase
import kz.idl.domain.usecase.role_permission.GetRolePermissionByIdUseCase
import kz.idl.domain.usecase.role_permission.PaginateRolePermissionUseCase
import kz.idl.infrastructure.filter.role_permission.RolePermissionPaginationFilter
import kz.idl.presentation.response.ResponseHelper.created
import kz.idl.presentation.response.ResponseHelper.success
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.text.toBooleanStrictOrNull
import kotlin.text.toLongOrNull

class RolePermissionController : KoinComponent {
    private val paginateRolePermissionUseCase by inject<PaginateRolePermissionUseCase>()
    private val getRolePermissionByIdUseCase by inject<GetRolePermissionByIdUseCase>()
    private val createRolePermissionUseCase by inject<CreateRolePermissionUseCase>()
    private val deleteRolePermissionUseCase by inject<DeleteRolePermissionUseCase>()
    private val bulkCreateRolePermissionUseCase by inject<BulkCreateRolePermissionUseCase>()
    private val bulkDeleteRolePermissionUseCase by inject<BulkDeleteRolePermissionUseCase>()

    fun register(route: Route, routeName: String = "/role-permissions") {
        route.route(routeName) {
            get {
                val filter = RolePermissionPaginationFilter.fromParameters(
                    call.request.queryParameters,
                    table = RolePermissionTable
                )
                val rolePermissions = paginateRolePermissionUseCase(filter)
                call.success(rolePermissions, "OK", 200)
            }

            post("/create") {
                val dto = call.receive<RolePermissionCDTO>()
                val rolePermission = createRolePermissionUseCase(dto)
                call.created(rolePermission, "RolePermission created successfully")
            }

            get("/{id}") {
                val includeJoin = call.request.queryParameters["includeJoin"]?.toBooleanStrictOrNull()
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: throw BadRequestException("Invalid ID")
                val rolePermission = getRolePermissionByIdUseCase(id = id, includeJoin = includeJoin)
                call.success(rolePermission)
            }

            delete("/delete/{id}") {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: throw BadRequestException("Invalid ID")
                val hardDelete = call.request.queryParameters["hardDelete"]?.toBooleanStrictOrNull() ?: true
                val result = deleteRolePermissionUseCase(id = id, hardDelete = hardDelete)
                call.success(result, "RolePermission deleted successfully")
            }

            // Bulk operations
            post("/bulk/create") {
                val dtos = call.receive<List<RolePermissionCDTO>>()
                val rolePermissions = bulkCreateRolePermissionUseCase(dtos)
                call.created(rolePermissions, "RolePermissions created successfully")
            }

            delete("/bulk/delete") {
                val ids = call.receive<List<Long>>()
                val hardDelete = call.request.queryParameters["hardDelete"]?.toBooleanStrictOrNull() ?: true
                val count = bulkDeleteRolePermissionUseCase(ids, hardDelete)
                call.success(count, "Deleted $count role-permissions successfully")
            }
        }
    }
}
