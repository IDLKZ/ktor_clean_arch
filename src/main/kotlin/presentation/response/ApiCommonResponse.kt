package kz.idl.presentation.response

import kotlinx.serialization.Serializable

@Serializable
data class ApiCommonResponse<T>(
    val message: String?,
    val data: T,
    val code: Int?,
    val error:ApiResponseError?
)

@Serializable data class ApiResponseError(
    val innerCode: Int,
    val message: String?,
    val detail: String?,
    val errorTrace: String?,
    val validationError: Map<String, String>?
)

@Serializable
data class EmptyData(val empty: Boolean = true)