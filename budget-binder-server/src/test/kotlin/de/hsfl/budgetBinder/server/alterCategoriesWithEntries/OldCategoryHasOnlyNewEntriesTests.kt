package de.hsfl.budgetBinder.server.alterCategoriesWithEntries

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.server.models.CategoryEntity
import de.hsfl.budgetBinder.server.models.EntryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.utils.*
import io.ktor.client.call.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import kotlin.test.*

class OldCategoryHasOnlyNewEntriesTests {
    @BeforeTest
    fun before() = customTestApplication { client ->
        client.registerUser()

        val userEntity = transaction { UserEntity.all().first() }
        val now = LocalDateTime.now()

        transaction {
            val shoppingCategory = CategoryEntity.new {
                name = "Shopping"
                color = TestCategories.color
                image = TestCategories.image
                budget = 300f
                created = now.minusMonths(1)
                ended = null
                child = null
                user = userEntity
            }

            EntryEntity.new {
                name = "Aldi"
                amount = -200f
                repeat = true
                created = now
                ended = null
                child = null
                user = userEntity
                category = shoppingCategory.id
            }

            EntryEntity.new {
                name = "Ikea"
                amount = -200f
                repeat = false
                created = now
                ended = null
                child = null
                user = userEntity
                category = shoppingCategory.id
            }
        }
    }

    @AfterTest
    fun after() = transaction {
        EntryEntity.all().forEach { it.delete() }
        CategoryEntity.all().forEach { it.delete() }
        UserEntity.all().forEach { it.delete() }
    }

    @Test
    fun testPatchCategory() = customTestApplicationWithLogin { client ->
        val categoryId = transaction { CategoryEntity.all().first().id.value }
        val entryId = transaction { EntryEntity.all().first().id.value }

        client.sendAuthenticatedRequestWithBody(
            HttpMethod.Patch, "/categories/$categoryId",
            Category.Patch(budget = 400f)
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse = wrapSuccess(
                Category(
                    categoryId,
                    "Shopping",
                    TestCategories.color,
                    TestCategories.image,
                    400f
                )
            )
            assertEquals(shouldResponse, responseBody)

            transaction {
                val categoryEntity = CategoryEntity[categoryId]
                assertNull(categoryEntity.ended)
                assertNull(categoryEntity.child)
                assertEquals(400f, categoryEntity.budget)

                val entryEntity1 = EntryEntity[entryId]
                val entryEntity2 = EntryEntity[entryId + 1]

                assertNull(entryEntity1.child)
                assertNull(entryEntity1.ended)
                assert(entryEntity1.repeat)

                assertEquals(categoryEntity.id, entryEntity1.category)
                assertEquals(categoryEntity.id, entryEntity2.category)

                assertNull(entryEntity2.child)
                assertNull(entryEntity2.ended)
                assert(!entryEntity2.repeat)
            }
        }
    }

    @Test
    fun testDeleteCategory() = customTestApplicationWithLogin { client ->
        val categoryId = transaction { CategoryEntity.all().first().id.value }
        val entryId = transaction { EntryEntity.all().first().id.value }

        client.sendAuthenticatedRequest(HttpMethod.Delete, "/categories/$categoryId") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse = wrapSuccess(
                Category(
                    categoryId,
                    "Shopping",
                    TestCategories.color,
                    TestCategories.image,
                    300f
                )
            )
            assertEquals(shouldResponse, responseBody)

            transaction {
                val categoryEntity = CategoryEntity.findById(categoryId)
                assertNull(categoryEntity)

                val mobileEntry = EntryEntity[entryId]
                val mobileOneEntry = EntryEntity[entryId + 1]

                assertNull(mobileEntry.child)
                assertNull(mobileEntry.ended)
                assert(mobileEntry.repeat)

                assertNull(mobileEntry.category)
                assertNull(mobileOneEntry.category)

                assertNull(mobileOneEntry.child)
                assertNull(mobileOneEntry.ended)
                assert(!mobileOneEntry.repeat)
            }
        }
    }
}
