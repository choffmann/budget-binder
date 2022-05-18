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
                TestCategories.categories.forEach {
                    CategoryEntity.new {
                        name = it.name
                        color = TestCategories.color
                        image = TestCategories.image
                        budget = it.budget
                        created = now.plusMonths(it.createdMonthOffset.toLong())
                        ended = it.endedMonthOffset?.let { endedOffset -> now.plusMonths(endedOffset.toLong()) }
                        child = null
                        user = userEntity
                    }
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
                        TestCategories.categories[0].name,
                        TestCategories.color,
                        TestCategories.image,
                        TestCategories.categories[0].budget,
                    )
                )
            ) {
                assertEquals(HttpStatusCode.OK, response.status())

                val category: APIResponse<Category> = decodeFromString(response.content!!)
                val id = transaction {
                    val categoryEntity = CategoryEntity.all().last()

                    assertEquals(TestCategories.categories[0].name, categoryEntity.name)
                    assertEquals(TestCategories.color, categoryEntity.color)
                    assertEquals(TestCategories.image, categoryEntity.image)
                    assertEquals(TestCategories.categories[0].budget, categoryEntity.budget)

                    categoryEntity.id.value
                }
                val shouldCategory: APIResponse<Category> = wrapSuccess(TestCategories.getTestCategory(id, 0))
                assertEquals(shouldCategory, category)
            }
        }
    }

    @Test
    fun testGetCategories() {
        withCustomTestApplication(Application::mainModule) {
            loginUser()


        }
    }
}
