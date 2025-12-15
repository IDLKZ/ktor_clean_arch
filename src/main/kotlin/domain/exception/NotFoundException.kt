package kz.idl.domain.exception

import kz.idl.domain.localization.LocalizedMessage

class NotFoundException(
    messageKey: String,
    innerCode: Int = 404,
    errorCode: Int = 404,
    detail: String? = null
) : ApiException(404, innerCode, LocalizedMessage(messageKey), detail)