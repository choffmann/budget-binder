package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.ErrorModel
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
import io.ktor.request.*
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

fun Application.module() {
    val dbType = System.getenv("DB_TYPE")
    val dbServer = System.getenv("DB_SERVER")
    val dbPort = System.getenv("DB_PORT")
    val dbDatabaseName = System.getenv("DB_DATABASE_NAME")
    val dbUser = System.getenv("DB_USER") ?: ""
    val dbPassword = System.getenv("DB_PASSWORD") ?: ""

    val url: String
    val driver: String
    when (dbType) {
        "SQLITE" -> {
            // Database.connect("jdbc:sqlite:file:test?mode=memory&cache=shared", "org.sqlite.JDBC")
            val path = (System.getenv("SQLITE_PATH") ?: (System.getProperty("user.dir") + "/data")) + "/data.db"
            url = "jdbc:sqlite:$path"
            driver = "org.sqlite.JDBC"
        }
        "MYSQL" -> {
            // Database.connect("jdbc:mysql://localhost:3306/test", driver = "com.mysql.cj.jdbc.Driver",
            //      user = "root", password = "your_pwd")
            url = "jdbc:mysql://$dbServer:$dbPort/$dbDatabaseName"
            driver = "com.mysql.cj.jdbc.Driver"
        }
        "POSTGRESQL" -> {
            url = "jdbc:pgsql://$dbServer:$dbPort/$dbDatabaseName"
            driver = "com.impossibl.postgres.jdbc.PGDriver"
        }
        else -> {
            throw Exception("No DATABASE Type given")
        }
    }

    Database.connect(url, driver, user = dbUser, password = dbPassword)


    transaction {
        if (System.getenv("DEV") == "True") {
            // Logging for DEV purposes
            addLogger(StdOutSqlLogger)
        }
        SchemaUtils.create(Users, Categories, Entries)
    }

    di {
        bindSingleton { UserService() }
        bindSingleton { JWTService() }
    }

    install(CallLogging) {
        level = if (System.getenv("DEV") == "True") Level.DEBUG else Level.INFO
    }

    install(CORS) {
        System.getenv("FRONTEND_ADDRESSES").replace(" ", "").split(",").forEach {
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

    System.getenv("NO_FORWARD_HEADER") ?: install(XForwardedHeaderSupport)

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
        status(HttpStatusCode.Unauthorized) {
            if (call.request.uri == "/login") {
                call.respond(HttpStatusCode.Unauthorized, APIResponse<String>(ErrorModel("Unauthorized")))
            } else {
                call.response.headers.append(HttpHeaders.WWWAuthenticate, "Bearer realm=\"Access to all your stuff\"")
                call.respond(HttpStatusCode.Unauthorized, APIResponse<String>(ErrorModel("Unauthorized")))
            }
        }
    }

    // install all Modules
    baseRoutes()
    userRoutes()
    authRoutes()
}
