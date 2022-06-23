package de.hsfl.budgetBinder.server.alterEntryCategory

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

class ToOldCategoryTests {
    private fun createEntry(isRepeated: Boolean): Int = transaction {
        EntryEntity.new {
            name = "Aldi"
            amount = -200f
            repeat = isRepeated
            created = LocalDateTime.now().minusMonths(2)
            ended = null
            child = null
            user = UserEntity.all().first()
            category = null
        }.id.value
    }

    @BeforeTest
    fun before() = customTestApplication { client ->
        client.registerUser()

        transaction {
            CategoryEntity.new {
                name = "Shopping"
                color = TestCategories.color
                image = TestCategories.image
                budget = 300f
                created = LocalDateTime.now().minusMonths(1)
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
    fun testOldEntryNoRepeat() = customTestApplicationWithLogin { client ->
        val categoryId = transaction { CategoryEntity.all().first().id.value }
        val entryId = createEntry(false)

        client.sendAuthenticatedRequestWithBody(
            HttpMethod.Patch,
            "/entries/$entryId",
            Entry.Patch(category = Entry.Category(categoryId))
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("you can't change this Entry")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testOldEntryRepeat() = customTestApplicationWithLogin { client ->
        val categoryId = transaction { CategoryEntity.all().first().id.value }
        val entryId = createEntry(true)

        client.sendAuthenticatedRequestWithBody(
            HttpMethod.Patch,
            "/entries/$entryId",
            Entry.Patch(category = Entry.Category(categoryId))
        ) { response ->
            val newEntryId = transaction { EntryEntity.all().last().id.value }
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse = wrapSuccess(Entry(newEntryId, "Aldi", -200f, true, categoryId))
            assertEquals(shouldResponse, responseBody)

            transaction {
                val entryEntity = EntryEntity[entryId]
                val newEntryEntity = EntryEntity[newEntryId]

                assertNotNull(entryEntity.ended)
                assertEquals(newEntryEntity.id, entryEntity.child)
                assert(entryEntity.repeat)
                assert(newEntryEntity.repeat)
                assertNull(entryEntity.category)
                assertEquals(categoryId, newEntryEntity.category?.value)
            }
        }
    }
}
