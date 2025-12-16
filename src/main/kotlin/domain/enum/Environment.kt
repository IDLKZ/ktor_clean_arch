package domain.enum

enum class Environment(val value: String) {
    DEV("dev"),
    STAGE("stage"),
    PROD("prod");

    companion object {
        fun fromString(value: String): Environment {
            return entries.find { it.value.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown environment: $value")
        }
    }

    fun isDev(): Boolean = this == DEV
    fun isStage(): Boolean = this == STAGE
    fun isProd(): Boolean = this == PROD
}
