package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.server.config.Config
import de.hsfl.budgetBinder.server.models.Categories
import de.hsfl.budgetBinder.server.models.Entries
import de.hsfl.budgetBinder.server.models.Users
import de.hsfl.budgetBinder.server.repository.UnauthorizedException
import de.hsfl.budgetBinder.server.routes.*
import de.hsfl.budgetBinder.server.services.*
import de.hsfl.budgetBinder.server.services.implementations.CategoryServiceImpl
import de.hsfl.budgetBinder.server.services.implementations.EntryServiceImpl
import de.hsfl.budgetBinder.server.services.implementations.UserServiceImpl
import de.hsfl.budgetBinder.server.services.interfaces.CategoryService
import de.hsfl.budgetBinder.server.services.interfaces.EntryService
import de.hsfl.budgetBinder.server.services.interfaces.UserService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.forwardedheaders.*
import io.ktor.server.plugins.statuspages.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.bindEagerSingleton
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import org.kodein.di.ktor.di
import org.slf4j.event.Level
import java.sql.DriverManager

fun Application.mainModule(config: Config) {
    val url: String
    val driver: String
    when (config.dataBase.dbType) {
        Config.DBType.SQLITE -> {
            url = "jdbc:sqlite:${config.dataBase.sqlitePath}"
            driver = "org.sqlite.JDBC"

            /*
            * The url is used in the tests to not create or alter the normal database.
            * the connection must be held because exposed closes the connection to the db
            * after every transaction and if no connection is alive the memory database will be deleted
            * */
            if (url == "jdbc:sqlite:file:test?mode=memory&cache=shared") {
                DriverManager.getConnection(url)
            }
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
        SchemaUtils.create(Users, Categories, Entries)
    }

    di {
        bindEagerSingleton { config }
        bindSingleton<UserService> { UserServiceImpl() }
        bindSingleton<EntryService> { EntryServiceImpl() }
        bindSingleton<CategoryService> { CategoryServiceImpl() }
        bindSingleton { JWTService(instance()) }
    }

    install(CallLogging) {
        level = Level.INFO
        disableDefaultColors()
    }

    install(CORS) {
        config.server.frontendAddresses.forEach {
            val (scheme, hostName) = it.split("://")
            allowHost(hostName, schemes = listOf(scheme))
        }
        allowCredentials = true
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Post)
        allowNonSimpleContentTypes = true
    }

    if (config.server.forwardedHeaderSupport)
        install(XForwardedHeaders)

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
            val jwtService: JWTService by this@mainModule.closestDI().instance()
            realm = jwtService.getRealm()
            verifier(jwtService.getAccessTokenVerifier())

            validate {
                val id = it.payload.getClaim("userid").asInt()
                val tokenVersion = it.payload.getClaim("token_version").asInt()
                val userService: UserService by closestDI().instance()
                userService.getUserPrincipalByIDAndTokenVersion(id, tokenVersion)
            }
        }
    }

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            when (cause) {
                is UnauthorizedException -> {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        APIResponse<String>(ErrorModel(cause.message))
                    )
                }
                else -> {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        APIResponse<String>(ErrorModel("An Internal-Server-Error occurred. Please contact your Administrator or see the Server-Logs."))
                    )
                    throw cause
                }
            }
        }
        status(HttpStatusCode.Unauthorized) { call, status ->
            when (call.request.uri) {
                "/login" -> call.respond(
                    status,
                    APIResponse<String>(ErrorModel("Your username and/or password do not match."))
                )
                else -> {
                    val jwtService: JWTService by this@mainModule.closestDI().instance()
                    call.response.headers.append(
                        HttpHeaders.WWWAuthenticate,
                        "Bearer realm=\"${jwtService.getRealm()}\""
                    )
                    call.respond(
                        status,
                        APIResponse<String>(ErrorModel("Your accessToken is absent or does not match."))
                    )
                }
            }
        }
        status(HttpStatusCode.MethodNotAllowed) { call, status ->
            call.respond(status, APIResponse<String>(ErrorModel("The used HTTP-Method is not allowed on this Endpoint.")))
        }
    }

    // install all Modules
    baseRoutes()
    userRoutes()
    authRoutes()
    categoryRoutes()
    entryRoutes()
}
