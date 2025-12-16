package kz.idl.domain.dto.file

import kotlinx.serialization.Serializable

@Serializable
data class FileDTO (
    val originalName: String,
    val uniqueFileName: String,
    val fullPath: String,
    val directory: String,
    val extension: String,
    val mimeType: String,
    val storedLocal: Boolean,
    val fileSizeByte: Long
)