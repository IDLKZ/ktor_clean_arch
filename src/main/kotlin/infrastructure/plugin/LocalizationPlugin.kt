// infrastructure/plugins/LocalizationPlugin.kt
package kz.idl.infrastructure.plugins

import io.ktor.server.application.*
import io.ktor.util.*
import kz.idl.domain.localization.SupportedLocale

private val LocaleKey = AttributeKey<SupportedLocale>("Locale")

val ApplicationCall.locale: SupportedLocale
    get() = attributes.getOrNull(LocaleKey) ?: SupportedLocale.RU

fun Application.configureLocalization() {
    intercept(ApplicationCallPipeline.Plugins) {
        val acceptLanguage = call.request.headers["Accept-Language"]
        val locale = SupportedLocale.fromHeader(acceptLanguage)
        call.attributes.put(LocaleKey, locale)
    }
}