package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.server.routes.authRoutes
import de.hsfl.budgetBinder.server.routes.userRoutes
import de.hsfl.budgetBinder.server.services.JWTService
import de.hsfl.budgetBinder.server.services.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.util.*
import kotlinx.html.*
import org.kodein.di.*
import org.kodein.di.ktor.closestDI
import org.kodein.di.ktor.di
import org.slf4j.event.Level
import java.io.File
import java.net.URL


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
                userService.findUserByID(id)?.let { user ->
                    if (user.active && user.tokenVersion == tokenVersion) user else null
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
    userRoutes()
    authRoutes()

    routing {
        get("/") {
            call.respondText(call.request.headers.toMap().toString())
        }
        get("/openapi.json") {
            val classLoader = javaClass.classLoader
            val url: URL = classLoader.getResource("openapi.json")!!
            val file = File(url.toURI())
            call.respondFile(file)
        }
        get("/docs") {
            call.respondHtml {
                head {
                    meta(charset = "utf-8")
                    meta("viewport", "width=device-width, initial-scale=1")
                    meta("description", "SwaggerIU")
                    title("SwaggerUI")
                    link(href = "https://unpkg.com/swagger-ui-dist@4.5.0/swagger-ui.css", rel = "stylesheet")
                }
                body {
                    div {
                        id = "swagger-ui"
                    }
                    unsafe {
                        +"""
                            <script src="https://unpkg.com/swagger-ui-dist@4.5.0/swagger-ui-bundle.js" crossorigin></script>
                            <script>
                                window.onload = () => {
                                    window.ui = SwaggerUIBundle({
                                        url: '/openapi.json',
                                        dom_id: '#swagger-ui',
                                    });
                                };
                            </script>
                        """.trimIndent()
                    }
                }
            }
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
