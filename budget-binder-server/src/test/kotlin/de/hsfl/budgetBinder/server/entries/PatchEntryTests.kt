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

class PatchEntryTests {
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
    fun testPatchEntryFalseBody() = customTestApplicationWithLogin { client ->
        val id = transaction { EntryEntity.all().first().id.value }

        client.sendAuthenticatedRequest(HttpMethod.Patch, "/entries/$id") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("The object you provided it not in the right format.")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testPatchEntryOld() = customTestApplicationWithLogin { client ->
        val id = transaction { EntryEntity.all().first().id.value }

        client.sendAuthenticatedRequestWithBody(
            HttpMethod.Patch, "/entries/$id",
            Entry.Patch(name = "Pay")
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("you can't change this Entry")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testPatchEntryName() = customTestApplicationWithLogin { client ->
        val id = transaction { EntryEntity.all().first().id.value + 1 }

        client.sendAuthenticatedRequestWithBody(
            HttpMethod.Patch, "/entries/$id",
            Entry.Patch(name = "Pay")
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse = wrapSuccess(Entry(id, "Pay", 3500f, true, null))
            assertEquals(shouldResponse, responseBody)

            transaction {
                val entry = EntryEntity[id]
                assertEquals("Pay", entry.name)
            }
        }
    }

    @Test
    fun testPatchEntryAmountNotRepeat() = customTestApplicationWithLogin { client ->
        val id = transaction { EntryEntity.all().first().id.value + 2 }

        client.sendAuthenticatedRequestWithBody(
            HttpMethod.Patch, "/entries/$id",
            Entry.Patch(amount = -1700f)
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse = wrapSuccess(Entry(id, "Bike", -1700f, false, null))
            assertEquals(shouldResponse, responseBody)

            transaction {
                val entry = EntryEntity[id]
                assertEquals(-1700f, entry.amount)
            }
        }
    }

    @Test
    fun testPatchEntryAmountRepeatNew() = customTestApplicationWithLogin { client ->
        val id = transaction { EntryEntity.all().first().id.value + 3 }

        client.sendAuthenticatedRequestWithBody(
            HttpMethod.Patch, "/entries/$id",
            Entry.Patch(amount = -60f)
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse = wrapSuccess(Entry(id, "new Phone", -60f, true, null))
            assertEquals(shouldResponse, responseBody)

            transaction {
                val entry = EntryEntity[id]
                assertEquals(-60f, entry.amount)
            }
        }
    }

    @Test
    fun testPatchEntryAmountRepeatOld() = customTestApplicationWithLogin { client ->
        val id = transaction { EntryEntity.all().first().id.value + 1 }
        val newId = transaction { EntryEntity.all().last().id.value + 1 }

        client.sendAuthenticatedRequestWithBody(
            HttpMethod.Patch, "/entries/$id",
            Entry.Patch(amount = 3700f)
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse = wrapSuccess(Entry(newId, "Monthly Job Pay", 3700f, true, null))
            assertEquals(shouldResponse, responseBody)

            transaction {
                val oldEntry = EntryEntity[id]
                val newEntry = EntryEntity[newId]
                assertNotNull(oldEntry.ended)
                assertEquals(newEntry.id, oldEntry.child)
                assertNull(newEntry.ended)
                assertNull(newEntry.child)
            }
        }
    }

    @Test
    fun testPatchEntryRepeatToFalseNew() = customTestApplicationWithLogin { client ->
        val id = transaction { EntryEntity.all().first().id.value + 3 }

        client.sendAuthenticatedRequestWithBody(
            HttpMethod.Patch, "/entries/$id",
            Entry.Patch(repeat = false)
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse = wrapSuccess(Entry(id, "new Phone", -50f, false, null))
            assertEquals(shouldResponse, responseBody)

            transaction {
                val entry = EntryEntity[id]
                assert(!entry.repeat)
            }
        }
    }

    @Test
    fun testPatchEntryRepeatToFalseOld() = customTestApplicationWithLogin { client ->
        val id = transaction { EntryEntity.all().first().id.value + 1 }
        val newId = transaction { EntryEntity.all().last().id.value + 1 }

        client.sendAuthenticatedRequestWithBody(
            HttpMethod.Patch, "/entries/$id",
            Entry.Patch(repeat = false)
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse = wrapSuccess(Entry(newId, "Monthly Job Pay", 3500f, false, null))
            assertEquals(shouldResponse, responseBody)

            transaction {
                val oldEntry = EntryEntity[id]
                val newEntry = EntryEntity[newId]
                assertNotNull(oldEntry.ended)
                assertEquals(newEntry.id, oldEntry.child)
                assertNull(newEntry.ended)
                assertNull(newEntry.child)
            }
        }
    }
}
