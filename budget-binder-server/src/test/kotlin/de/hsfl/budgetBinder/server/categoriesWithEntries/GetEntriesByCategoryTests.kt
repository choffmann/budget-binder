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

class GetEntriesByCategoryTests {

    private fun getEntryListFromID(entryId: Int, categoryId: Int): List<Entry> {
        return listOf(
            Entry(entryId, "Internet", -50f, true, categoryId),
            Entry(entryId + 1, "Internet", -50f, true, categoryId + 1),
            Entry(entryId + 2, "Phone", -50f, true, categoryId + 1),
            Entry(entryId + 3, "Phone one Time", -250f, false, categoryId + 1),
            Entry(entryId + 4, "Monthly Pay", 3000f, true, null),
        )
    }

    @BeforeTest
    fun before() = customTestApplication { client ->
        client.registerUser()

        val userEntity = transaction { UserEntity.all().first() }
        val now = LocalDateTime.now()

        transaction {
            val internetCategory = CategoryEntity.new {
                name = "Internet"
                color = TestCategories.color
                image = TestCategories.image
                budget = 50f
                created = now.minusMonths(3)
                ended = now.minusMonths(2)
                child = null
                user = userEntity
            }

            val internetPhoneCategory = CategoryEntity.new {
                name = "Internet-Phone"
                color = TestCategories.color
                image = TestCategories.image
                budget = 100f
                created = now.minusMonths(2)
                ended = null
                child = null
                user = userEntity
            }

            internetCategory.child = internetPhoneCategory.id

            val internetEntry = EntryEntity.new {
                name = "Internet"
                amount = -50f
                repeat = true
                created = now.minusMonths(3)
                ended = now.minusMonths(2)
                child = null
                user = userEntity
                category = internetCategory.id
            }

            EntryEntity.new {
                name = "Internet"
                amount = -50f
                repeat = true
                created = now.minusMonths(2)
                ended = null
                child = null
                user = userEntity
                category = internetPhoneCategory.id
            }.let { internetEntry.child = it.id }

            EntryEntity.new {
                name = "Phone"
                amount = -50f
                repeat = true
                created = now.minusMonths(2)
                ended = null
                child = null
                user = userEntity
                category = internetPhoneCategory.id
            }

            EntryEntity.new {
                name = "Phone one Time"
                amount = -250f
                repeat = false
                created = now.minusMonths(2)
                ended = null
                child = null
                user = userEntity
                category = internetPhoneCategory.id
            }

            EntryEntity.new {
                name = "Monthly Pay"
                amount = 3000f
                repeat = true
                created = now.minusMonths(3)
                ended = null
                child = null
                user = userEntity
                category = null
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
    fun testGetEntriesByCategoryString() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequest(HttpMethod.Get, "/categories/test/entries") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Entry>> = response.body()
            val shouldResponse: APIResponse<List<Entry>> = wrapFailure("The ID you provided is not a number.")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testGetEntriesByCategoryNotFound() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequest(HttpMethod.Get, "/categories/5000/entries") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Entry>> = response.body()
            val shouldResponse: APIResponse<List<Entry>> = wrapFailure("Your category was not found.")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testGetEntriesByCategory() = customTestApplicationWithLogin { client ->
        val categoryId = transaction { CategoryEntity.all().first().id.value }
        val entryId = transaction { EntryEntity.all().first().id.value }
        val entryList = getEntryListFromID(entryId, categoryId)

        client.sendAuthenticatedRequest(HttpMethod.Get, "/categories/$categoryId/entries") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Entry>> = response.body()
            val shouldResponse = wrapSuccess(listOf(entryList[0]))
            assertEquals(shouldResponse, responseBody)
        }

        client.sendAuthenticatedRequest(HttpMethod.Get, "/categories/${categoryId + 1}/entries") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Entry>> = response.body()
            val shouldResponse = wrapSuccess(listOf(entryList[1], entryList[2], entryList[3]))
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testGetEntriesByCategoryNull() = customTestApplicationWithLogin { client ->
        val categoryId = transaction { CategoryEntity.all().first().id.value }
        val entryId = transaction { EntryEntity.all().first().id.value }
        val entryList = getEntryListFromID(entryId, categoryId)

        client.sendAuthenticatedRequest(HttpMethod.Get, "/categories/null/entries") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Entry>> = response.body()
            val shouldResponse = wrapSuccess(listOf(entryList[4]))
            assertEquals(shouldResponse, responseBody)
        }
    }
}
