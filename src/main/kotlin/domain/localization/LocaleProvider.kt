// domain/localization/LocaleProvider.kt
package kz.idl.domain.localization

import java.util.*

enum class SupportedLocale(val code: String, val locale: Locale) {
    RU("ru", Locale.of("ru")),
    KK("kk", Locale.of("kk")),
    EN("en", Locale.ENGLISH);

    companion object {
        fun fromCode(code: String?): SupportedLocale {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: RU
        }

        fun fromHeader(acceptLanguage: String?): SupportedLocale {
            if (acceptLanguage.isNullOrBlank()) return RU
            val primaryLang = acceptLanguage.split(",").firstOrNull()?.split("-")?.firstOrNull()?.trim()
            return fromCode(primaryLang)
        }
    }
}

object LocaleProvider {
    private val bundles = mutableMapOf<SupportedLocale, ResourceBundle>()

    init {
        SupportedLocale.entries.forEach { locale ->
            bundles[locale] = ResourceBundle.getBundle("i18n.messages", locale.locale)
        }
    }

    fun getMessage(key: String, locale: SupportedLocale = SupportedLocale.RU): String {
        return try {
            bundles[locale]?.getString(key) ?: key
        } catch (e: MissingResourceException) {
            key
        }
    }

    fun getMessage(key: String, locale: SupportedLocale, vararg args: Any): String {
        val template = getMessage(key, locale)
        return template.format(*args)
    }
}