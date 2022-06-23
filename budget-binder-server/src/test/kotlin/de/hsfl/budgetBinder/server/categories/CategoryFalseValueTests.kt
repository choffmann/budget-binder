package de.hsfl.budgetBinder.server.categories

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.utils.*
import io.ktor.client.call.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

class CategoryFalseValueTests {
    @BeforeTest
    fun before() = customTestApplication { client ->
        client.registerUser()
    }

    @AfterTest
    fun after() = transaction {
        UserEntity.all().forEach { it.delete() }
    }

    @Test
    fun testCreateCategoryFalseBody() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequest(HttpMethod.Post, "/categories") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> =
                wrapFailure("The object you provided it not in the right format.")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testGetCategoriesByPeriod() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequest(HttpMethod.Get, "/categories?period=508346") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Category>> = response.body()
            val shouldResponse = wrapFailure<List<Category>>("period has not the right pattern")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testGetCategoryByIDString() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequest(HttpMethod.Get, "/categories/test") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("The ID you provided is not a number.")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testGetCategoryByIDNull() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequest(HttpMethod.Get, "/categories/null") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("The ID you provided is not a number.")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testPatchCategoryByIDString() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequest(HttpMethod.Patch, "/categories/test") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("The ID you provided is not a number.")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testPatchCategoryByIDNull() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequest(HttpMethod.Patch, "/categories/null") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("The ID you provided is not a number.")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testDeleteCategoryByIDString() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequest(HttpMethod.Delete, "/categories/test") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("The ID you provided is not a number.")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testDeleteCategoryByIDNull() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequest(HttpMethod.Delete, "/categories/null") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("The ID you provided is not a number.")
            assertEquals(shouldResponse, responseBody)
        }
    }
}
