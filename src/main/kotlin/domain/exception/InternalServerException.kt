package kz.idl.domain.exception

import kz.idl.domain.localization.LocalizedMessage

class InternalServerException(
    messageKey: String,
    innerCode: Int = 500,
    errorCode: Int = 500,
    detail: String? = null
) : ApiException(500, innerCode, LocalizedMessage(messageKey), detail)