package de.hsfl.budgetBinder.server.categories

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.server.models.CategoryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.utils.*
import io.ktor.client.call.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

class CreateCategoryTests {
    @BeforeTest
    fun before() = customTestApplication { client ->
        client.registerUser()
    }

    @AfterTest
    fun after() = transaction {
        UserEntity.all().forEach { it.delete() }
        CategoryEntity.all().forEach { it.delete() }
    }

    @Test
    fun testCreateCategory() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequestWithBody(
            HttpMethod.Post, "/categories",
            Category.In("Test", TestCategories.color, TestCategories.image, 50f)
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)

            val responseBody: APIResponse<Category> = response.body()
            val id = transaction {
                val categoryEntity = CategoryEntity.all().last()

                assertEquals("Test", categoryEntity.name)
                assertEquals(TestCategories.color, categoryEntity.color)
                assertEquals(TestCategories.image, categoryEntity.image)
                assertEquals(50f, categoryEntity.budget)

                categoryEntity.id.value
            }
            val shouldResponse = wrapSuccess(Category(id, "Test", TestCategories.color, TestCategories.image, 50f))
            assertEquals(shouldResponse, responseBody)
        }
    }
}
