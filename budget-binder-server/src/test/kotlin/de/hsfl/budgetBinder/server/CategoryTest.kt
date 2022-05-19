package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.server.models.CategoryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import kotlin.test.*

class CategoryTest {

    @BeforeTest
    fun before() {
        withCustomTestApplication(Application::mainModule) {
            registerUser()

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
    }

    @AfterTest
    fun after() {
        withCustomTestApplication(Application::mainModule) {
            transaction {
                UserEntity.all().forEach {
                    CategoryEntity[it.category!!].delete()
                    it.delete()
                }
                CategoryEntity.all().forEach {
                    it.delete()
                }
            }
        }
    }

    @Test
    fun testCreateCategory() {
        withCustomTestApplication(Application::mainModule) {
            loginUser()

            handleRequest(HttpMethod.Post, "/categories").apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
                assertNull(response.content)
            }

            sendAuthenticatedRequest(HttpMethod.Post, "/categories") {
                assertEquals(HttpStatusCode.OK, response.status())

                val category: APIResponse<Category> = decodeFromString(response.content!!)
                val shouldCategory: APIResponse<Category> = wrapFailure("not the right Parameters provided")
                assertEquals(shouldCategory, category)
            }

            sendAuthenticatedRequest(
                HttpMethod.Post, "/categories",
                toJsonString(
                    Category.In(
                        "Test",
                        TestCategories.color,
                        TestCategories.image,
                        50f,
                    )
                )
            ) {
                assertEquals(HttpStatusCode.OK, response.status())

                val category: APIResponse<Category> = decodeFromString(response.content!!)
                val id = transaction {
                    val categoryEntity = CategoryEntity.all().last()

                    assertEquals("Test", categoryEntity.name)
                    assertEquals(TestCategories.color, categoryEntity.color)
                    assertEquals(TestCategories.image, categoryEntity.image)
                    assertEquals(50f, categoryEntity.budget)

                    categoryEntity.id.value
                }
                val shouldCategory = wrapSuccess(Category(id, "Test", TestCategories.color, TestCategories.image, 50f))
                assertEquals(shouldCategory, category)
            }
        }
    }

    @Test
    fun testGetCategories() {
        withCustomTestApplication(Application::mainModule) {
            loginUser()

            handleRequest(HttpMethod.Get, "/categories").apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
                assertNull(response.content)
            }

            val id = transaction { CategoryEntity.all().first().id.value + 1 }

            val categoryList = listOf(
                Category(id, "test", TestCategories.color, TestCategories.image, 10f),
                Category(id + 1, "Internet-Phone", TestCategories.color, TestCategories.image, 50f),
                Category(id + 2, "Internet-Phone", TestCategories.color, TestCategories.image, 100f),
                Category(id + 3, "Insurance", TestCategories.color, TestCategories.image, 100f),
                Category(id + 4, "Hobbies", TestCategories.color, TestCategories.image, 150f),
            )

            sendAuthenticatedRequest(HttpMethod.Get, "/categories") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Category>> = decodeFromString(response.content!!)
                assert(response.success)
                assertEquals(5, response.data!!.size)
                val shouldResponse = wrapSuccess(categoryList)
                assertEquals(shouldResponse, response)
            }

            sendAuthenticatedRequest(HttpMethod.Get, "/categories?current=true") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Category>> = decodeFromString(response.content!!)
                assert(response.success)
                assertEquals(3, response.data!!.size)

                val currentList = listOf(categoryList[2], categoryList[3], categoryList[4])
                val shouldResponse = wrapSuccess(currentList)
                assertEquals(shouldResponse, response)
            }

            sendAuthenticatedRequest(HttpMethod.Get, "/categories?period=508346") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Category>> = decodeFromString(response.content!!)
                val shouldResponse = wrapFailure<List<Category>>("period has not the right pattern")
                assertEquals(shouldResponse, response)
            }

            val now = LocalDateTime.now()

            sendAuthenticatedRequest(HttpMethod.Get, "/categories?period=${formatToPeriod(now)}") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Category>> = decodeFromString(response.content!!)
                assert(response.success)
                assertEquals(3, response.data!!.size)

                val currentList = listOf(categoryList[2], categoryList[3], categoryList[4])
                val shouldResponse = wrapSuccess(currentList)
                assertEquals(shouldResponse, response)
            }

            sendAuthenticatedRequest(HttpMethod.Get, "/categories?period=${formatToPeriod(now.minusMonths(2))}") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Category>> = decodeFromString(response.content!!)
                assert(response.success)
                assertEquals(3, response.data!!.size)

                val currentList = listOf(categoryList[2], categoryList[3], categoryList[4])
                val shouldResponse = wrapSuccess(currentList)
                assertEquals(shouldResponse, response)
            }

            sendAuthenticatedRequest(HttpMethod.Get, "/categories?period=${formatToPeriod(now.minusMonths(3))}") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Category>> = decodeFromString(response.content!!)
                assert(response.success)
                assertEquals(2, response.data!!.size)

                val currentList = listOf(categoryList[1], categoryList[3])
                val shouldResponse = wrapSuccess(currentList)
                assertEquals(shouldResponse, response)
            }

            sendAuthenticatedRequest(HttpMethod.Get, "/categories?period=${formatToPeriod(now.minusMonths(4))}") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Category>> = decodeFromString(response.content!!)
                assert(response.success)
                assertEquals(1, response.data!!.size)

                val currentList = listOf(categoryList[0])
                val shouldResponse = wrapSuccess(currentList)
                assertEquals(shouldResponse, response)
            }

            sendAuthenticatedRequest(HttpMethod.Get, "/categories?period=${formatToPeriod(now.minusMonths(5))}") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Category>> = decodeFromString(response.content!!)
                assert(response.success)
                assertEquals(1, response.data!!.size)

                val currentList = listOf(categoryList[0])
                val shouldResponse = wrapSuccess(currentList)
                assertEquals(shouldResponse, response)
            }

            sendAuthenticatedRequest(HttpMethod.Get, "/categories?period=${formatToPeriod(now.minusMonths(6))}") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Category>> = decodeFromString(response.content!!)
                assert(response.success)
                assertEquals(0, response.data!!.size)
                val shouldResponse: APIResponse<List<Category>> = wrapSuccess(emptyList())
                assertEquals(shouldResponse, response)
            }
        }
    }
}
