This file provides guidance to  Code (code) when working with code in this repository.

## Project Overview

This is a Ktor-based backend application using Clean Architecture with Kotlin. The project implements a role-based permission system with soft delete support.

**Tech Stack:**
- Ktor 3.0.1 (Netty server)
- Kotlin 2.2.21 (JVM 21)
- Exposed ORM with PostgreSQL
- Flyway for database migrations
- Koin for dependency injection
- Hibernate Validator for DTO validation
- HikariCP for connection pooling
- Multi-language support (RU, KK, EN)

## Build & Run Commands

**Start the application:**
```bash
./gradlew run
```

**Build the project:**
```bash
./gradlew build
```

**Run database migrations:**
```bash
./gradlew flywayMigrate
```

**Clean migrations (baseline):**
```bash
./gradlew flywayBaseline
```

**Validate migrations:**
```bash
./gradlew flywayValidate
```

**Migration info:**
```bash
./gradlew flywayInfo
```

## Architecture

### Clean Architecture Layers

The codebase follows Clean Architecture with clear separation:

1. **Domain Layer** (`domain/`)
    - `dto/` - Data transfer objects with validation annotations
    - `entity/` - Domain entities
    - `exception/` - Custom exceptions extending `ApiException`
    - `filters/` - Filter abstractions (`BaseFilter`, `BasePaginationFilter`)
    - `localization/` - Internationalization support (`LocalizedMessage`)
    - `mapper/` - Entity to DTO mapping functions (extension functions)
    - `repository/` - Repository interfaces (contracts)
    - `usecase/` - Business logic (one class per operation)

2. **Infrastructure Layer** (`infrastructure/`)
    - `repository/` - Repository implementations extending `BaseRepositoryImpl`
    - `filter/` - Concrete filter implementations
    - `plugin/` - Ktor plugins (ExceptionHandler, LocalizationPlugin)

3. **Data Layer** (`data/`)
    - `database/table/` - Exposed table definitions
    - `database/factory/` - Database and Flyway factory classes

4. **Presentation Layer** (`presentation/`)
    - `http/` - Controller classes with routing logic
    - `response/` - Response helper and common response structures

5. **DI Layer** (`di/`)
    - Koin modules: `dataModule`, `repositoryModule`, `useCaseModule`, `validationModule`

### Key Architectural Patterns

**Base Repository Pattern:**
- All repositories extend `BaseRepository<T: LongIdTable>` interface
- Implementations extend `BaseRepositoryImpl<T>` which provides:
    - CRUD operations with mapper functions
    - Pagination support via `BasePaginationFilter`
    - Soft delete support (two strategies: `deletedAt` timestamp or `isDeleted` boolean)
    - Bulk operations (create, update, delete, restore)
    - Query building with joins via `baseJoinQuery()` override
    - Filter-based querying via `BaseFilter`

**Soft Delete Tables:**
- Tables extend either `SoftDeleteAtTable` (uses `deletedAt: LocalDateTime?`) or `SoftIsDeleteTable` (uses `isDeleted: Boolean`)
- Both extend `BasicLongTable` which provides `createdAt` and `updatedAt` timestamps
- Repository methods accept `showDeleted` parameter to include/exclude soft-deleted records
- Delete operations accept `hardDelete` parameter to permanently delete records

**Use Case Pattern:**
- Each operation is a separate class with `operator fun invoke()`
- Use cases handle validation, business logic, and repository calls
- DTO validation uses Hibernate Validator injected via Koin
- Throw domain exceptions (`ConflictException`, `ValidationException`, etc.)

**Filter Pattern:**
- Filters implement query building via `buildConditions()` returning `Op<Boolean>?`
- `BaseFilter<T>` provides ordering and join control
- `BasePaginationFilter<T>` adds pagination with `validPage` and `validPerPage`
- Filters can be constructed from query parameters via static `fromParameters()` methods

**Controller Pattern:**
- Controllers are Koin components that inject use cases
- Register routes via `register(Route, routeName)` function
- Use `ResponseHelper.success()` and `ResponseHelper.created()` for responses
- Extract and validate parameters from `ApplicationCall`

**Exception Handling:**
- All domain exceptions extend `ApiException` with `statusCode` and `innerCode`
- Custom exceptions: `BadRequestException`, `NotFoundException`, `ConflictException`, `ValidationException`, `UnauthorizedException`, `ForbiddenException`, `InternalServerException`
- Global handler in `ExceptionHandler.kt` returns structured `ApiCommonResponse`
- Exceptions support `LocalizedMessage` with i18n keys

**Localization:**
- `LocalizationPlugin` extracts locale from `Accept-Language` header
- Supported locales: RU (default), KK, EN via `SupportedLocale` enum
- Messages in `src/main/resources/i18n/messages_{locale}.properties`
- Use `LocalizedMessage(key, params)` for messages and exceptions

## Database Configuration

**Connection details:**
- Default: `jdbc:postgresql://localhost:5432/ktor_back`
- Username: `postgres` / Password: `root`
- Configured in `src/main/resources/application.yaml`
- HikariCP pool settings: max 10, min 2 connections

**Migration location:**
- `src/main/resources/db/migration/`
- Naming: `V{number}__{description}.sql`

**Important:** Migrations run automatically on application startup via `FlywayMigration.run()` in `Application.module()`.

## Development Workflow

**Adding a new entity:**
1. Create table in `data/database/table/` extending `SoftDeleteAtTable` or `SoftIsDeleteTable`
2. Create migration in `src/main/resources/db/migration/`
3. Create DTO in `domain/dto/` with validation annotations
4. Create repository interface in `domain/repository/`
5. Create repository implementation in `infrastructure/repository/` extending `BaseRepositoryImpl`
6. Add repository to `repositoryModule` in `di/RepoModule.kt`
7. Create mapper extension functions in `domain/mapper/`
8. Create use cases in `domain/usecase/` (one per operation)
9. Add use cases to `useCaseModule` in `di/UseCaseModule.kt`
10. Create filter in `infrastructure/filter/`
11. Create controller in `presentation/http/`
12. Register controller in `Routing.kt`

**Validation:**
- Use Jakarta Validation annotations on DTOs (`@NotBlank`, `@Size`, `@Pattern`, etc.)
- Validator is injected into use cases from `validationModule`
- Call `validator.validate(dto)` and throw `ValidationException` if violations exist

**Response Structure:**
All API responses follow `ApiCommonResponse<T>` structure:
```kotlin
{
  "message": "Success message",
  "data": { ... },
  "code": 200,
  "error": null  // or ApiResponseError on failure
}
```

**Mapper Pattern:**
- Create extension functions on `ResultRow`: `fun ResultRow.toEntityDto(): EntityDto`
- Pass mapper as trailing lambda to repository methods

## Configuration Files

- `application.yaml` - Ktor and database configuration
- `logback.xml` - Logging configuration
- `build.gradle.kts` - Dependencies and build config
- `gradle.properties` - Gradle properties

## Entry Point

Application starts in `Application.kt`:
1. Localization plugin
2. Serialization plugin
3. Exception handler
4. Flyway migrations (blocking)
5. Koin DI setup
6. Route configuration