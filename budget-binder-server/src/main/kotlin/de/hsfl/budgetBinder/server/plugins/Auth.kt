package de.hsfl.budgetBinder.server.plugins

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.server.services.JWTService
import de.hsfl.budgetBinder.server.services.interfaces.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.configureAuth() {
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
            val jwtService: JWTService by this@configureAuth.closestDI().instance()
            realm = jwtService.getRealm()
            verifier(jwtService.accessTokenVerifier)

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
}
