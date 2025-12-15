val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"
    id("io.ktor.plugin") version "3.3.2"
    id("org.flywaydb.flyway") version "11.0.0"
}

group = "kz.idl"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}


val ktorVersion = "3.0.1"
val kotlinVersion = "2.0.21"
val logbackVersion = "1.5.12"

val koinVersion = "4.0.0"
val postgresVersion = "42.7.4"
val redisVersion = "5.2.0"
val micrometerVersion = "1.14.1"
val opentelemetryVersion = "1.44.1"
val HickariCpVersion = "7.0.2"

dependencies {
    // Ktor Core
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")

    // HTTP Plugins
    implementation("io.ktor:ktor-server-default-headers-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cors-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-compression-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-conditional-headers-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-caching-headers-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-http-redirect-jvm:3.2.1")

    // Content Negotiation & Serialization
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // OpenAPI & Swagger
    implementation("io.ktor:ktor-server-openapi:$ktorVersion")
    implementation("io.ktor:ktor-server-swagger-jvm:$ktorVersion")

    // Authentication & Sessions
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions-jvm:$ktorVersion")

    // Routing
    implementation("io.ktor:ktor-server-auto-head-response-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-resources:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-request-validation:$ktorVersion")

    // Monitoring & Metrics
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-id-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-metrics-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-metrics-micrometer-jvm:$ktorVersion")

    // Micrometer
    implementation("io.micrometer:micrometer-registry-prometheus:$micrometerVersion")

    // OpenTelemetry
    implementation("io.opentelemetry:opentelemetry-api:$opentelemetryVersion")
    implementation("io.opentelemetry:opentelemetry-sdk:$opentelemetryVersion")

    // WebSockets
    implementation("io.ktor:ktor-server-websockets-jvm:$ktorVersion")

    // Templating
    implementation("io.ktor:ktor-server-html-builder-jvm:$ktorVersion")

    // Rate Limiting
    implementation("io.ktor:ktor-server-rate-limit:$ktorVersion")

    // Database - PostgreSQL & Exposed
    implementation("org.postgresql:postgresql:$postgresVersion")
    //Exposed
    implementation("org.jetbrains.exposed:exposed-core:1.0.0-rc-4")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.0.0-rc-4")
    implementation("org.jetbrains.exposed:exposed-json:1.0.0-rc-4")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:1.0.0-rc-4")

    //HickariCp
    implementation("com.zaxxer:HikariCP:$HickariCpVersion")

    //FlyWay Migration
    implementation("org.flywaydb:flyway-core:11.18.0")
    implementation("org.flywaydb:flyway-database-postgresql:11.18.0")

    // Redis Cache
    implementation("redis.clients:jedis:$redisVersion")

    // Dependency Injection - Koin
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // Configuration
    implementation("io.ktor:ktor-server-config-yaml:$ktorVersion")

    // Validation
    implementation("commons-validator:commons-validator:1.9.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    // DateTime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

    //DTO Validation
    implementation("jakarta.validation:jakarta.validation-api:4.0.0-M1")
    implementation("org.hibernate.validator:hibernate-validator:9.1.0.Final")
    implementation("jakarta.validation:jakarta.validation-api:4.0.0-M1")

    // Testing
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("io.insert-koin:koin-test:$koinVersion")
    testImplementation("io.mockk:mockk:1.13.13")
}

kotlin {
    jvmToolchain(21)
}

// Конфигурация Flyway для запуска миграций через Gradle
flyway {
    url = "jdbc:postgresql://localhost:5432/ktor_back"
    user = "postgres"
    password = "root"
    locations = arrayOf("classpath:db/migration")
    baselineOnMigrate = true
    validateOnMigrate = true
}
