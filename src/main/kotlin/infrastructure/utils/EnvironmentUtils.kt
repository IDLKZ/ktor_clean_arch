package infrastructure.utils

import domain.enum.Environment
import io.ktor.server.application.*

object EnvironmentUtils {

    private var cachedEnvironment: Environment? = null

    /**
     * Get current application environment from Ktor config
     * Value is cached after first call
     */
    fun getEnvironment(applicationEnvironment: ApplicationEnvironment): Environment {
        if (cachedEnvironment != null) {
            return cachedEnvironment!!
        }

        val envValue = applicationEnvironment.config
            .propertyOrNull("ktor.deployment.environment")?.getString()
            ?: "dev"

        cachedEnvironment = Environment.fromString(envValue)
        return cachedEnvironment!!
    }

    /**
     * Get current environment or return default (DEV)
     */
    fun getEnvironmentOrDefault(applicationEnvironment: ApplicationEnvironment): Environment {
        return try {
            getEnvironment(applicationEnvironment)
        } catch (e: Exception) {
            Environment.DEV
        }
    }

    /**
     * Check if current environment is DEV
     */
    fun isDev(applicationEnvironment: ApplicationEnvironment): Boolean {
        return getEnvironment(applicationEnvironment).isDev()
    }

    /**
     * Check if current environment is STAGE
     */
    fun isStage(applicationEnvironment: ApplicationEnvironment): Boolean {
        return getEnvironment(applicationEnvironment).isStage()
    }

    /**
     * Check if current environment is PROD
     */
    fun isProd(applicationEnvironment: ApplicationEnvironment): Boolean {
        return getEnvironment(applicationEnvironment).isProd()
    }

    /**
     * Reset cached environment (useful for testing)
     */
    fun resetCache() {
        cachedEnvironment = null
    }
}
