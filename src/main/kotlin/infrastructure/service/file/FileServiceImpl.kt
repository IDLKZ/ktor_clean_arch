package kz.idl.infrastructure.service.file

import io.ktor.server.plugins.BadRequestException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kz.idl.config.StorageConfig
import kz.idl.domain.dto.file.FileDTO
import kz.idl.domain.service.file.FileService
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID

class FileServiceImpl(private val config: StorageConfig) : FileService {

    private val logger = LoggerFactory.getLogger(FileServiceImpl::class.java)
    private val storageDir: Path = Paths.get(config.uploadDir).toAbsolutePath().normalize()


    init {
        Files.createDirectories(storageDir)
        logger.info("Storage initialized: $storageDir")
        logger.info("Max file size: ${config.maxFileSizeBytes / (1024 * 1024)} MB")
        logger.info("Forbidden extensions: ${config.forbiddenExtensions}")
        if (config.allowedExtensions.isNotEmpty()) {
            logger.info("Allowed extensions: ${config.allowedExtensions}")
        }
    }


    override suspend fun store(
        fileName: String,
        mimeType: String,
        bytes: ByteArray
    ): FileDTO {
        validateFile(fileName, bytes.size.toLong())
        val extension = extractExtension(fileName)
        val uniqueFileName = generateUniqueName(extension)
        val targetPath = storageDir.resolve(uniqueFileName)
        withContext(Dispatchers.IO) {
            Files.write(targetPath, bytes)
        }
        logger.debug("File stored: $targetPath (${bytes.size} bytes)")
        return createFileDto(fileName, uniqueFileName, targetPath, mimeType, bytes.size.toLong())
    }

    override suspend fun store(
        fileName: String,
        mimeType: String,
        inputStream: InputStream
    ): FileDTO {
        val extension = extractExtension(fileName)
        val uniqueFileName = generateUniqueName(extension)
        val targetPath = storageDir.resolve(uniqueFileName)

        val size = withContext(Dispatchers.IO) {
            var totalBytes = 0L
            val buffer = ByteArray(8192)

            Files.newOutputStream(targetPath).use { output ->
                inputStream.use { input ->
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        totalBytes += bytesRead

                        if (totalBytes > config.maxFileSizeBytes) {
                            output.close()
                            Files.deleteIfExists(targetPath)
                            throw BadRequestException("file_too_large")
                        }

                        output.write(buffer, 0, bytesRead)
                    }
                }
            }
            totalBytes
        }
        // Валидация после записи (extension check)
        try {
            validateExtension(fileName)
        } catch (e: Exception) {
            withContext(Dispatchers.IO) { Files.deleteIfExists(targetPath) }
            throw e
        }

        logger.debug("File stored via stream: $targetPath ($size bytes)")
        return createFileDto(fileName, uniqueFileName, targetPath, mimeType, size)
    }

    override suspend fun load(uniqueFileName: String): ByteArray {
        val path = resolveAndValidatePath(uniqueFileName)

        return withContext(Dispatchers.IO) {
            if (!Files.exists(path)) {
                throw BadRequestException("file_does_not_exist")
            }
            Files.readAllBytes(path)
        }
    }

    override suspend fun loadAsStream(uniqueFileName: String): InputStream {
        val path = resolveAndValidatePath(uniqueFileName)
        return withContext(Dispatchers.IO) {
            if (!Files.exists(path)) {
                throw BadRequestException("file_does_not_exist")
            }
            Files.newInputStream(path)
        }
    }

    override suspend fun delete(uniqueFileName: String): Boolean {
        val path = resolveAndValidatePath(uniqueFileName)

        return withContext(Dispatchers.IO) {
            Files.deleteIfExists(path).also {
                if (it) logger.debug("File deleted: $path")
            }
        }
    }

    override suspend fun exists(uniqueFileName: String): Boolean {
        val path = resolveAndValidatePath(uniqueFileName)
        return withContext(Dispatchers.IO) { Files.exists(path) }
    }

    override suspend fun getFileInfo(uniqueFileName: String): FileDTO? {
        val path = resolveAndValidatePath(uniqueFileName)

        return withContext(Dispatchers.IO) {
            if (!Files.exists(path)) return@withContext null

            val file = path.toFile()
            FileDTO(
                originalName = uniqueFileName,
                uniqueFileName = uniqueFileName,
                fullPath = "/${config.uploadDir}/${uniqueFileName}",
                directory = config.uploadDir,
                extension = extractExtension(uniqueFileName),
                mimeType = Files.probeContentType(path) ?: "application/octet-stream",
                storedLocal = true,
                fileSizeByte = file.length()
            )
        }
    }

    override fun getStorageDir(): String {
       return storageDir.toString()
    }

    override fun getConfig(): StorageConfig = config

    // === Private Methods ===

    private fun validateFile(fileName: String, size: Long) {
        validateSize(size)
        validateExtension(fileName)
    }

    private fun validateExtension(fileName: String) {
        val extension = extractExtension(fileName).lowercase()

        if (extension in config.forbiddenExtensions) {
            throw BadRequestException("this_file_type_is_forbidden")
        }

        if (config.allowedExtensions.isNotEmpty() && extension !in config.allowedExtensions) {
            throw BadRequestException("this_file_type_is_not_allowed")
        }
    }

    private fun extractExtension(fileName: String): String {
        return fileName.substringAfterLast(".", "")
    }

    private fun validateSize(size: Long) {
        if (size > config.maxFileSizeBytes) {
            throw BadRequestException("file_too_large")
        }
    }

    private fun resolveAndValidatePath(fileName: String): Path {
        val resolved = storageDir.resolve(fileName).normalize()

        // Защита от path traversal
        if (!resolved.startsWith(storageDir)) {
            throw BadRequestException("invalid_path")
        }

        return resolved
    }

    private fun generateUniqueName(extension: String): String {
        val uuid = UUID.randomUUID().toString()
        return if (extension.isNotEmpty()) "$uuid.$extension" else uuid
    }

    private fun createFileDto(
        originalName: String,
        uniqueFileName: String,
        path: Path,
        mimeType: String,
        size: Long
    ): FileDTO {
        return FileDTO(
            originalName = originalName,
            uniqueFileName = uniqueFileName,
            fullPath = "/${config.uploadDir}/${uniqueFileName}",
            directory = config.uploadDir,
            extension = extractExtension(originalName),
            mimeType = mimeType,
            storedLocal = true,
            fileSizeByte = size
        )
    }
}