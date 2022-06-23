package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.server.models.EntryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.utils.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import kotlin.test.*

class EntryTest {

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
        EntryEntity.all().forEach {
            it.delete()
        }
        UserEntity.all().forEach {
            it.delete()
        }
    }


    @Test
    fun testGetEntryById() = customTestApplicationWithLogin { client ->
        client.get("/entries/1").let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("Your accessToken is absent or does not match.", 401)
            assertEquals(shouldResponse, responseBody)
        }

        client.sendAuthenticatedRequest(HttpMethod.Get, "/entries/test") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("The ID you provided is not a number.")
            assertEquals(shouldResponse, responseBody)
        }

        client.sendAuthenticatedRequest(HttpMethod.Get, "/entries/5000") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("Your entry was not found.")
            assertEquals(shouldResponse, responseBody)
        }

        val id = transaction { EntryEntity.all().first().id.value }

        client.sendAuthenticatedRequest(HttpMethod.Get, "/entries/$id") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse = wrapSuccess(Entry(id, "Monthly Job Pay", 3000f, true, null))
            assertEquals(shouldResponse, responseBody)
        }
    }


    @Test
    fun testPatchEntry() = customTestApplicationWithLogin { client ->
        client.patch("/entries/1").let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("Your accessToken is absent or does not match.", 401)
            assertEquals(shouldResponse, responseBody)
        }

        client.sendAuthenticatedRequest(HttpMethod.Patch, "/entries/test") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("The ID you provided is not a number.")
            assertEquals(shouldResponse, responseBody)
        }

        client.sendAuthenticatedRequest(HttpMethod.Patch, "/entries/5000") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("Your entry was not found.")
            assertEquals(shouldResponse, responseBody)
        }

        val id = transaction { EntryEntity.all().first().id.value }

        client.sendAuthenticatedRequest(HttpMethod.Patch, "/entries/$id") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("The object you provided it not in the right format.")
            assertEquals(shouldResponse, responseBody)
        }

        client.sendAuthenticatedRequestWithBody(
            HttpMethod.Patch, "/entries/$id",
            Entry.Patch(name = "Pay")
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("you can't change this Entry")
            assertEquals(shouldResponse, responseBody)
        }

        client.sendAuthenticatedRequestWithBody(
            HttpMethod.Patch, "/entries/${id + 5}",
            Entry.Patch(name = "Ikea Shopping")
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse = wrapSuccess(Entry(id + 5, "Ikea Shopping", -200f, false, null))
            assertEquals(shouldResponse, responseBody)
        }

        client.sendAuthenticatedRequestWithBody(
            HttpMethod.Patch, "/entries/${id + 4}",
            Entry.Patch(amount = -1700f)
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse = wrapSuccess(Entry(id + 4, "Bike", -1700f, false, null))
            assertEquals(shouldResponse, responseBody)
        }

        client.sendAuthenticatedRequestWithBody(
            HttpMethod.Patch, "/entries/${id + 5}",
            Entry.Patch(name = "Ikea", repeat = true)
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse = wrapSuccess(Entry(id + 5, "Ikea", -200f, true, null))
            assertEquals(shouldResponse, responseBody)
        }

        client.sendAuthenticatedRequestWithBody(
            HttpMethod.Patch, "/entries/${id + 6}",
            Entry.Patch(repeat = false)
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse = wrapSuccess(Entry(id + 6, "new Phone", -50f, false, null))
            assertEquals(shouldResponse, responseBody)
        }

        client.sendAuthenticatedRequestWithBody(
            HttpMethod.Patch, "/entries/${id + 3}",
            Entry.Patch(repeat = false)
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse = wrapSuccess(Entry(id + 7, "Internet", -50f, false, null))
            assertEquals(shouldResponse, responseBody)

            transaction {
                val oldEntry = EntryEntity[id + 3]
                val newEntry = EntryEntity[id + 7]
                assertNotNull(oldEntry.ended)
                assertEquals(newEntry.id, oldEntry.child)
                assertNull(newEntry.ended)
                assertNull(newEntry.child)
            }
        }

        client.sendAuthenticatedRequestWithBody(
            HttpMethod.Patch, "/entries/${id + 1}",
            Entry.Patch(amount = 3700f)
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse = wrapSuccess(Entry(id + 8, "Monthly Job Pay", 3700f, true, null))
            assertEquals(shouldResponse, responseBody)

            transaction {
                val oldEntry = EntryEntity[id + 1]
                val newEntry = EntryEntity[id + 8]
                assertNotNull(oldEntry.ended)
                assertEquals(newEntry.id, oldEntry.child)
                assertNull(newEntry.ended)
                assertNull(newEntry.child)
            }
        }
    }


    @Test
    fun testDeleteEntry() = customTestApplicationWithLogin { client ->
        client.delete("/entries/1").let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("Your accessToken is absent or does not match.", 401)
            assertEquals(shouldResponse, responseBody)
        }

        client.sendAuthenticatedRequest(HttpMethod.Delete, "/entries/test") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("The ID you provided is not a number.")
            assertEquals(shouldResponse, responseBody)
        }

        client.sendAuthenticatedRequest(HttpMethod.Delete, "/entries/5000") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("Your entry was not found.")
            assertEquals(shouldResponse, responseBody)
        }

        val id = transaction { EntryEntity.all().first().id.value }

        client.sendAuthenticatedRequest(HttpMethod.Delete, "/entries/$id") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("you can't delete this Entry")
            assertEquals(shouldResponse, responseBody)
        }

        client.sendAuthenticatedRequest(HttpMethod.Delete, "/entries/${id + 1}") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse = wrapSuccess(Entry(id + 1, "Monthly Job Pay", 3500f, true, null))
            assertEquals(shouldResponse, responseBody)

            transaction {
                val entry = EntryEntity[id + 1]
                assertNotNull(entry.ended)
            }
        }

        client.sendAuthenticatedRequest(HttpMethod.Delete, "/entries/${id + 6}") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse = wrapSuccess(Entry(id + 6, "new Phone", -50f, true, null))
            assertEquals(shouldResponse, responseBody)

            transaction {
                val entry = EntryEntity.findById(id + 6)
                assertNull(entry)
            }
        }

        client.sendAuthenticatedRequest(HttpMethod.Delete, "/entries/${id + 4}") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse = wrapSuccess(Entry(id + 4, "Bike", -1500f, false, null))
            assertEquals(shouldResponse, responseBody)

            transaction {
                val entry = EntryEntity.findById(id + 4)
                assertNull(entry)
            }
        }

        client.sendAuthenticatedRequest(HttpMethod.Delete, "/entries/${id + 5}") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse = wrapSuccess(Entry(id + 5, "Ikea", -200f, false, null))
            assertEquals(shouldResponse, responseBody)

            transaction {
                val entry = EntryEntity.findById(id + 5)
                assertNull(entry)
            }
        }
    }
}
