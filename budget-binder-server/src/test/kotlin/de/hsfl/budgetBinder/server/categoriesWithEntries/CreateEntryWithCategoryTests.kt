package de.hsfl.budgetBinder.server.categoriesWithEntries

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.server.models.CategoryEntity
import de.hsfl.budgetBinder.server.models.EntryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.utils.*
import io.ktor.client.call.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import kotlin.test.*

class CreateEntryWithCategoryTests {
    @BeforeTest
    fun before() = customTestApplication { client ->
        client.registerUser()

        transaction {
            CategoryEntity.new {
                name = "Shopping"
                color = TestCategories.color
                image = TestCategories.image
                budget = 300f
                created = LocalDateTime.now()
                ended = null
                child = null
                user = UserEntity.all().first()
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
    fun testCreateEntryWithCategoryNotFound() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequestWithBody(
            HttpMethod.Post, "/entries",
            Entry.In("Second Phone", -50f, true, 5000)
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()

            val id = transaction {
                EntryEntity.all().last().let {
                    assertEquals("Second Phone", it.name)
                    assertEquals(-50f, it.amount)
                    assert(it.repeat)
                    assertNull(it.category)
                    it.id.value
                }
            }
            val shouldResponse = wrapSuccess(Entry(id, "Second Phone", -50f, true, null))
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testCreateEntryWithCategory() = customTestApplicationWithLogin { client ->
        val categoryId = transaction { CategoryEntity.all().first().id.value }
        client.sendAuthenticatedRequestWithBody(
            HttpMethod.Post, "/entries",
            Entry.In("Second Phone", -50f, true, categoryId)
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()

            val id = transaction {
                EntryEntity.all().last().let {
                    assertEquals("Second Phone", it.name)
                    assertEquals(-50f, it.amount)
                    assert(it.repeat)
                    assertEquals(categoryId, it.category?.value)
                    it.id.value
                }
            }
            val shouldResponse = wrapSuccess(Entry(id, "Second Phone", -50f, true, categoryId))
            assertEquals(shouldResponse, responseBody)
        }
    }
}
