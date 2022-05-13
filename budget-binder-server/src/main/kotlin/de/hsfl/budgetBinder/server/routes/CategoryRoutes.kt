package de.hsfl.budgetBinder.server.routes

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.server.models.UserPrincipal
import de.hsfl.budgetBinder.server.services.CategoryService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Route.categoriesRoute() {
    route("/categories") {
        get {
            val userPrincipal: UserPrincipal = call.principal()!!
            val categoryService: CategoryService by closestDI().instance()

            call.respond(
                APIResponse(data = categoryService.getAllCategories(userPrincipal.getUserID()), success = true)
            )
        }
        post {
            val userPrincipal: UserPrincipal = call.principal()!!
            val categoryService: CategoryService by closestDI().instance()

            val response = call.receiveOrNull<Category.In>()?.let {
                APIResponse(data = categoryService.insertCategoryForUser(userPrincipal.getUserID(), it), success = true)
            } ?: APIResponse(ErrorModel("not the right Parameters provided"))
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
                    APIResponse(
                        data = categoryService.changeCategory(userPrincipal.getUserID(), category.id, changeCategory),
                        success = true
                    )
                } ?: APIResponse(ErrorModel("not the right Parameters provided"))
            }
            call.respond(response)
        }

        delete {
            val userPrincipal: UserPrincipal = call.principal()!!
            val categoryService: CategoryService by closestDI().instance()

            val response = categoryService.getByIDOrErrorResponse(
                userPrincipal.getUserID(),
                call.parameters["id"]?.toIntOrNull()
            ) {
                APIResponse(data = categoryService.deleteCategory(it.id), success = true)
            }
            call.respond(response)
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