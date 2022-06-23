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

class DeleteCategoryTests {

    @BeforeTest
    fun before() = customTestApplication { client ->
        client.registerUser()

        val userEntity = transaction { UserEntity.all().first() }
        val now = LocalDateTime.now()

        transaction {
            val internet = CategoryEntity.new {
                name = "Internet"
                color = TestCategories.color
                image = TestCategories.image
                budget = 50f
                created = now.minusMonths(3)
                ended = now.minusMonths(2)
                child = null
                user = userEntity
            }

            val internetPhone = CategoryEntity.new {
                name = "Internet-Phone"
                color = TestCategories.color
                image = TestCategories.image
                budget = 100f
                created = now.minusMonths(2)
                ended = null
                child = null
                user = userEntity
            }

            internet.child = internetPhone.id

            CategoryEntity.new {
                name = "Hobbies"
                color = TestCategories.color
                image = TestCategories.image
                budget = 150f
                created = now.minusMonths(2)
                ended = null
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
    fun testDeleteCategoryByIDTooHigh() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequest(HttpMethod.Patch, "/categories/5000") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("Your category was not found.")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testDeleteCategoryByIDTooLow() = customTestApplicationWithLogin { client ->
        val id = transaction { CategoryEntity.all().first().id.value }

        client.sendAuthenticatedRequest(HttpMethod.Patch, "/categories/${id - 1}") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("Your category was not found.")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testDeleteCategoryOld() = customTestApplicationWithLogin { client ->
        val id = transaction { CategoryEntity.all().first().id.value }

        client.sendAuthenticatedRequest(HttpMethod.Delete, "/categories/$id") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("you can't delete an old category.")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testDeleteCategory() = customTestApplicationWithLogin { client ->
        val id = transaction { CategoryEntity.all().first().id.value + 2 }

        client.sendAuthenticatedRequest(HttpMethod.Delete, "/categories/$id") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> =
                wrapSuccess(Category(id, "Hobbies", TestCategories.color, TestCategories.image, 150f))
            assertEquals(shouldResponse, responseBody)

            transaction {
                val categoryEntity = CategoryEntity.findById(id)
                assertNull(categoryEntity)
            }
        }
    }
}
