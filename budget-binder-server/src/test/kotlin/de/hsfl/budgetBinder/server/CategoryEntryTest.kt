package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Category
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

class CategoryEntryTest {

    @BeforeTest
    fun before() {
        withCustomTestApplication(Application::mainModule) {
            registerUser()

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
                    category = internetCategory
                }

                EntryEntity.new {
                    name = "Internet"
                    amount = -50f
                    repeat = true
                    created = now.minusMonths(2)
                    ended = null
                    child = null
                    user = userEntity
                    category = internetPhoneCategory
                }.let { internetEntry.child = it.id }

                EntryEntity.new {
                    name = "Phone"
                    amount = -50f
                    repeat = true
                    created = now.minusMonths(2)
                    ended = null
                    child = null
                    user = userEntity
                    category = internetPhoneCategory
                }

                EntryEntity.new {
                    name = "Phone one Time"
                    amount = -250f
                    repeat = false
                    created = now.minusMonths(2)
                    ended = null
                    child = null
                    user = userEntity
                    category = internetPhoneCategory
                }

                EntryEntity.new {
                    name = "Monthly Pay"
                    amount = 3000f
                    repeat = true
                    created = now.minusMonths(3)
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
                EntryEntity.all().forEach { it.delete() }
                UserEntity.all().forEach {
                    CategoryEntity[it.category!!].delete()
                    it.delete()
                }
                CategoryEntity.all().forEach { it.delete() }
            }
        }
    }

    @Test
    fun testGetEntriesByCategory() {
        withCustomTestApplication(Application::mainModule) {
            loginUser()

            handleRequest(HttpMethod.Get, "/categories/1/entries").apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
                assertNull(response.content)
            }

            sendAuthenticatedRequest(HttpMethod.Get, "/categories/test/entries") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Entry>> = decodeFromString(response.content!!)
                val shouldResponse: APIResponse<List<Entry>> = wrapFailure("path parameter is not a number")
                assertEquals(shouldResponse, response)
            }

            sendAuthenticatedRequest(HttpMethod.Get, "/categories/5000/entries") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Entry>> = decodeFromString(response.content!!)
                val shouldResponse: APIResponse<List<Entry>> = wrapFailure("Category not found")
                assertEquals(shouldResponse, response)
            }

            val categoryId = transaction { CategoryEntity.all().first().id.value + 1 }
            val entryId = transaction { EntryEntity.all().first().id.value }

            val entryList = listOf(
                Entry(entryId, "Internet", -50f, true, categoryId),
                Entry(entryId + 1, "Internet", -50f, true, categoryId + 1),
                Entry(entryId + 2, "Phone", -50f, true, categoryId + 1),
                Entry(entryId + 3, "Phone one Time", -250f, false, categoryId + 1),
                Entry(entryId + 4, "Monthly Pay", 3000f, true, null),
            )

            sendAuthenticatedRequest(HttpMethod.Get, "/categories/${categoryId - 1}/entries") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Entry>> = decodeFromString(response.content!!)
                val shouldResponse: APIResponse<List<Entry>> = wrapFailure("Category not found")
                assertEquals(shouldResponse, response)
            }

            sendAuthenticatedRequest(HttpMethod.Get, "/categories/$categoryId/entries") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Entry>> = decodeFromString(response.content!!)
                val shouldResponse = wrapSuccess(listOf(entryList[0]))
                assertEquals(shouldResponse, response)
            }

            sendAuthenticatedRequest(HttpMethod.Get, "/categories/${categoryId + 1}/entries") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Entry>> = decodeFromString(response.content!!)
                val shouldResponse = wrapSuccess(listOf(entryList[1], entryList[2], entryList[3]))
                assertEquals(shouldResponse, response)
            }

            sendAuthenticatedRequest(HttpMethod.Get, "/categories/null/entries") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Entry>> = decodeFromString(response.content!!)
                val shouldResponse = wrapSuccess(listOf(entryList[4]))
                assertEquals(shouldResponse, response)
            }

            sendAuthenticatedRequest(HttpMethod.Get, "/entries?current=true") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<List<Entry>> = decodeFromString(response.content!!)
                val shouldResponse = wrapSuccess(listOf(entryList[1], entryList[2], entryList[4]))
                assertEquals(shouldResponse, response)
            }
        }
    }

    @Test
    fun createEntryWithCategory() {
        withCustomTestApplication(Application::mainModule) {
            loginUser()
            val categoryId = transaction { CategoryEntity.all().first().id.value + 2 }

            sendAuthenticatedRequest(
                HttpMethod.Post, "/entries",
                toJsonString(Entry.In("Second Phone", -50f, true, 5000))
            ) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<Entry> = decodeFromString(response.content!!)

                val id = transaction {
                    EntryEntity.all().last().let {
                        assertEquals("Second Phone", it.name)
                        assertEquals(-50f, it.amount)
                        assert(it.repeat)
                        assertEquals(it.user.category, it.category.id)
                        it.id.value
                    }
                }
                val shouldResponse = wrapSuccess(Entry(id, "Second Phone", -50f, true, null))
                assertEquals(shouldResponse, response)
            }

            sendAuthenticatedRequest(
                HttpMethod.Post, "/entries",
                toJsonString(Entry.In("Second Phone", -50f, true, categoryId))
            ) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<Entry> = decodeFromString(response.content!!)

                val id = transaction {
                    EntryEntity.all().last().let {
                        assertEquals("Second Phone", it.name)
                        assertEquals(-50f, it.amount)
                        assert(it.repeat)
                        assertEquals(categoryId, it.category.id.value)
                        it.id.value
                    }
                }
                val shouldResponse = wrapSuccess(Entry(id, "Second Phone", -50f, true, categoryId))
                assertEquals(shouldResponse, response)
            }
        }
    }

    @Test
    fun testChangeCategoryInEntry() {
        withCustomTestApplication(Application::mainModule) {
            loginUser()

            val categoryId = transaction { CategoryEntity.all().first().id.value + 2 }
            val entryId = transaction { EntryEntity.all().last().id.value }

            sendAuthenticatedRequest(
                HttpMethod.Patch, "/entries/$entryId",
                toJsonString(Entry.Patch(category = Entry.Category(categoryId - 1)))
            ) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<Entry> = decodeFromString(response.content!!)
                val shouldResponse: APIResponse<Entry> = wrapFailure("you can't change this Entry")
                assertEquals(shouldResponse, response)
            }

            sendAuthenticatedRequest(
                HttpMethod.Patch, "/entries/$entryId",
                toJsonString(Entry.Patch(category = Entry.Category(categoryId)))
            ) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<Entry> = decodeFromString(response.content!!)

                transaction {
                    assertEquals(categoryId, EntryEntity[entryId].category.id.value)
                }
                val shouldResponse = wrapSuccess(Entry(entryId, "Monthly Pay", 3000f, true, categoryId))
                assertEquals(shouldResponse, response)
            }

            sendAuthenticatedRequest(
                HttpMethod.Patch, "/entries/$entryId",
                toJsonString(Entry.Patch(category = Entry.Category(5000)))
            ) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<Entry> = decodeFromString(response.content!!)

                transaction {
                    EntryEntity[entryId].let {
                        assertEquals(it.user.category, it.category.id)
                    }
                }
                val shouldResponse = wrapSuccess(Entry(entryId, "Monthly Pay", 3000f, true, null))
                assertEquals(shouldResponse, response)
            }
        }
    }

    @Test
    fun testChangeOldCategoryHasOldEntries() {
        withCustomTestApplication(Application::mainModule) {
            loginUser()

            val categoryId = transaction { CategoryEntity.all().first().id.value + 2 }
            val entryId = transaction { EntryEntity.all().first().id.value + 1 }

            sendAuthenticatedRequest(
                HttpMethod.Patch, "/categories/$categoryId",
                toJsonString(Category.Patch(budget = 200f))
            ) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<Category> = decodeFromString(response.content!!)

                val id = transaction {
                    val oldCategory = CategoryEntity[categoryId]
                    assertNotNull(oldCategory.ended)
                    assertNotNull(oldCategory.child)
                    val newCategory = CategoryEntity[oldCategory.child!!]

                    val oldInternetEntry = EntryEntity[entryId]
                    val oldPhoneEntry = EntryEntity[entryId + 1]
                    val oldPhoneOneTimeEntry = EntryEntity[entryId + 2]

                    val newId = EntryEntity.all().last().id.value - 1
                    val newInternetEntry = EntryEntity[newId]
                    val newPhoneEntry = EntryEntity[newId + 1]

                    assertEquals(oldInternetEntry.name, newInternetEntry.name)
                    assertEquals(oldInternetEntry.repeat, newInternetEntry.repeat)
                    assertNotEquals(oldInternetEntry.category, newInternetEntry.category)
                    assertNotNull(oldInternetEntry.child)
                    assertNotNull(oldInternetEntry.ended)
                    assertEquals(newInternetEntry.id, oldInternetEntry.child)
                    assertNull(newInternetEntry.child)
                    assertNull(newInternetEntry.ended)

                    assertEquals(oldPhoneEntry.name, newPhoneEntry.name)
                    assertEquals(oldPhoneEntry.repeat, newPhoneEntry.repeat)
                    assertNotEquals(oldPhoneEntry.category, newPhoneEntry.category)
                    assertNotNull(oldPhoneEntry.child)
                    assertNotNull(oldPhoneEntry.ended)
                    assertEquals(oldPhoneEntry.child, newPhoneEntry.id)
                    assertNull(newPhoneEntry.child)
                    assertNull(newPhoneEntry.ended)

                    assert(!oldPhoneOneTimeEntry.repeat)
                    assertNull(oldPhoneOneTimeEntry.child)
                    assertNull(oldPhoneOneTimeEntry.ended)

                    newCategory.id.value
                }

                val shouldResponse =
                    wrapSuccess(Category(id, "Internet-Phone", TestCategories.color, TestCategories.image, 200f))
                assertEquals(shouldResponse, response)
            }
        }
    }

    @Test
    fun testChangeOldCategoryHasNewEntries() {
        withCustomTestApplication(Application::mainModule) {
            loginUser()

            val categoryId = transaction { CategoryEntity.all().first().id.value + 2 }
            transaction {
                val userEntity = UserEntity.all().first()

                EntryEntity.new {
                    name = "Mobile"
                    amount = -50f
                    repeat = true
                    created = LocalDateTime.now()
                    ended = null
                    child = null
                    user = userEntity
                    category = CategoryEntity[categoryId]
                }.id.value

                EntryEntity.new {
                    name = "Mobile One"
                    amount = -250f
                    repeat = false
                    created = LocalDateTime.now()
                    ended = null
                    child = null
                    user = userEntity
                    category = CategoryEntity[categoryId]
                }
            }

            val entryId = transaction { EntryEntity.all().last().id.value - 1 }

            sendAuthenticatedRequest(
                HttpMethod.Patch, "/categories/$categoryId",
                toJsonString(Category.Patch(budget = 200f))
            ) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<Category> = decodeFromString(response.content!!)

                val id = transaction {
                    val oldCategory = CategoryEntity[categoryId]
                    assertNotNull(oldCategory.ended)
                    assertNotNull(oldCategory.child)
                    val newCategory = CategoryEntity[oldCategory.child!!]

                    val mobileEntry = EntryEntity[entryId]
                    val mobileOneEntry = EntryEntity[entryId + 1]

                    assertNull(mobileEntry.child)
                    assertNull(mobileEntry.ended)
                    assert(mobileEntry.repeat)

                    assertNull(mobileOneEntry.child)
                    assertNull(mobileOneEntry.ended)
                    assert(!mobileOneEntry.repeat)

                    newCategory.id.value
                }

                val shouldResponse = wrapSuccess(
                    Category(
                        id,
                        "Internet-Phone",
                        TestCategories.color,
                        TestCategories.image,
                        200f
                    )
                )
                assertEquals(shouldResponse, response)
            }
        }
    }

    @Test
    fun testChangeNewCategoryHasNewEntries() {
        withCustomTestApplication(Application::mainModule) {
            loginUser()

            transaction {
                val userEntity = UserEntity.all().first()
                val now = LocalDateTime.now()

                val categoryEntity = CategoryEntity.new {
                    name = "Mobile"
                    color = TestCategories.color
                    image = TestCategories.image
                    budget = 50f
                    created = now
                    ended = null
                    child = null
                    user = userEntity
                }

                EntryEntity.new {
                    name = "Mobile"
                    amount = -50f
                    repeat = true
                    created = LocalDateTime.now()
                    ended = null
                    child = null
                    user = userEntity
                    category = categoryEntity
                }.id.value

                EntryEntity.new {
                    name = "Mobile One"
                    amount = -250f
                    repeat = false
                    created = now
                    ended = null
                    child = null
                    user = userEntity
                    category = categoryEntity
                }
            }

            val categoryId = transaction { CategoryEntity.all().last().id.value }
            val entryId = transaction { EntryEntity.all().last().id.value - 1 }

            sendAuthenticatedRequest(
                HttpMethod.Patch, "/categories/$categoryId",
                toJsonString(Category.Patch(budget = 200f))
            ) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val response: APIResponse<Category> = decodeFromString(response.content!!)

                transaction {
                    val categoryEntity = CategoryEntity[categoryId]
                    assertNull(categoryEntity.ended)
                    assertNull(categoryEntity.child)

                    val mobileEntry = EntryEntity[entryId]
                    val mobileOneEntry = EntryEntity[entryId + 1]

                    assertNull(mobileEntry.child)
                    assertNull(mobileEntry.ended)
                    assert(mobileEntry.repeat)

                    assertNull(mobileOneEntry.child)
                    assertNull(mobileOneEntry.ended)
                    assert(!mobileOneEntry.repeat)
                }

                val shouldResponse = wrapSuccess(
                    Category(
                        categoryId,
                        "Mobile",
                        TestCategories.color,
                        TestCategories.image,
                        200f
                    )
                )
                assertEquals(shouldResponse, response)
            }
        }
    }

}
