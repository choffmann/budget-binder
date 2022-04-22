package de.hsfl.budgetBinder.server

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.server.models.Roles
import de.hsfl.budgetBinder.server.routes.authRoutes
import de.hsfl.budgetBinder.server.routes.userRoutes
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
import org.jetbrains.exposed.sql.transactions.transaction

import org.kodein.di.*
import org.kodein.di.ktor.closestDI
import org.kodein.di.ktor.di


fun Application.module() {
    install(CORS) {
        anyHost()
    }
    install(XForwardedHeaderSupport)

    install(Authentication) {
        basic("auth-basic") {
            realm = "Budget Binder Server"
            validate {
                val userService: UserService by closestDI().instance()
                userService.findUserByEmailAndPassword(it.name, it.password)
            }
        }
        basic("auth-basic-admin") {
            realm = "Budget Binder Server Admin"
            validate {
                val userService: UserService by closestDI().instance()
                val user = userService.findUserByEmailAndPassword(it.name, it.password)

                transaction {
                    if (user?.role == Roles.ADMIN) user else null
                }
            }
        }
        form("auth-form") {
            userParamName = "username"
            passwordParamName = "password"
            validate {
                val userService: UserService by closestDI().instance()
                userService.findUserByEmailAndPassword(it.name, it.password)
            }
        }

        jwt("auth-jwt") {
            val secret = System.getenv("JWT_ACCESS_SECRET")
            val issuer = System.getenv("JWT_ISSUER") ?: "http://0.0.0.0:8080/"
            val audience = System.getenv("JWT_AUDIENCE") ?: "http://0.0.0.0:8080/"
            realm = System.getenv("JWT_SECRET") ?: "Access to all"
            verifier(JWT
                .require(Algorithm.HMAC256(secret))
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaimPresence("userid")
                .build())

            validate {
                val id = it.payload.getClaim("userid").asInt()
                val userService: UserService by closestDI().instance()
                userService.findUserByID(id)
            }
        }
    }

    install(ContentNegotiation) {
        json()
    }

    install(StatusPages) {
        exception<Throwable> { cause ->
            call.respond(HttpStatusCode.InternalServerError,
                APIResponse<String>(null, "Internal Server Error", false)
            )
            throw cause
        }
    }

    di {
        bindSingleton { UserService() }
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
