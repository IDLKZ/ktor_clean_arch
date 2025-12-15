package kz.idl.domain.exception

import kz.idl.domain.localization.LocalizedMessage

class ConflictException(
    messageKey: String,
    vararg args: Any
) : ApiException(409, 409, LocalizedMessage(messageKey, args))