package kz.idl.presentation.response

import kz.idl.domain.exception.ApiException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

object ResponseHelper {

    suspend inline fun <reified T> ApplicationCall.success(
        data: T,
        message: String? = "",
        code: Int? = 200
    ) {
        respond(HttpStatusCode.OK, ApiCommonResponse(message, data, code, null))
    }

    suspend inline fun <reified T> ApplicationCall.created(
        data: T,
        message: String? = "Created"
    ) {
        respond(HttpStatusCode.Created, ApiCommonResponse(message, data, 201, null))
    }

    suspend fun ApplicationCall.noContent() {
        respond(HttpStatusCode.NoContent)
    }

    suspend fun ApplicationCall.deleted(message: String? = "Deleted") {
        respond(HttpStatusCode.OK, ApiCommonResponse(message, EmptyData(), 200, null))
    }

    fun ApiException.toErrorResponse(locale: kz.idl.domain.localization.SupportedLocale, includeTrace: Boolean = false): ApiCommonResponse<EmptyData> {
        return ApiCommonResponse(
            message = null,
            data = EmptyData(),
            code = statusCode,
            error = ApiResponseError(
                innerCode = innerCode,
                message = getResolvedMessage(locale),
                detail = detail,
                errorTrace = if (includeTrace) stackTraceToString() else null,
                validationError = getResolvedValidationErrors(locale)
            )
        )
    }
}