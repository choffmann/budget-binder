package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.server.models.Roles
import de.hsfl.budgetBinder.server.routes.authRoutes
import de.hsfl.budgetBinder.server.routes.userRoutes
import de.hsfl.budgetBinder.server.services.JWTService
import de.hsfl.budgetBinder.server.services.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.util.*
import org.kodein.di.*
import org.kodein.di.ktor.closestDI
import org.kodein.di.ktor.di
import org.slf4j.event.Level


fun Application.module() {

    di {
        bindSingleton { UserService() }
        bindSingleton { JWTService() }
    }

    install(CallLogging) {
        level = if (System.getenv("DEV") == "True") Level.DEBUG else Level.INFO
    }

    install(CORS) {
        val clientHost = System.getenv("FRONTEND_ADDRESS").split("://")
        host(clientHost[1], schemes = listOf(clientHost[0]))
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
                val user = userService.findUserByID(id)
                if (user?.tokenVersion == tokenVersion) user else null
            }
        }

        jwt("auth-jwt-admin") {
            val jwtService: JWTService by closestDI().instance()
            realm = "Access to all your stuff"
            verifier(jwtService.getAccessTokenVerifier())

            validate {
                val id = it.payload.getClaim("userid").asInt()
                val tokenVersion = it.payload.getClaim("token_version").asInt()
                val userService: UserService by closestDI().instance()
                val user = userService.findUserByID(id)
                if (user?.tokenVersion == tokenVersion && user?.role == Roles.ADMIN) user else null
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
                APIResponse("error", ErrorModel(error = true, message = "Internal Server Error"), false)
            )
            throw cause
        }
    }

    // install all Modules
    userRoutes()
    authRoutes()

    routing {
        get("/") {
            call.respondText(call.request.headers.toMap().toString())
        }
    }

    routing {
        get("/path") {
            val userService: UserService by closestDI().instance()
            val user = userService.getRandomUser()
            call.respondText(user.email)
        }
    }
}
