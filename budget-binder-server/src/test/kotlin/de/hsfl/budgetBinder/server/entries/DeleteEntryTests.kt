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

class DeleteEntryTests {
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
    fun testDeleteEntryOld() = customTestApplicationWithLogin { client ->
        val id = transaction { EntryEntity.all().first().id.value }

        client.sendAuthenticatedRequest(HttpMethod.Delete, "/entries/$id") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("you can't delete this Entry")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testDeleteEntryNotRepeat() = customTestApplicationWithLogin { client ->
        val id = transaction { EntryEntity.all().first().id.value + 2 }

        client.sendAuthenticatedRequest(HttpMethod.Delete, "/entries/$id") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse = wrapSuccess(Entry(id, "Bike", -1500f, false, null))
            assertEquals(shouldResponse, responseBody)

            transaction {
                val entry = EntryEntity.findById(id)
                assertNull(entry)
            }
        }
    }

    @Test
    fun testDeleteEntryRepeatNew() = customTestApplicationWithLogin { client ->
        println(transaction { EntryEntity.all().toList().map { it.toDto() } })


        val id = transaction { EntryEntity.all().first().id.value + 3 }

        client.sendAuthenticatedRequest(HttpMethod.Delete, "/entries/$id") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse = wrapSuccess(Entry(id, "new Phone", -50f, true, null))
            assertEquals(shouldResponse, responseBody)

            transaction {
                val entry = EntryEntity.findById(id)
                assertNull(entry)
            }
        }
    }

    @Test
    fun testDeleteEntryRepeatOld() = customTestApplicationWithLogin { client ->
        val id = transaction { EntryEntity.all().first().id.value + 1 }

        client.sendAuthenticatedRequest(HttpMethod.Delete, "/entries/$id") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse = wrapSuccess(Entry(id, "Monthly Job Pay", 3500f, true, null))
            assertEquals(shouldResponse, responseBody)

            transaction {
                val entry = EntryEntity[id]
                assertNotNull(entry.ended)
            }
        }
    }
}
