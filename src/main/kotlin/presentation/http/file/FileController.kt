package kz.idl.presentation.http.file

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kz.idl.domain.service.file.FileService
import kz.idl.presentation.response.ResponseHelper.success
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory

class FileController : KoinComponent {
    private val fileService by inject<FileService>()
    private val logger = LoggerFactory.getLogger(FileController::class.java)

    fun register(route: Route, routeName: String = "/files") {
        route.route(routeName) {

            // Upload file
            post("/upload") {
                val multipart = call.receiveMultipart()
                var fileDTO: kz.idl.domain.dto.file.FileDTO? = null

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            val fileName = part.originalFileName
                                ?: throw BadRequestException("file_name_required")

                            val mimeType = part.contentType?.toString()
                                ?: "application/octet-stream"

                            logger.info("Uploading file: $fileName (${mimeType})")

                            part.streamProvider().use { inputStream ->
                                fileDTO = fileService.store(fileName, mimeType, inputStream)
                            }
                        }
                        else -> {
                            // Ignore other parts
                        }
                    }
                    part.dispose()
                }

                if (fileDTO == null) {
                    throw BadRequestException("no_file_provided")
                }

                call.success(
                    data = fileDTO,
                    message = "File uploaded successfully",
                    code = 201
                )
            }

            // Download file
            get("/{filename}") {
                val filename = call.parameters["filename"]
                    ?: throw BadRequestException("filename_required")

                if (!fileService.exists(filename)) {
                    throw NotFoundException("file_not_found")
                }

                val fileInfo = fileService.getFileInfo(filename)
                    ?: throw NotFoundException("file_not_found")

                val fileBytes = fileService.load(filename)

                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment
                        .withParameter(ContentDisposition.Parameters.FileName, fileInfo.originalName)
                        .toString()
                )

                call.respondBytes(fileBytes, ContentType.parse(fileInfo.mimeType))
            }

            // Get file info
            get("/{filename}/info") {
                val filename = call.parameters["filename"]
                    ?: throw BadRequestException("filename_required")

                val fileInfo = fileService.getFileInfo(filename)
                    ?: throw NotFoundException("file_not_found")

                call.success(fileInfo)
            }

            // Delete file
            delete("/{filename}") {
                val filename = call.parameters["filename"]
                    ?: throw BadRequestException("filename_required")

                val deleted = fileService.delete(filename)

                if (deleted) {
                    call.success(
                        data = mapOf("deleted" to true),
                        message = "File deleted successfully"
                    )
                } else {
                    throw NotFoundException("file_not_found")
                }
            }

            // Check if file exists
            get("/{filename}/exists") {
                val filename = call.parameters["filename"]
                    ?: throw BadRequestException("filename_required")

                val exists = fileService.exists(filename)

                call.success(
                    data = mapOf("exists" to exists)
                )
            }

            // Get storage info
            get("/storage/info") {
                val config = fileService.getConfig()

                call.success(
                    data = mapOf(
                        "storageDir" to fileService.getStorageDir(),
                        "maxFileSizeMB" to (config.maxFileSizeBytes / (1024 * 1024)),
                        "allowedExtensions" to config.allowedExtensions,
                        "forbiddenExtensions" to config.forbiddenExtensions
                    )
                )
            }
        }
    }
}
