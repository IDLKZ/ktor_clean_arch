package kz.idl.domain.exception

import kz.idl.domain.localization.LocalizedMessage

class UnauthorizedException(
    messageKey: String,
    innerCode: Int = 401,
    statusCode: Int = 401,
    detail: String? = null
) : ApiException(statusCode, innerCode, LocalizedMessage(messageKey), detail)