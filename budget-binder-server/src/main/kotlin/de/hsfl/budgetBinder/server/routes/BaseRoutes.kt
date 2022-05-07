package de.hsfl.budgetBinder.server.routes

import de.hsfl.budgetBinder.server.services.UserService
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.html.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.baseRoutes() {
    routing {
        get("/") {
            call.respondText(call.request.headers.toMap().toString())
        }
        get("/favicon.ico") {
            val classLoader = javaClass.classLoader
            val inputStream = classLoader.getResourceAsStream("ico/BudgetBinderRounded.ico")!!
            call.respondBytes(contentType = ContentType.defaultForFileExtension("ico")) {
                withContext(Dispatchers.IO) {
                    inputStream.readAllBytes()
                }
            }
        }
        get("/openapi.json") {
            val classLoader = javaClass.classLoader
            val inputStream = classLoader.getResourceAsStream("openapi.json")!!
            call.respondBytes(contentType = ContentType.defaultForFileExtension("json")) {
                withContext(Dispatchers.IO) {
                    inputStream.readAllBytes()
                }
            }
        }
        get("/docs") {
            call.respondHtml {
                head {
                    meta(charset = "utf-8")
                    meta("viewport", "width=device-width, initial-scale=1")
                    meta("description", "SwaggerIU")
                    title("SwaggerUI")
                    link(href = "https://unpkg.com/swagger-ui-dist@4.5.0/swagger-ui.css", rel = "stylesheet")
                    link(href = "/favicon.ico", rel = "icon", type = "image/x-icon")
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