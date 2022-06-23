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

class OldCategoryHasOldEntriesTests {
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
                created = now.minusMonths(1)
                ended = null
                child = null
                user = userEntity
                category = shoppingCategory.id
            }

            EntryEntity.new {
                name = "Ikea"
                amount = -200f
                repeat = false
                created = now.minusMonths(1)
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

            val newCategoryId = transaction { CategoryEntity.all().last().id.value }
            val newEntryId = transaction { EntryEntity.all().last().id.value }

            val responseBody: APIResponse<Category> = response.body()
            val shouldResponse = wrapSuccess(
                Category(
                    newCategoryId,
                    "Shopping",
                    TestCategories.color,
                    TestCategories.image,
                    400f
                )
            )
            assertEquals(shouldResponse, responseBody)

            transaction {
                val categoryEntity = CategoryEntity[categoryId]
                val newCategoryEntity = CategoryEntity[newCategoryId]
                assertNotNull(categoryEntity.ended)
                assertNull(newCategoryEntity.ended)
                assertNull(newCategoryEntity.child)
                assertEquals(newCategoryEntity.id, categoryEntity.child)
                assertEquals(300f, categoryEntity.budget)
                assertEquals(400f, newCategoryEntity.budget)

                val entryEntity1 = EntryEntity[entryId]
                val newEntryEntity1 = EntryEntity[newEntryId]
                val entryEntity2 = EntryEntity[entryId + 1]

                assertNotNull(entryEntity1.ended)
                assertEquals(newEntryEntity1.id, entryEntity1.child)
                assert(entryEntity1.repeat)
                assert(newEntryEntity1.repeat)

                assertNull(newEntryEntity1.ended)
                assertNull(newEntryEntity1.child)

                assertEquals(categoryEntity.id, entryEntity1.category)
                assertEquals(newCategoryEntity.id, newEntryEntity1.category)
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

            val newEntryId = transaction { EntryEntity.all().last().id.value }

            transaction {
                val categoryEntity = CategoryEntity[categoryId]
                assertNotNull(categoryEntity.ended)
                assertNull(categoryEntity.child)

                val entryEntity1 = EntryEntity[entryId]
                val newEntryEntity1 = EntryEntity[newEntryId]
                val entryEntity2 = EntryEntity[entryId + 1]

                assertNotNull(entryEntity1.ended)
                assertEquals(newEntryEntity1.id, entryEntity1.child)
                assert(entryEntity1.repeat)
                assert(newEntryEntity1.repeat)

                assertNull(newEntryEntity1.ended)
                assertNull(newEntryEntity1.child)

                assertEquals(categoryEntity.id, entryEntity1.category)
                assertNull(newEntryEntity1.category)
                assertEquals(categoryEntity.id, entryEntity2.category)

                assertNull(entryEntity2.child)
                assertNull(entryEntity2.ended)
                assert(!entryEntity2.repeat)
            }
        }
    }
}
