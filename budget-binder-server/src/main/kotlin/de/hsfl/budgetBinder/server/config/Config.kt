package de.hsfl.budgetBinder.server.config


data class Config(val server: Server, val dataBase: DataBase, val jwt: JWT) {
    data class Server(
        val dev: Boolean,
        val ssl: Boolean,
        val host: String,
        val port: Int,
        val sslHost: String,
        val sslPort: Int,
        val keyStorePassword: String,
        val keyStorePath: String,
        val frontendAddresses: List<String>,
        val forwardedHeaderSupport: Boolean
    )

    enum class DBType {
        SQLITE,
        MYSQL,
        POSTGRES,
    }

    data class DataBase(
        val dbType: DBType,
        val sqlitePath: String,
        val serverAddress: String,
        val serverPort: String,
        val name: String,
        val user: String,
        val password: String
    )

    data class JWT(
        val accessSecret: String,
        val refreshSecret: String,
        val accessMinutes: Int,
        val refreshDays: Int,
        val realm: String,
        val issuer: String,
        val audience: String
    )
}
