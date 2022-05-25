package de.hsfl.budgetBinder.server.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import com.sksamuel.hoplite.yaml.YamlPropertySource
import java.io.File

data class ConfigIntermediate(val server: Server, val dataBase: DataBase, val jwt: JWT) {
    data class Server(
        val dev: Boolean?,
        val ssl: Boolean?,
        val host: String?,
        val port: Int?,
        val sslHost: String?,
        val sslPort: Int?,
        val keyStorePassword: String?,
        val keyStorePath: String?,
        val frontendAddresses: String?,
        val noForwardedHeaderSupport: Boolean?
    )

    data class DataBase(
        val dbType: Config.DBType,
        val sqlitePath: String?,
        val serverAddress: String?,
        val serverPort: String?,
        val name: String?,
        val user: String?,
        val password: String?
    )

    data class JWT(
        val accessSecret: String,
        val refreshSecret: String,
        val accessMinutes: Int?,
        val refreshDays: Int?,
        val realm: String?,
        val issuer: String?,
        val audience: String?
    )

    fun toConfig(): Config {
        val dbType = dataBase.dbType

        val sqlitePath: String
        val dbServerAddress: String
        val dbServerPort: String
        val dbName: String
        val dbUser: String
        val dbPassword: String
        if (dbType == Config.DBType.SQLITE) {
            sqlitePath = dataBase.sqlitePath ?: (System.getProperty("user.dir") + "/data/data.db")
            dbServerAddress = ""
            dbServerPort = ""
            dbName = ""
            dbUser = ""
            dbPassword = ""
        } else {
            sqlitePath = ""
            dbServerAddress = dataBase.serverAddress ?: throw Exception("No dbServerAddress specified")
            dbServerPort = dataBase.serverPort ?: throw Exception("No dbServerPort specified")
            dbName = dataBase.name ?: throw Exception("No dbDatabaseName specified")
            dbUser = dataBase.user ?: throw Exception("No dbUser specified")
            dbPassword = dataBase.password ?: throw Exception("No dbPassword specified")
        }

        val dev = server.dev ?: false
        val ssl = server.ssl ?: false

        val host = server.host ?: "0.0.0.0"
        val port = server.port ?: 8080
        val sslHost = server.sslHost ?: "0.0.0.0"
        val sslPort = server.sslPort ?: 8443

        val keyStorePassword: String
        val keyStorePath: String
        if (ssl) {
            keyStorePassword = server.keyStorePassword
                ?: if (dev) "budget-binder-server" else throw Exception("No KeystorePassword provided")
            keyStorePath = server.keyStorePath
                ?: if (dev) "data/dev_keystore.jks" else throw Exception("No KeystorePath provided")
        } else {
            keyStorePassword = ""
            keyStorePath = ""
        }

        val frontendAddresses = server.frontendAddresses?.replace(" ", "")
            ?.split(",")
            ?: listOf()

        val forwardedHeaderSupport = !(server.noForwardedHeaderSupport ?: false)

        val jwtAccessSecret = jwt.accessSecret
        val jwtRefreshSecret = jwt.refreshSecret
        val jwtAccessMinutes = jwt.accessMinutes ?: 15
        val jwtRefreshDays = jwt.refreshDays ?: 7
        val jwtRealm = jwt.realm ?: "budget-binder-server"
        val jwtIssuer = jwt.issuer ?: "http://0.0.0.0:8080/"
        val jwtAudience = jwt.audience ?: "http://0.0.0.0:8080/"

        return Config(
            dataBase = Config.DataBase(
                dbType,
                sqlitePath,
                dbServerAddress,
                dbServerPort,
                dbName,
                dbUser,
                dbPassword
            ), server = Config.Server(
                dev,
                ssl,
                host,
                port,
                sslHost,
                sslPort,
                keyStorePassword,
                keyStorePath,
                frontendAddresses,
                forwardedHeaderSupport
            ), jwt = Config.JWT(
                jwtAccessSecret,
                jwtRefreshSecret,
                jwtAccessMinutes,
                jwtRefreshDays,
                jwtRealm,
                jwtIssuer,
                jwtAudience
            )
        )
    }
}

private fun getConfigIntermediateFromEnv(): ConfigIntermediate {
    val server = ConfigIntermediate.Server(
        System.getenv("DEV") != null,
        System.getenv("SSL") != null,
        System.getenv("HOST"),
        System.getenv("PORT")?.toIntOrNull(),
        System.getenv("SSL_HOST"),
        System.getenv("SSL_PORT")?.toIntOrNull(),
        System.getenv("KEYSTORE_PASSWORD"),
        System.getenv("KEYSTORE_PATH"),
        System.getenv("FRONTEND_ADDRESSES"),
        System.getenv("NO_FORWARDED_HEADER") != null
    )

    val dbType = when (System.getenv("DB_TYPE")) {
        "SQLITE" -> Config.DBType.SQLITE
        "MYSQL" -> Config.DBType.MYSQL
        "POSTGRES" -> Config.DBType.POSTGRES
        else -> throw Exception("No Database Type given")
    }

    val dataBase = ConfigIntermediate.DataBase(
        dbType,
        System.getenv("SQLITE_PATH"),
        System.getenv("DB_SERVER"),
        System.getenv("DB_PORT"),
        System.getenv("DB_DATABASE_NAME"),
        System.getenv("DB_USER"),
        System.getenv("DB_PASSWORD")
    )

    val accessSecret = System.getenv("JWT_ACCESS_SECRET") ?: throw Exception("No AccessTokenSecret given")
    val refreshSecret = System.getenv("JWT_REFRESH_SECRET") ?: throw Exception("No RefreshTokenSecret given")

    val jwt = ConfigIntermediate.JWT(
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

private fun getConfigIntermediateFromFile(configFile: File): ConfigIntermediate {
    return ConfigLoaderBuilder
        .default()
        .addFileSource(configFile)
        .build()
        .loadConfigOrThrow()
}

private fun getConfigIntermediateFromString(configString: String): ConfigIntermediate {
    return ConfigLoaderBuilder
        .default()
        .addSource(YamlPropertySource(configString))
        .build()
        .loadConfigOrThrow()
}

fun getServerConfig(configFile: File? = null, configString: String? = null): Config {
    return configString?.let {
        getConfigIntermediateFromString(it).toConfig()
    } ?: configFile?.let {
        getConfigIntermediateFromFile(it).toConfig()
    } ?: getConfigIntermediateFromEnv().toConfig()
}
