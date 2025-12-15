package kz.idl.domain.exception

import kz.idl.domain.localization.LocalizedMessage

class ValidationException(
    messageKey: String,
    errors: Map<String, LocalizedMessage>,
    statusCode: Int = 422,
    innerCode: Int = 422,
) : ApiException(statusCode, innerCode, LocalizedMessage(messageKey), validationError = errors)