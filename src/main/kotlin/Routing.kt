package kz.idl

import io.ktor.server.application.*
import io.ktor.server.http.content.staticFiles
import io.ktor.server.routing.*
import kz.idl.config.StorageConfig
import kz.idl.presentation.http.PermissionController
import kz.idl.presentation.http.RoleController
import kz.idl.presentation.http.RolePermissionController
import kz.idl.presentation.http.file.FileController
import org.koin.ktor.ext.inject
import java.io.File

fun Application.configureRouting() {
    val storageConfig by inject<StorageConfig>()

    routing {
        RoleController().register(this)
        PermissionController().register(this)
        RolePermissionController().register(this)
        FileController().register(this)

        // Serve static files from uploads directory
        staticFiles("/uploads", File(storageConfig.uploadDir))
    }
}

