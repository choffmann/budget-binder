package de.hsfl.budgetBinder.server.categories

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.server.models.CategoryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.utils.*
import io.ktor.client.call.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import kotlin.test.*

class GetCategoryByIDTests {
    @BeforeTest
    fun before() = customTestApplication { client ->
        client.registerUser()

        val userEntity = transaction { UserEntity.all().first() }
        val now = LocalDateTime.now()

        transaction {
            CategoryEntity.new {
                name = "test"
                color = TestCategories.color
                image = TestCategories.image
                budget = 10f
                created = now.minusMonths(5)
                ended = now.minusMonths(3)
                child = null
                user = userEntity
            }
        }
    }

    @AfterTest
    fun after() = transaction {
        UserEntity.all().forEach { it.delete() }
        CategoryEntity.all().forEach { it.delete() }
    }

    @Test
    fun testGetCategoryByIDTooHigh() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequest(HttpMethod.Get, "/categories/5000") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("Your category was not found.")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testGetCategoryByIDTooLow() = customTestApplicationWithLogin { client ->
        val id = transaction { CategoryEntity.all().first().id.value }

        client.sendAuthenticatedRequest(HttpMethod.Get, "/categories/${id - 1}") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("Your category was not found.")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testGetCategoryByID() = customTestApplicationWithLogin { client ->
        val id = transaction { CategoryEntity.all().first().id.value }

        client.sendAuthenticatedRequest(HttpMethod.Get, "/categories/${id}") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse = wrapSuccess(Category(id, "test", TestCategories.color, TestCategories.image, 10f))
            assertEquals(shouldResponse, responseBody)
        }
    }
}
