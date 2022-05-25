package de.hsfl.budgetBinder.server.routes

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.server.models.UserPrincipal
import de.hsfl.budgetBinder.server.repository.parseParameterToLocalDateTimeOrErrorMessage
import de.hsfl.budgetBinder.server.services.interfaces.CategoryService
import de.hsfl.budgetBinder.server.services.interfaces.EntryService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Route.categoriesRoute() {
    route("/categories") {
        get {
            val userPrincipal: UserPrincipal = call.principal()!!
            val categoryService: CategoryService by closestDI().instance()

            val (error, period) = parseParameterToLocalDateTimeOrErrorMessage(
                call.request.queryParameters["current"].toBoolean(),
                call.request.queryParameters["period"]
            )

            error?.let {
                call.respond(APIResponse<List<Category>>(ErrorModel(error)))
            } ?: run {
                call.respond(
                    APIResponse(
                        data = categoryService.getCategoriesByPeriod(userPrincipal.getUserID(), period),
                        success = true
                    )
                )
            }
        }

        post {
            val userPrincipal: UserPrincipal = call.principal()!!
            val categoryService: CategoryService by closestDI().instance()

            val response = call.receiveOrNull<Category.In>()?.let {
                APIResponse(data = categoryService.createCategory(userPrincipal.getUserID(), it), success = true)
            } ?: APIResponse(ErrorModel("The object you provided it not in the right format."))
            call.respond(response)
        }
    }
}

fun Route.categoryByIdRoute() {
    route("/categories/{id}") {
        get {
            val userPrincipal: UserPrincipal = call.principal()!!
            val categoryService: CategoryService by closestDI().instance()

            val response = categoryService.getByIDOrErrorResponse(
                userPrincipal.getUserID(),
                call.parameters["id"]?.toIntOrNull()
            ) {
                APIResponse(data = it, success = true)
            }
            call.respond(response)
        }

        patch {
            val userPrincipal: UserPrincipal = call.principal()!!
            val categoryService: CategoryService by closestDI().instance()

            val response = categoryService.getByIDOrErrorResponse(
                userPrincipal.getUserID(),
                call.parameters["id"]?.toIntOrNull()
            ) { category ->
                call.receiveOrNull<Category.Patch>()?.let { changeCategory ->
                    categoryService.changeCategory(userPrincipal.getUserID(), category.id, changeCategory)?.let {
                        APIResponse(data = it, success = true)
                    } ?: APIResponse(ErrorModel("you can't change an old category."))
                } ?: APIResponse(ErrorModel("The object you provided it not in the right format."))
            }
            call.respond(response)
        }

        delete {
            val userPrincipal: UserPrincipal = call.principal()!!
            val categoryService: CategoryService by closestDI().instance()

            val response = categoryService.getByIDOrErrorResponse(
                userPrincipal.getUserID(),
                call.parameters["id"]?.toIntOrNull()
            ) { category ->
                categoryService.deleteCategory(category.id)?.let {
                    APIResponse(data = it, success = true)
                } ?: APIResponse(ErrorModel("you can't delete an old category."))
            }
            call.respond(response)
        }

        get("/entries") {
            val userPrincipal: UserPrincipal = call.principal()!!
            val entryService: EntryService by closestDI().instance()

            call.respond(
                entryService.getAllEntriesForCategoryIdParam(
                    userPrincipal.getUserID(),
                    call.parameters["id"]
                )
            )
        }
    }
}

fun Application.categoryRoutes() {
    routing {
        authenticate("auth-jwt") {
            categoriesRoute()
            categoryByIdRoute()
        }
    }
}
