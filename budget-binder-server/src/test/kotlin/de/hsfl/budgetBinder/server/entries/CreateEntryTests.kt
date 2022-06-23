package de.hsfl.budgetBinder.server.entries

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.server.models.EntryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.utils.*
import io.ktor.client.call.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

class CreateEntryTests {

    @BeforeTest
    fun before() = customTestApplication { client ->
        client.registerUser()
    }

    @AfterTest
    fun after() = transaction {
        UserEntity.all().forEach { it.delete() }
    }

    @Test
    fun testCreateEntry() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequestWithBody(
            HttpMethod.Post, "/entries",
            Entry.In("Bafög", 700f, true, null)
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()

            val id = transaction {
                EntryEntity.all().last().let {
                    assertEquals("Bafög", it.name)
                    assertEquals(700f, it.amount)
                    assert(it.repeat)
                    assertNull(it.category)
                    it.id.value
                }
            }
            val shouldResponse = wrapSuccess(Entry(id, "Bafög", 700f, true, null))
            assertEquals(shouldResponse, responseBody)
        }
    }
}
