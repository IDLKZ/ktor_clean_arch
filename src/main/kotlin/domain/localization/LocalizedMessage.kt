// domain/localization/LocalizedMessage.kt
package kz.idl.domain.localization

data class LocalizedMessage(
    val key: String,
    val args: Array<out Any> = emptyArray()
) {
    fun resolve(locale: SupportedLocale): String {
        return LocaleProvider.getMessage(key, locale, *args)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LocalizedMessage) return false
        return key == other.key && args.contentEquals(other.args)
    }

    override fun hashCode(): Int {
        return 31 * key.hashCode() + args.contentHashCode()
    }
}