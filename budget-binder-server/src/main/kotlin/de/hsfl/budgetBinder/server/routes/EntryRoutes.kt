package de.hsfl.budgetBinder.server.routes

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.server.models.UserPrincipal
import de.hsfl.budgetBinder.server.repository.parseParameterToLocalDateTimeOrErrorMessage
import de.hsfl.budgetBinder.server.services.interfaces.EntryService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Route.entriesRoute() {
    route("/entries") {
        get {
            val userPrincipal: UserPrincipal = call.principal()!!
            val entryService: EntryService by closestDI().instance()

            val (error, period) = parseParameterToLocalDateTimeOrErrorMessage(
                call.request.queryParameters["current"].toBoolean(),
                call.request.queryParameters["period"]
            )

            error?.let {
                call.respond(APIResponse<List<Category>>(ErrorModel(error)))
            } ?: run {
                call.respond(
                    APIResponse(
                        data = entryService.getEntriesByPeriod(userPrincipal.getUserID(), period),
                        success = true
                    )
                )
            }
        }

        post {
            val userPrincipal: UserPrincipal = call.principal()!!
            val entryService: EntryService by closestDI().instance()

            val response = call.receiveOrNull<Entry.In>()?.let {
                APIResponse(data = entryService.createEntry(userPrincipal.getUserID(), it), success = true)
            } ?: APIResponse(ErrorModel("not the right Parameters provided"))
            call.respond(response)
        }
    }
}

fun Route.entryByIdRoute() {
    route("/entries/{id}") {
        get {
            val userPrincipal: UserPrincipal = call.principal()!!
            val entryService: EntryService by closestDI().instance()

            val response = entryService.getByIDOrErrorResponse(
                userPrincipal.getUserID(),
                call.parameters["id"]?.toIntOrNull()
            ) {
                APIResponse(data = it, success = true)
            }
            call.respond(response)
        }

        patch {
            val userPrincipal: UserPrincipal = call.principal()!!
            val entryService: EntryService by closestDI().instance()

            val response = entryService.getByIDOrErrorResponse(
                userPrincipal.getUserID(),
                call.parameters["id"]?.toIntOrNull()
            ) { entry ->
                call.receiveOrNull<Entry.Patch>()?.let { changeEntry ->
                    entryService.changeEntry(userPrincipal.getUserID(), entry.id, changeEntry)?.let {
                        APIResponse(data = it, success = true)
                    } ?: APIResponse(ErrorModel("you can't change this Entry"))
                } ?: APIResponse(ErrorModel("not the right Parameters provided"))
            }
            call.respond(response)
        }

        delete {
            val userPrincipal: UserPrincipal = call.principal()!!
            val entryService: EntryService by closestDI().instance()

            val response = entryService.getByIDOrErrorResponse(
                userPrincipal.getUserID(),
                call.parameters["id"]?.toIntOrNull()
            ) { entry ->
                entryService.deleteEntry(entry.id)?.let {
                    APIResponse(data = it, success = true)
                } ?: APIResponse(ErrorModel("you can't delete this Entry"))
            }
            call.respond(response)
        }
    }
}

fun Application.entryRoutes() {
    routing {
        authenticate("auth-jwt") {
            entriesRoute()
            entryByIdRoute()
        }
    }
}
