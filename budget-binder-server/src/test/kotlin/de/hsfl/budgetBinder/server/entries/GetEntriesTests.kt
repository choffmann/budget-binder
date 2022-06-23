package de.hsfl.budgetBinder.server.entries

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.server.models.EntryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.utils.*
import io.ktor.client.call.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import kotlin.test.*

class GetEntriesTests {

    private fun getEntryListFromID(id: Int): List<Entry> {
        return listOf(
            Entry(id, "Monthly Job Pay", 3000f, true, null),
            Entry(id + 1, "Monthly Job Pay", 3500f, true, null),
            Entry(id + 2, "Phone", -50f, true, null),
            Entry(id + 3, "Internet", -50f, true, null),
            Entry(id + 4, "Bike", -1500f, false, null),
            Entry(id + 5, "Ikea", -200f, false, null),
            Entry(id + 6, "new Phone", -50f, true, null),
        )
    }

    @BeforeTest
    fun before() = customTestApplication { client ->
        client.registerUser()

        val userEntity = transaction { UserEntity.all().first() }
        val now = LocalDateTime.now()

        transaction {
            val oldPay = EntryEntity.new {
                name = "Monthly Pay"
                amount = 3000f
                repeat = true
                created = now.minusMonths(3)
                ended = now.minusMonths(2)
                child = null

                user = userEntity
                category = null
            }

            val newPay = EntryEntity.new {
                name = "Monthly Job Pay"
                amount = 3500f
                repeat = true
                created = now.minusMonths(2)
                ended = null
                child = null

                user = userEntity
                category = null
            }

            oldPay.child = newPay.id

            EntryEntity.new {
                name = "Phone"
                amount = -50f
                repeat = true
                created = now.minusMonths(3)
                ended = now.minusMonths(1)
                child = null

                user = userEntity
                category = null
            }

            EntryEntity.new {
                name = "Internet"
                amount = -50f
                repeat = true
                created = now.minusMonths(2)
                ended = null
                child = null

                user = userEntity
                category = null
            }

            EntryEntity.new {
                name = "Bike"
                amount = -1500f
                repeat = false
                created = now.minusMonths(1)
                ended = null
                child = null

                user = userEntity
                category = null
            }

            EntryEntity.new {
                name = "Ikea"
                amount = -200f
                repeat = false
                created = now
                ended = null
                child = null

                user = userEntity
                category = null
            }

            EntryEntity.new {
                name = "new Phone"
                amount = -50f
                repeat = true
                created = now
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
        UserEntity.all().forEach { it.delete() }
    }

    @Test
    fun testGetAllCategories() = customTestApplicationWithLogin { client ->
        val id = transaction { EntryEntity.all().first().id.value }
        val entryList = getEntryListFromID(id)

        client.sendAuthenticatedRequest(HttpMethod.Get, "/entries") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Entry>> = response.body()
            assert(responseBody.success)
            assertEquals(7, responseBody.data!!.size)
            val shouldResponse = wrapSuccess(entryList)
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testGetCurrentEntries() = customTestApplicationWithLogin { client ->
        val id = transaction { EntryEntity.all().first().id.value }
        val entryList = getEntryListFromID(id)

        client.sendAuthenticatedRequest(HttpMethod.Get, "/entries?current=true") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Entry>> = response.body()
            assert(responseBody.success)
            assertEquals(4, responseBody.data!!.size)

            val currentList = listOf(entryList[1], entryList[3], entryList[5], entryList[6])
            val shouldResponse = wrapSuccess(currentList)
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testGetEntriesByPeriod() = customTestApplicationWithLogin { client ->
        val id = transaction { EntryEntity.all().first().id.value }
        val entryList = getEntryListFromID(id)
        val now = LocalDateTime.now()

        client.sendAuthenticatedRequest(HttpMethod.Get, "/entries?period=${formatToPeriod(now)}") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Entry>> = response.body()
            assert(responseBody.success)
            assertEquals(4, responseBody.data!!.size)

            val currentList = listOf(entryList[1], entryList[3], entryList[5], entryList[6])
            val shouldResponse = wrapSuccess(currentList)
            assertEquals(shouldResponse, responseBody)
        }

        client.sendAuthenticatedRequest(
            HttpMethod.Get,
            "/entries?period=${formatToPeriod(now.minusMonths(1))}"
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Entry>> = response.body()
            assert(responseBody.success)
            assertEquals(3, responseBody.data!!.size)

            val currentList = listOf(entryList[1], entryList[3], entryList[4])
            val shouldResponse = wrapSuccess(currentList)
            assertEquals(shouldResponse, responseBody)
        }

        client.sendAuthenticatedRequest(
            HttpMethod.Get,
            "/entries?period=${formatToPeriod(now.minusMonths(2))}"
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Entry>> = response.body()
            assert(responseBody.success)
            assertEquals(3, responseBody.data!!.size)

            val currentList = listOf(entryList[1], entryList[2], entryList[3])
            val shouldResponse = wrapSuccess(currentList)
            assertEquals(shouldResponse, responseBody)
        }

        client.sendAuthenticatedRequest(
            HttpMethod.Get,
            "/entries?period=${formatToPeriod(now.minusMonths(3))}"
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Entry>> = response.body()
            assert(responseBody.success)
            assertEquals(2, responseBody.data!!.size)

            val currentList = listOf(entryList[0], entryList[2])
            val shouldResponse = wrapSuccess(currentList)
            assertEquals(shouldResponse, responseBody)
        }

        client.sendAuthenticatedRequest(
            HttpMethod.Get,
            "/entries?period=${formatToPeriod(now.minusMonths(4))}"
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Entry>> = response.body()
            assert(responseBody.success)
            assertEquals(0, responseBody.data!!.size)

            val shouldResponse: APIResponse<List<Entry>> = wrapSuccess(emptyList())
            assertEquals(shouldResponse, responseBody)
        }
    }
}
