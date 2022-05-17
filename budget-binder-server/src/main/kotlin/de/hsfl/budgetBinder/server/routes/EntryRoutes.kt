package de.hsfl.budgetBinder.server.routes

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.server.models.UserPrincipal
import de.hsfl.budgetBinder.server.repository.parseParameterToLocalDateTime
import de.hsfl.budgetBinder.server.services.interfaces.EntryService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Route.entriesRoute() {
    route("/entries") {
        get {
            val userPrincipal: UserPrincipal = call.principal()!!
            val entryService: EntryService by closestDI().instance()

            val (error, period) = parseParameterToLocalDateTime(
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
                APIResponse(data = entryService.insertEntryForUser(userPrincipal.getUserID(), it), success = true)
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
                    } ?: APIResponse(ErrorModel("You can't change this entry"))
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
            ) {
                APIResponse(data = entryService.deleteEntry(it.id), success = true)
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