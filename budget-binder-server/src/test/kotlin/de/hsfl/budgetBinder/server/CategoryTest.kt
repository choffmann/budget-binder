package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.server.models.CategoryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import kotlin.test.*

class CategoryTest {

    @BeforeTest
    fun before() = customTestApplication { client ->
        registerUser(client)

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
        UserEntity.all().forEach {
            CategoryEntity[it.category!!].delete()
            it.delete()
        }
        CategoryEntity.all().forEach {
            it.delete()
        }
    }


    @Test
    fun testCreateCategory() = customTestApplicationWithLogin { client ->
        client.get("/categories").let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("Your accessToken is absent or does not match.", 401)
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(client, HttpMethod.Post, "/categories") { response ->
            assertEquals(HttpStatusCode.OK, response.status)

            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("The object you provided it not in the right format.")
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequestWithBody(
            client,
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


    @Test
    fun testGetCategories() = customTestApplicationWithLogin { client ->
        client.get("/categories").let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<List<Category>> = response.body()
            val shouldResponse: APIResponse<List<Category>> = wrapFailure("Your accessToken is absent or does not match.", 401)
            assertEquals(shouldResponse, responseBody)
        }

        val id = transaction { CategoryEntity.all().first().id.value + 1 }

        val categoryList = listOf(
            Category(id, "test", TestCategories.color, TestCategories.image, 10f),
            Category(id + 1, "Internet-Phone", TestCategories.color, TestCategories.image, 50f),
            Category(id + 2, "Internet-Phone", TestCategories.color, TestCategories.image, 100f),
            Category(id + 3, "Insurance", TestCategories.color, TestCategories.image, 100f),
            Category(id + 4, "Hobbies", TestCategories.color, TestCategories.image, 150f),
        )

        sendAuthenticatedRequest(client, HttpMethod.Get, "/categories") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Category>> = response.body()
            assert(responseBody.success)
            assertEquals(5, responseBody.data!!.size)
            val shouldResponse = wrapSuccess(categoryList)
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(client, HttpMethod.Get, "/categories?current=true") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Category>> = response.body()
            assert(responseBody.success)
            assertEquals(3, responseBody.data!!.size)

            val currentList = listOf(categoryList[2], categoryList[3], categoryList[4])
            val shouldResponse = wrapSuccess(currentList)
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(client, HttpMethod.Get, "/categories?period=508346") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Category>> = response.body()
            val shouldResponse = wrapFailure<List<Category>>("period has not the right pattern")
            assertEquals(shouldResponse, responseBody)
        }

        val now = LocalDateTime.now()

        sendAuthenticatedRequest(client, HttpMethod.Get, "/categories?period=${formatToPeriod(now)}") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Category>> = response.body()
            assert(responseBody.success)
            assertEquals(3, responseBody.data!!.size)

            val currentList = listOf(categoryList[2], categoryList[3], categoryList[4])
            val shouldResponse = wrapSuccess(currentList)
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(
            client,
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

        sendAuthenticatedRequest(
            client,
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

        sendAuthenticatedRequest(
            client,
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

        sendAuthenticatedRequest(
            client,
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

        sendAuthenticatedRequest(
            client,
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


    @Test
    fun testGetCategoryById() = customTestApplicationWithLogin { client ->
        client.get("/categories/1").let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("Your accessToken is absent or does not match.", 401)
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(client, HttpMethod.Get, "/categories/test") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("The ID you provided is not a number.")
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(client, HttpMethod.Get, "/categories/null") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("The ID you provided is not a number.")
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(client, HttpMethod.Get, "/categories/5000") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("Your category was not found.")
            assertEquals(shouldResponse, responseBody)
        }

        val id = transaction { CategoryEntity.all().first().id.value + 1 }

        sendAuthenticatedRequest(client, HttpMethod.Get, "/categories/${id - 1}") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("Your category was not found.")
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(client, HttpMethod.Get, "/categories/${id}") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse = wrapSuccess(Category(id, "test", TestCategories.color, TestCategories.image, 10f))
            assertEquals(shouldResponse, responseBody)
        }
    }


    @Test
    fun testPatchCategory() = customTestApplicationWithLogin { client ->
        client.patch("/categories/1").let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("Your accessToken is absent or does not match.", 401)
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(client, HttpMethod.Patch, "/categories/test") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("The ID you provided is not a number.")
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(client, HttpMethod.Patch, "/categories/null") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("The ID you provided is not a number.")
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(client, HttpMethod.Patch, "/categories/5000") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("Your category was not found.")
            assertEquals(shouldResponse, responseBody)
        }

        val id = transaction { CategoryEntity.all().first().id.value + 1 }

        sendAuthenticatedRequest(client, HttpMethod.Patch, "/categories/${id}") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("The object you provided it not in the right format.")
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequestWithBody(
            client,
            HttpMethod.Patch,
            "/categories/${id}",
            Category.Patch(name = "patchedTest")
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("you can't change an old category.")
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequestWithBody(
            client,
            HttpMethod.Patch,
            "/categories/${id - 1}",
            Category.Patch(name = "patchedTest")
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("Your category was not found.")
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequestWithBody(
            client,
            HttpMethod.Patch,
            "/categories/${id + 4}",
            Category.Patch(name = "Fishing")
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse =
                wrapSuccess(Category(id + 4, "Fishing", TestCategories.color, TestCategories.image, 150f))
            assertEquals(shouldResponse, responseBody)

            transaction {
                val categoryEntity = CategoryEntity[id + 4]
                assertEquals("Fishing", categoryEntity.name)
                assertNull(categoryEntity.ended)
                assertNull(categoryEntity.child)
            }
        }

        sendAuthenticatedRequestWithBody(
            client,
            HttpMethod.Patch,
            "/categories/${id + 4}",
            Category.Patch(name = "Fishing", budget = 100f)
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse =
                wrapSuccess(Category(id + 4, "Fishing", TestCategories.color, TestCategories.image, 100f))
            assertEquals(shouldResponse, responseBody)

            transaction {
                val newCategoryEntity = CategoryEntity[id + 4]
                assertEquals("Fishing", newCategoryEntity.name)
                assertNull(newCategoryEntity.ended)
                assertNull(newCategoryEntity.child)
            }
        }
    }


    @Test
    fun testDeleteCategory() = customTestApplicationWithLogin { client ->
        client.get("categories/1").let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("Your accessToken is absent or does not match.", 401)
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(client, HttpMethod.Delete, "/categories/test") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("The ID you provided is not a number.")
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(client, HttpMethod.Delete, "/categories/null") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("The ID you provided is not a number.")
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(client, HttpMethod.Delete, "/categories/5000") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("Your category was not found.")
            assertEquals(shouldResponse, responseBody)
        }

        val id = transaction { CategoryEntity.all().first().id.value + 1 }

        sendAuthenticatedRequest(client, HttpMethod.Delete, "/categories/${id - 1}") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("Your category was not found.")
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(client, HttpMethod.Delete, "/categories/${id}") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> = wrapFailure("you can't delete an old category.")
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(client, HttpMethod.Delete, "/categories/${id + 4}") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse: APIResponse<Category> =
                wrapSuccess(Category(id + 4, "Hobbies", TestCategories.color, TestCategories.image, 150f))
            assertEquals(shouldResponse, responseBody)

            transaction {
                val categoryEntity = CategoryEntity.findById(id + 4)
                assertNull(categoryEntity)
            }
        }
    }
}
