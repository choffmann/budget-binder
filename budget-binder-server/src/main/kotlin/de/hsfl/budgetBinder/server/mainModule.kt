package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.server.config.Config
import de.hsfl.budgetBinder.server.config.getServerConfig
import de.hsfl.budgetBinder.server.models.Categories
import de.hsfl.budgetBinder.server.models.Entries
import de.hsfl.budgetBinder.server.models.Users
import de.hsfl.budgetBinder.server.routes.authRoutes
import de.hsfl.budgetBinder.server.routes.baseRoutes
import de.hsfl.budgetBinder.server.routes.userRoutes
import de.hsfl.budgetBinder.server.services.JWTService
import de.hsfl.budgetBinder.server.services.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.serialization.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.*
import org.kodein.di.ktor.closestDI
import org.kodein.di.ktor.di
import org.slf4j.event.Level
import java.sql.DriverManager

fun Application.mainModule(serverConfig: Config? = null, configString: String? = null) {

    val config =
        serverConfig ?: configString?.let { getServerConfig(null, configString) } ?: throw Exception("Do not Reach")

    val url: String
    val driver: String
    when (config.dataBase.dbType) {
        Config.DBType.SQLITE -> {
            url = "jdbc:sqlite:${config.dataBase.sqlitePath}"
            if (url == "jdbc:sqlite:file:test?mode=memory&cache=shared") {
                DriverManager.getConnection(url)
            }

            driver = "org.sqlite.JDBC"
        }
        Config.DBType.MYSQL -> {
            url = "jdbc:mysql://${config.dataBase.serverAddress}:${config.dataBase.serverPort}/${config.dataBase.name}"
            driver = "com.mysql.cj.jdbc.Driver"
        }
        Config.DBType.POSTGRES -> {
            url = "jdbc:pgsql://${config.dataBase.serverAddress}:${config.dataBase.serverPort}/${config.dataBase.name}"
            driver = "com.impossibl.postgres.jdbc.PGDriver"
        }
    }

    Database.connect(url, driver, user = config.dataBase.user, password = config.dataBase.password)

    transaction {
        if (config.server.dev) {
            // Logging for DEV purposes
            addLogger(StdOutSqlLogger)
        }
        SchemaUtils.create(Users, Categories, Entries)
    }

    di {
        bindEagerSingleton { config }
        bindSingleton { UserService() }
        bindSingleton { JWTService(instance()) }
    }

    install(CallLogging) {
        level = if (config.server.dev) Level.DEBUG else Level.INFO
    }

    install(CORS) {
        config.server.frontendAddresses.forEach {
            val (scheme, hostName) = it.split("://")
            host(hostName, schemes = listOf(scheme))
        }
        allowCredentials = true
        header(HttpHeaders.Authorization)
        header(HttpHeaders.ContentType)
        method(HttpMethod.Put)
        method(HttpMethod.Options)
        method(HttpMethod.Patch)
        method(HttpMethod.Delete)
        method(HttpMethod.Post)
        allowNonSimpleContentTypes = true
    }

    if (config.server.forwardedHeaderSupport)
        install(XForwardedHeaderSupport)

    install(Authentication) {
        form("auth-form") {
            userParamName = "username"
            passwordParamName = "password"
            validate {
                val userService: UserService by closestDI().instance()
                userService.findUserByEmailAndPassword(it.name, it.password)
            }
        }

        jwt("auth-jwt") {
            val jwtService: JWTService by closestDI().instance()
            realm = "Access to all your stuff"
            verifier(jwtService.getAccessTokenVerifier())

            validate {
                val id = it.payload.getClaim("userid").asInt()
                val tokenVersion = it.payload.getClaim("token_version").asInt()
                val userService: UserService by closestDI().instance()
                userService.findUserByID(id)?.let { user ->
                    if (user.tokenVersion == tokenVersion) user else null
                }
            }
        }
    }

    install(ContentNegotiation) {
        json()
    }

    install(StatusPages) {
        exception<Throwable> { cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                APIResponse<String>(ErrorModel("Internal Server Error"))
            )
            throw cause
        }
    }

    // install all Modules
    baseRoutes()
    userRoutes()
    authRoutes()
}
