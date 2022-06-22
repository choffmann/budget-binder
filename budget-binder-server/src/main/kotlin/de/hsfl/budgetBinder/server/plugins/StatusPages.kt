package de.hsfl.budgetBinder.server.plugins

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.server.utils.UnauthorizedException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
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
}
