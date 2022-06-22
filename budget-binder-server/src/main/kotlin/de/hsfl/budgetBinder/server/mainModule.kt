package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.server.config.Config
import de.hsfl.budgetBinder.server.models.Categories
import de.hsfl.budgetBinder.server.models.Entries
import de.hsfl.budgetBinder.server.models.Users
import de.hsfl.budgetBinder.server.utils.UnauthorizedException
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
import io.ktor.server.response.*
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
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
    val url: String = when (config.database.dbType) {
        Config.DBType.SQLITE -> {
            val url = "jdbc:sqlite:${config.database.sqlitePath}"
            /*
            * The url is used in the tests to not create or alter the normal database.
            * the connection must be held because exposed closes the connection to the db
            * after every transaction and if no connection is alive the memory database will be deleted
            * */
            if (url == "jdbc:sqlite:file:test?mode=memory&cache=shared") {
                DriverManager.getConnection(url)
            }
            url
        }
        Config.DBType.MYSQL -> "jdbc:mysql://${config.database.serverAddress}:${config.database.serverPort}/${config.database.name}"
        Config.DBType.POSTGRES -> "jdbc:postgresql://${config.database.serverAddress}:${config.database.serverPort}/${config.database.name}"
    }

    Database.connect(url, user = config.database.user, password = config.database.password)

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

            challenge {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    APIResponse<String>(
                        ErrorModel(
                            "Your username and/or password do not match.",
                            HttpStatusCode.Unauthorized.value
                        )
                    )
                )
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

            challenge { defaultScheme, realm ->
                call.response.headers.append(HttpHeaders.WWWAuthenticate, "$defaultScheme realm=$realm")
                call.respond(
                    HttpStatusCode.Unauthorized,
                    APIResponse<String>(
                        ErrorModel(
                            "Your accessToken is absent or does not match.",
                            HttpStatusCode.Unauthorized.value
                        )
                    )
                )
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
                        APIResponse<String>(ErrorModel(cause.message, HttpStatusCode.Unauthorized.value))
                    )
                }
                else -> {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        APIResponse<String>(
                            ErrorModel(
                                "An Internal-Server-Error occurred. Please contact your Administrator or see the Server-Logs.",
                                HttpStatusCode.InternalServerError.value
                            )
                        )
                    )
                    throw cause
                }
            }
        }
        status(HttpStatusCode.MethodNotAllowed) { call, status ->
            call.respond(
                status,
                APIResponse<String>(ErrorModel("The used HTTP-Method is not allowed on this Endpoint.", status.value))
            )
        }
    }

    // install all Modules
    baseRoutes()
    userRoutes()
    authRoutes()
    categoryRoutes()
    entryRoutes()
}
