package com.barcodebite.backend.config

data class AppConfig(
    val database: DatabaseConfig,
    val jwt: JwtConfig,
    val openFoodFacts: OpenFoodFactsConfig,
) {
    companion object {
        fun fromEnvironment(): AppConfig {
            val rawDatabaseUrl = System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/barcodebite"
            val databaseUrl = when {
                rawDatabaseUrl.startsWith("postgresql://") -> "jdbc:$rawDatabaseUrl"
                else -> rawDatabaseUrl
            }
            val databaseDriver = when {
                databaseUrl.startsWith("jdbc:h2:") -> "org.h2.Driver"
                else -> "org.postgresql.Driver"
            }

            val offBaseUrl = (System.getenv("OPEN_FOOD_FACTS_URL") ?: "https://world.openfoodfacts.org/api/v2")
                .trim()
                .trimEnd('/')

            return AppConfig(
                database = DatabaseConfig(
                    url = databaseUrl,
                    user = System.getenv("DATABASE_USER") ?: "barcodebite",
                    password = System.getenv("DATABASE_PASSWORD") ?: "secret",
                    driver = databaseDriver,
                    maxPoolSize = (System.getenv("DATABASE_MAX_POOL_SIZE") ?: "8").toIntOrNull() ?: 8,
                ),
                jwt = JwtConfig(
                    secret = System.getenv("JWT_SECRET") ?: "dev-jwt-secret-change-me",
                    issuer = System.getenv("JWT_ISSUER") ?: "barcodebite.app",
                    audience = System.getenv("JWT_AUDIENCE") ?: "barcodebite-users",
                    expiresInHours = (System.getenv("JWT_EXPIRES_IN_HOURS") ?: "24").toLongOrNull() ?: 24,
                ),
                openFoodFacts = OpenFoodFactsConfig(
                    baseUrl = offBaseUrl,
                    userAgent = System.getenv("OPEN_FOOD_FACTS_USER_AGENT") ?: "BarcodeBites/1.0 (contact@barcodebite.app)",
                    timeoutMillis = (System.getenv("OPEN_FOOD_FACTS_TIMEOUT_MS") ?: "5000").toLongOrNull() ?: 5000L,
                ),
            )
        }
    }
}

data class DatabaseConfig(
    val url: String,
    val user: String,
    val password: String,
    val driver: String,
    val maxPoolSize: Int,
)

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val expiresInHours: Long,
)

data class OpenFoodFactsConfig(
    val baseUrl: String,
    val userAgent: String,
    val timeoutMillis: Long,
)
