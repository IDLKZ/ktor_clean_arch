package kz.idl

import io.ktor.server.application.*
import io.ktor.server.routing.*
import kz.idl.presentation.http.PermissionController
import kz.idl.presentation.http.RoleController
import kz.idl.presentation.http.RolePermissionController

fun Application.configureRouting() {

    routing {
        RoleController().register(this)
        PermissionController().register(this)
        RolePermissionController().register(this)
    }
}

