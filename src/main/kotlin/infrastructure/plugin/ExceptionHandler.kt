package kz.idl.infrastructure.plugin

import kz.idl.domain.exception.ApiException
import kz.idl.presentation.response.ApiCommonResponse
import kz.idl.presentation.response.ApiResponseError

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kz.idl.infrastructure.plugins.locale
import kz.idl.presentation.response.EmptyData

fun Application.configureExceptionHandler() {
    val isDev:Boolean = true;
    install(StatusPages) {
        exception<ApiException> { call, cause ->
            val locale = call.locale
            call.respond(
                HttpStatusCode.fromValue(cause.statusCode),
                ApiCommonResponse(
                    message = null,
                    data = EmptyData(),
                    code = cause.statusCode,
                    error = ApiResponseError(
                        innerCode = cause.innerCode,
                        message = cause.getResolvedMessage(locale),
                        detail = cause.detail,
                        errorTrace = if (isDev) cause.stackTraceToString() else null,
                        validationError = cause.getResolvedValidationErrors(locale),
                    )
                )
            )
        }
    }
}