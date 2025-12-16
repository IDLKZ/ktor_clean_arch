package kz.idl.config

import io.ktor.server.config.ApplicationConfig

// config/StorageConfig.kt
data class StorageConfig(
    val uploadDir: String,
    val maxFileSizeBytes: Long,
    val allowedExtensions: Set<String>,
    val forbiddenExtensions: Set<String>
) {
    companion object {
        fun load(config: ApplicationConfig): StorageConfig {
            val storage = config.config("storage")

            val maxSizeMb = storage.propertyOrNull("maxFileSizeMb")?.getString()?.toLongOrNull() ?: 10

            return StorageConfig(
                uploadDir = storage.propertyOrNull("uploadDir")?.getString() ?: "uploads",
                maxFileSizeBytes = maxSizeMb * 1024 * 1024,
                allowedExtensions = storage.propertyOrNull("allowedExtensions")
                    ?.getList()
                    ?.map { it.lowercase() }
                    ?.toSet() ?: emptySet(),
                forbiddenExtensions = storage.propertyOrNull("forbiddenExtensions")
                    ?.getList()
                    ?.map { it.lowercase() }
                    ?.toSet() ?: setOf("exe", "bat", "sh", "cmd", "ps1", "jar")
            )
        }
    }
}