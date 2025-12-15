package kz.idl.domain.exception

import kz.idl.domain.localization.LocalizedMessage
import kz.idl.domain.localization.SupportedLocale

open class ApiException(
    val statusCode: Int,
    val innerCode: Int,
    val localizedMessage: LocalizedMessage,
    val detail: String? = null,
    val validationError: Map<String, LocalizedMessage>? = null
) : RuntimeException(localizedMessage.key){
    fun getResolvedMessage(locale: SupportedLocale): String {
        return localizedMessage.resolve(locale)
    }

    fun getResolvedValidationErrors(locale: SupportedLocale): Map<String, String>? {
        return validationError?.mapValues { it.value.resolve(locale) }
    }
}