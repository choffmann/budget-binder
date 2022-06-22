package de.hsfl.budgetBinder.server.routes

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import kotlinx.html.*
import java.io.File

fun Application.baseRoutes() {
    routing {
        static("/") {
            staticRootFolder = File("public")
            files(".")
            default("index.html")
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
                    script {
                        src = "https://unpkg.com/swagger-ui-dist@4.5.0/swagger-ui-bundle.js"
                        attributes["crossorigin"] = "crossorigin"
                    }
                    script {
                        unsafe {
                            +"""
                                window.onload = () => {
                                    window.ui = SwaggerUIBundle({
                                        url: '/openapi.json',
                                        dom_id: '#swagger-ui',
                                    });
                                };
                            """.trimIndent()
                        }
                    }
                }
            }
        }
    }
}
