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
import kz.idl.data.database.table.role.RoleTable
import kz.idl.domain.dto.role.RoleCreateDTO
import kz.idl.domain.dto.role.RoleUpdateRequest
import kz.idl.domain.usecase.role.BulkCreateRoleUseCase
import kz.idl.domain.usecase.role.BulkDeleteRoleUseCase
import kz.idl.domain.usecase.role.BulkRestoreRoleUseCase
import kz.idl.domain.usecase.role.BulkUpdateRoleUseCase
import kz.idl.domain.usecase.role.CreateRoleUseCase
import kz.idl.domain.usecase.role.DeleteRoleUseCase
import kz.idl.domain.usecase.role.GetRoleByIdUseCase
import kz.idl.domain.usecase.role.PaginateRoleUseCase
import kz.idl.domain.usecase.role.RestoreRoleUseCase
import kz.idl.domain.usecase.role.UpdateRoleByIdCase
import kz.idl.infrastructure.filter.role.RolePaginationFilter
import kz.idl.presentation.response.ResponseHelper.created
import kz.idl.presentation.response.ResponseHelper.success
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.text.toBooleanStrictOrNull
import kotlin.text.toLong
import kotlin.text.toLongOrNull

class RoleController : KoinComponent {
    private val paginateRoleUseCase by inject<PaginateRoleUseCase>()
    private val getRoleByIdUseCase by inject<GetRoleByIdUseCase>()
    private val createRoleUseCase by inject<CreateRoleUseCase>()
    private val updateRoleUseCase by inject<UpdateRoleByIdCase>()
    private val deleteRoleUseCase by inject<DeleteRoleUseCase>()
    private val restoreRoleUseCase by inject<RestoreRoleUseCase>()
    private val bulkCreateRoleUseCase by inject<BulkCreateRoleUseCase>()
    private val bulkUpdateRoleUseCase by inject<BulkUpdateRoleUseCase>()
    private val bulkDeleteRoleUseCase by inject<BulkDeleteRoleUseCase>()
    private val bulkRestoreRoleUseCase by inject<BulkRestoreRoleUseCase>()

    fun register(route: Route,routeName: String = "/roles"){
        route.route(routeName) {
            get {
                val filter = RolePaginationFilter.fromParameters(call.request.queryParameters, table = RoleTable)
                val roles = paginateRoleUseCase(filter)
                call.success(roles, "OK", 200)
            }

            post("/create") {
                val dto = call.receive<RoleCreateDTO>()
                val role = createRoleUseCase(dto)
                call.created(role, "Role created successfully")
            }

            put("/update/{id}") {
                val id = call.parameters["id"]?.toLong() ?: throw BadRequestException("provide_id")
                val dto = call.receive<RoleCreateDTO>()
                val role = updateRoleUseCase(id,dto)
                call.created(role, "Role Updated successfully")
            }

            get("/{id}") {
                val includeJoin = call.request.queryParameters["includeJoin"]?.toBooleanStrictOrNull()
                val showDeleted = call.request.queryParameters["showDeleted"]?.toBooleanStrictOrNull()
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: throw BadRequestException("Invalid ID")
                val role = getRoleByIdUseCase(id = id, showDeleted = showDeleted, includeJoin = includeJoin)
                call.success(role)
            }

            delete("/delete/{id}") {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: throw BadRequestException("Invalid ID")
                val hardDelete = call.request.queryParameters["hardDelete"]?.toBooleanStrictOrNull() ?: false
                val result = deleteRoleUseCase(id = id, hardDelete = hardDelete)
                call.success(result, "Role deleted successfully")
            }

            patch("/restore/{id}") {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: throw BadRequestException("Invalid ID")
                val result = restoreRoleUseCase(id = id)
                call.success(result, "Role restored successfully")
            }

            // Bulk operations
            post("/bulk/create") {
                val dtos = call.receive<List<RoleCreateDTO>>()
                val roles = bulkCreateRoleUseCase(dtos)
                call.created(roles, "Roles created successfully")
            }

            put("/bulk/update") {
                val requests = call.receive<List<RoleUpdateRequest>>()
                val roles = bulkUpdateRoleUseCase(requests)
                call.success(roles, "Roles updated successfully")
            }

            delete("/bulk/delete") {
                val ids = call.receive<List<Long>>()
                val hardDelete = call.request.queryParameters["hardDelete"]?.toBooleanStrictOrNull() ?: false
                val count = bulkDeleteRoleUseCase(ids, hardDelete)
                call.success(count, "Deleted $count roles successfully")
            }

            patch("/bulk/restore") {
                val ids = call.receive<List<Long>>()
                val count = bulkRestoreRoleUseCase(ids)
                call.success(count, "Restored $count roles successfully")
            }
        }
    }
}