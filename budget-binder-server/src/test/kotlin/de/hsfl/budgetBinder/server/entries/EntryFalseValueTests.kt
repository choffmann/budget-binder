package de.hsfl.budgetBinder.server.entries

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.utils.*
import io.ktor.client.call.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

class EntryFalseValueTests {
    @BeforeTest
    fun before() = customTestApplication { client ->
        client.registerUser()
    }

    @AfterTest
    fun after() = transaction {
        UserEntity.all().forEach { it.delete() }
    }

    @Test
    fun testCreateEntryFalseBody() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequest(HttpMethod.Post, "/entries") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("The object you provided it not in the right format.")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testGetEntriesByPeriod() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequest(HttpMethod.Get, "/entries?period=50-8346") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Entry>> = response.body()
            val shouldResponse: APIResponse<List<Entry>> = wrapFailure("period has not the right pattern")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testGetEntryByIDString() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequest(HttpMethod.Get, "/entries/test") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("The ID you provided is not a number.")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testGetEntryByIDNotFound() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequest(HttpMethod.Get, "/entries/5000") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("Your entry was not found.")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testPatchEntryByIDString() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequest(HttpMethod.Patch, "/entries/test") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("The ID you provided is not a number.")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testPatchEntryByIDNotFound() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequest(HttpMethod.Patch, "/entries/5000") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("Your entry was not found.")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testDeleteEntryByIDString() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequest(HttpMethod.Delete, "/entries/test") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("The ID you provided is not a number.")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testDeleteEntryByIDNotFound() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequest(HttpMethod.Delete, "/entries/5000") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("Your entry was not found.")
            assertEquals(shouldResponse, responseBody)
        }
    }
}
