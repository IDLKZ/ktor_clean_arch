package kz.idl.domain.service.file

import kz.idl.config.StorageConfig
import kz.idl.domain.dto.file.FileDTO
import java.io.InputStream

interface FileService {
    suspend fun store(fileName: String, mimeType: String, bytes: ByteArray): FileDTO
    suspend fun store(fileName: String, mimeType: String, inputStream: InputStream): FileDTO
    suspend fun load(uniqueFileName: String): ByteArray
    suspend fun loadAsStream(uniqueFileName: String): InputStream
    suspend fun delete(uniqueFileName: String): Boolean
    suspend fun exists(uniqueFileName: String): Boolean
    suspend fun getFileInfo(uniqueFileName: String): FileDTO?
    fun getStorageDir(): String
    fun getConfig(): StorageConfig
}