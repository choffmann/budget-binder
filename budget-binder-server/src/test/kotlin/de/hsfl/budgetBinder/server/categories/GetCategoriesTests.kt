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

class GetCategoriesTests {
    private fun getCategoryListFromID(id: Int): List<Category> {
        return listOf(
            Category(id, "test", TestCategories.color, TestCategories.image, 10f),
            Category(id + 1, "Internet-Phone", TestCategories.color, TestCategories.image, 50f),
            Category(id + 2, "Internet-Phone", TestCategories.color, TestCategories.image, 100f),
            Category(id + 3, "Insurance", TestCategories.color, TestCategories.image, 100f),
            Category(id + 4, "Hobbies", TestCategories.color, TestCategories.image, 150f),
        )
    }

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
                name = "Insurance"
                color = TestCategories.color
                image = TestCategories.image
                budget = 100f
                created = now.minusMonths(3)
                ended = null
                child = null
                user = userEntity
            }

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
    fun testGetAllCategories() = customTestApplicationWithLogin { client ->
        val id = transaction { CategoryEntity.all().first().id.value }
        val categoryList = getCategoryListFromID(id)

        client.sendAuthenticatedRequest(HttpMethod.Get, "/categories") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Category>> = response.body()
            assert(responseBody.success)
            assertEquals(5, responseBody.data!!.size)
            val shouldResponse = wrapSuccess(categoryList)
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testGetCurrentCategories() = customTestApplicationWithLogin { client ->
        val id = transaction { CategoryEntity.all().first().id.value }
        val categoryList = getCategoryListFromID(id)

        client.sendAuthenticatedRequest(HttpMethod.Get, "/categories?current=true") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Category>> = response.body()
            assert(responseBody.success)
            assertEquals(3, responseBody.data!!.size)

            val currentList = listOf(categoryList[2], categoryList[3], categoryList[4])
            val shouldResponse = wrapSuccess(currentList)
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testGetCategoriesByPeriod() = customTestApplicationWithLogin { client ->
        val now = LocalDateTime.now()
        val id = transaction { CategoryEntity.all().first().id.value }
        val categoryList = getCategoryListFromID(id)

        client.sendAuthenticatedRequest(HttpMethod.Get, "/categories?period=${formatToPeriod(now)}") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Category>> = response.body()
            assert(responseBody.success)
            assertEquals(3, responseBody.data!!.size)

            val currentList = listOf(categoryList[2], categoryList[3], categoryList[4])
            val shouldResponse = wrapSuccess(currentList)
            assertEquals(shouldResponse, responseBody)
        }

        client.sendAuthenticatedRequest(
            HttpMethod.Get,
            "/categories?period=${formatToPeriod(now.minusMonths(2))}"
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Category>> = response.body()
            assert(responseBody.success)
            assertEquals(3, responseBody.data!!.size)

            val currentList = listOf(categoryList[2], categoryList[3], categoryList[4])
            val shouldResponse = wrapSuccess(currentList)
            assertEquals(shouldResponse, responseBody)
        }

        client.sendAuthenticatedRequest(
            HttpMethod.Get,
            "/categories?period=${formatToPeriod(now.minusMonths(3))}"
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Category>> = response.body()
            assert(responseBody.success)
            assertEquals(2, responseBody.data!!.size)

            val currentList = listOf(categoryList[1], categoryList[3])
            val shouldResponse = wrapSuccess(currentList)
            assertEquals(shouldResponse, responseBody)
        }

        client.sendAuthenticatedRequest(
            HttpMethod.Get,
            "/categories?period=${formatToPeriod(now.minusMonths(4))}"
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Category>> = response.body()
            assert(responseBody.success)
            assertEquals(1, responseBody.data!!.size)

            val currentList = listOf(categoryList[0])
            val shouldResponse = wrapSuccess(currentList)
            assertEquals(shouldResponse, responseBody)
        }

        client.sendAuthenticatedRequest(
            HttpMethod.Get,
            "/categories?period=${formatToPeriod(now.minusMonths(5))}"
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Category>> = response.body()
            assert(responseBody.success)
            assertEquals(1, responseBody.data!!.size)

            val currentList = listOf(categoryList[0])
            val shouldResponse = wrapSuccess(currentList)
            assertEquals(shouldResponse, responseBody)
        }

        client.sendAuthenticatedRequest(
            HttpMethod.Get,
            "/categories?period=${formatToPeriod(now.minusMonths(6))}"
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Category>> = response.body()
            assert(responseBody.success)
            assertEquals(0, responseBody.data!!.size)
            val shouldResponse: APIResponse<List<Category>> = wrapSuccess(emptyList())
            assertEquals(shouldResponse, responseBody)
        }
    }
}
