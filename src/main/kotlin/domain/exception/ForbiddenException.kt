package kz.idl.domain.exception

import kz.idl.domain.localization.LocalizedMessage

class ForbiddenException(
    messageKey: String,
    vararg args: Any,
    innerCode: Int = 403,
    errorCode: Int = 403,
    detail: String? = null
) : ApiException(403, innerCode, LocalizedMessage(messageKey,args), detail)