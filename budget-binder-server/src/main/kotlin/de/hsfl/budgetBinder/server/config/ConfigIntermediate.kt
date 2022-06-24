package de.hsfl.budgetBinder.server.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import java.io.File

data class ConfigIntermediate(val server: Server? = null, val database: Database, val jwt: JWT) {
    data class Server(
        val dev: Boolean? = null,
        val ssl: Boolean? = null,
        val host: String? = null,
        val port: Int? = null,
        val sslHost: String? = null,
        val sslPort: Int? = null,
        val keyStorePassword: String? = null,
        val keyStorePath: String? = null,
        val noForwardedHeaderSupport: Boolean? = null
    )

    data class Database(
        val dbType: Config.DBType,
        val sqlitePath: String? = null,
        val serverAddress: String? = null,
        val serverPort: String? = null,
        val name: String? = null,
        val user: String? = null,
        val password: String? = null
    )

    data class JWT(
        val accessSecret: String,
        val refreshSecret: String,
        val accessMinutes: Int? = null,
        val refreshDays: Int? = null,
        val realm: String? = null,
        val issuer: String? = null,
        val audience: String? = null
    )

    companion object {
        fun createFromEnv(): ConfigIntermediate {
            val server = Server(
                System.getenv("DEV") != null,
                System.getenv("SSL") != null,
                System.getenv("HOST"),
                System.getenv("PORT")?.toIntOrNull(),
                System.getenv("SSL_HOST"),
                System.getenv("SSL_PORT")?.toIntOrNull(),
                System.getenv("KEYSTORE_PASSWORD"),
                System.getenv("KEYSTORE_PATH"),
                System.getenv("NO_FORWARDED_HEADER") != null
            )

            val dbType = when (System.getenv("DB_TYPE")) {
                "SQLITE" -> Config.DBType.SQLITE
                "MYSQL" -> Config.DBType.MYSQL
                "POSTGRES" -> Config.DBType.POSTGRES
                else -> error("No Database Type given")
            }

            val dataBase = Database(
                dbType,
                System.getenv("SQLITE_PATH"),
                System.getenv("DB_SERVER"),
                System.getenv("DB_PORT"),
                System.getenv("DB_DATABASE_NAME"),
                System.getenv("DB_USER"),
                System.getenv("DB_PASSWORD")
            )

            val accessSecret = System.getenv("JWT_ACCESS_SECRET") ?: error("No AccessTokenSecret given")
            val refreshSecret = System.getenv("JWT_REFRESH_SECRET") ?: error("No RefreshTokenSecret given")

            val jwt = JWT(
                accessSecret,
                refreshSecret,
                System.getenv("JWT_ACCESS_MINUTES")?.toIntOrNull(),
                System.getenv("JWT_REFRESH_DAYS")?.toIntOrNull(),
                System.getenv("JWT_REALM"),
                System.getenv("JWT_ISSUER"),
                System.getenv("JWT_AUDIENCE")
            )

            return ConfigIntermediate(server, dataBase, jwt)
        }

        fun createFromFile(configFile: File): ConfigIntermediate {
            return ConfigLoaderBuilder
                .default()
                .addFileSource(configFile)
                .build()
                .loadConfigOrThrow()
        }
    }
}
