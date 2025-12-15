package kz.idl.domain.exception

import kz.idl.domain.localization.LocalizedMessage

class BadRequestException(
    messageKey: String = "error.bad_request",
    vararg args: Any
) : ApiException(400, 400, LocalizedMessage(messageKey, args))