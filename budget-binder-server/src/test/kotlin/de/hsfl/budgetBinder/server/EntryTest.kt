package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.server.models.CategoryEntity
import de.hsfl.budgetBinder.server.models.EntryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import kotlin.test.*

class EntryTest {

    @BeforeTest
    fun before() {
        withCustomTestApplication(Application::mainModule) {
            registerUser()

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
                    category = CategoryEntity[userEntity.category!!]
                }

                val newPay = EntryEntity.new {
                    name = "Monthly Job Pay"
                    amount = 3500f
                    repeat = true
                    created = now.minusMonths(2)
                    ended = null
                    child = null

                    user = userEntity
                    category = CategoryEntity[userEntity.category!!]
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
                    category = CategoryEntity[userEntity.category!!]
                }

                EntryEntity.new {
                    name = "Internet"
                    amount = -50f
                    repeat = true
                    created = now.minusMonths(2)
                    ended = null
                    child = null

                    user = userEntity
                    category = CategoryEntity[userEntity.category!!]
                }

                EntryEntity.new {
                    name = "Bike"
                    amount = -1500f
                    repeat = false
                    created = now.minusMonths(1)
                    ended = null
                    child = null

                    user = userEntity
                    category = CategoryEntity[userEntity.category!!]
                }

                EntryEntity.new {
                    name = "Ikea"
                    amount = -200f
                    repeat = false
                    created = now
                    ended = null
                    child = null

                    user = userEntity
                    category = CategoryEntity[userEntity.category!!]
                }
            }
        }
    }

    @AfterTest
    fun after() {
        withCustomTestApplication(Application::mainModule) {
            transaction {
                EntryEntity.all().forEach {
                    it.delete()
                }
                UserEntity.all().forEach {
                    CategoryEntity[it.category!!].delete()
                    it.delete()
                }
            }
        }
    }

    @Test
    fun testCreateEntry() {
        withCustomTestApplication(Application::mainModule) {
            loginUser()

            handleRequest(HttpMethod.Post, "/entries").apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
                assertNull(response.content)
            }

            sendAuthenticatedRequest(HttpMethod.Post, "/entries") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)

                val entry: APIResponse<Entry> = decodeFromString(response.content!!)
                val shouldEntry: APIResponse<Entry> = wrapFailure("not the right Parameters provided")
                assertEquals(shouldEntry, entry)
            }

            sendAuthenticatedRequest(
                HttpMethod.Post, "/entries",
                toJsonString(
                    Entry.In("Bafög", 700f, true, null)
                )
            ) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val entry: APIResponse<Entry> = decodeFromString(response.content!!)

                val id = transaction {
                    EntryEntity.all().last().let {
                        assertEquals("Bafög", it.name)
                        assertEquals(700f, it.amount)
                        assert(it.repeat)
                        assertEquals(it.user.category, it.category.id)
                        it.id.value
                    }
                }
                val shouldEntry = wrapSuccess(Entry(id, "Bafög", 700f, true, null))
                assertEquals(shouldEntry, entry)
            }
        }
    }

    @Test
    fun testGetEntries() {
        withCustomTestApplication(Application::mainModule) {
            loginUser()

            handleRequest(HttpMethod.Get, "/entries").apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
                assertNull(response.content)
            }

            val id = transaction { EntryEntity.all().first().id.value }

            val entryList = listOf(
                Entry(id, "Monthly Job Pay", 3000f, true, null),
                Entry(id + 1, "Monthly Job Pay", 3500f, true, null),
                Entry(id + 2, "Phone", -50f, true, null),
                Entry(id + 3, "Internet", -50f, true, null),
                Entry(id + 4, "Bike", -1500f, false, null),
                Entry(id + 5, "Ikea", -200f, false, null),
            )

            sendAuthenticatedRequest(HttpMethod.Get, "/entries") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Entry>> = decodeFromString(response.content!!)
                assert(response.success)
                assertEquals(6, response.data!!.size)
                val shouldResponse = wrapSuccess(entryList)
                assertEquals(shouldResponse, response)
            }

            sendAuthenticatedRequest(HttpMethod.Get, "/entries?current=true") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Entry>> = decodeFromString(response.content!!)
                assert(response.success)
                assertEquals(3, response.data!!.size)

                val currentList = listOf(entryList[1], entryList[3], entryList[5])
                val shouldResponse = wrapSuccess(currentList)
                assertEquals(shouldResponse, response)
            }

            sendAuthenticatedRequest(HttpMethod.Get, "/entries?period=50-8346") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Entry>> = decodeFromString(response.content!!)
                val shouldResponse: APIResponse<List<Entry>> = wrapFailure("period has not the right pattern")
                assertEquals(shouldResponse, response)
            }

            val now = LocalDateTime.now()

            sendAuthenticatedRequest(HttpMethod.Get, "/entries?period=${formatToPeriod(now)}") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Entry>> = decodeFromString(response.content!!)
                assert(response.success)
                assertEquals(3, response.data!!.size)

                val currentList = listOf(entryList[1], entryList[3], entryList[5])
                val shouldResponse = wrapSuccess(currentList)
                assertEquals(shouldResponse, response)
            }

            sendAuthenticatedRequest(HttpMethod.Get, "/entries?period=${formatToPeriod(now.minusMonths(1))}") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Entry>> = decodeFromString(response.content!!)
                assert(response.success)
                assertEquals(3, response.data!!.size)

                val currentList = listOf(entryList[1], entryList[3], entryList[4])
                val shouldResponse = wrapSuccess(currentList)
                assertEquals(shouldResponse, response)
            }

            sendAuthenticatedRequest(HttpMethod.Get, "/entries?period=${formatToPeriod(now.minusMonths(2))}") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Entry>> = decodeFromString(response.content!!)
                assert(response.success)
                assertEquals(3, response.data!!.size)

                val currentList = listOf(entryList[1], entryList[2], entryList[3])
                val shouldResponse = wrapSuccess(currentList)
                assertEquals(shouldResponse, response)
            }

            sendAuthenticatedRequest(HttpMethod.Get, "/entries?period=${formatToPeriod(now.minusMonths(3))}") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Entry>> = decodeFromString(response.content!!)
                assert(response.success)
                assertEquals(2, response.data!!.size)

                val currentList = listOf(entryList[0], entryList[2])
                val shouldResponse = wrapSuccess(currentList)
                assertEquals(shouldResponse, response)
            }

            sendAuthenticatedRequest(HttpMethod.Get, "/entries?period=${formatToPeriod(now.minusMonths(4))}") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Entry>> = decodeFromString(response.content!!)
                assert(response.success)
                assertEquals(0, response.data!!.size)

                val shouldResponse: APIResponse<List<Entry>> = wrapSuccess(emptyList())
                assertEquals(shouldResponse, response)
            }
        }
    }

    @Test
    @Ignore("Test Not implemented")
    fun testGetEntryById() {
        withCustomTestApplication(Application::mainModule) {
            loginUser()

            TODO()
        }
    }

    @Test
    @Ignore("Test Not implemented")
    fun testPatchEntry() {
        withCustomTestApplication(Application::mainModule) {
            loginUser()

            TODO()
        }
    }

    @Test
    @Ignore("Test Not implemented")
    fun testDeleteEntry() {
        withCustomTestApplication(Application::mainModule) {
            loginUser()

            TODO()
        }
    }
}
