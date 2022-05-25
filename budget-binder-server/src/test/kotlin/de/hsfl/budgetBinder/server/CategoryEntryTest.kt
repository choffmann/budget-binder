package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.server.models.CategoryEntity
import de.hsfl.budgetBinder.server.models.EntryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import kotlin.test.*

class CategoryEntryTest {

    @BeforeTest
    fun before() = customTestApplication { client ->
        registerUser(client)

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


    @AfterTest
    fun after() = transaction {
        EntryEntity.all().forEach { it.delete() }
        UserEntity.all().forEach {
            CategoryEntity[it.category!!].delete()
            it.delete()
        }
        CategoryEntity.all().forEach { it.delete() }
    }


    @Test
    fun testGetEntriesByCategory() = customTestApplicationWithLogin { client ->
        client.get("/categories/1/entries").let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<List<Entry>> = response.body()
            val shouldResponse: APIResponse<List<Entry>> = wrapFailure("Your accessToken is absent or does not match.")
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(client, HttpMethod.Get, "/categories/test/entries") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Entry>> = response.body()
            val shouldResponse: APIResponse<List<Entry>> = wrapFailure("path parameter is not a number")
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(client, HttpMethod.Get, "/categories/5000/entries") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Entry>> = response.body()
            val shouldResponse: APIResponse<List<Entry>> = wrapFailure("Category not found")
            assertEquals(shouldResponse, responseBody)
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

        sendAuthenticatedRequest(client, HttpMethod.Get, "/categories/${categoryId - 1}/entries") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Entry>> = response.body()
            val shouldResponse: APIResponse<List<Entry>> = wrapFailure("Category not found")
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(client, HttpMethod.Get, "/categories/$categoryId/entries") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Entry>> = response.body()
            val shouldResponse = wrapSuccess(listOf(entryList[0]))
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(client, HttpMethod.Get, "/categories/${categoryId + 1}/entries") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Entry>> = response.body()
            val shouldResponse = wrapSuccess(listOf(entryList[1], entryList[2], entryList[3]))
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(client, HttpMethod.Get, "/categories/null/entries") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Entry>> = response.body()
            val shouldResponse = wrapSuccess(listOf(entryList[4]))
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(client, HttpMethod.Get, "/entries?current=true") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<List<Entry>> = response.body()
            val shouldResponse = wrapSuccess(listOf(entryList[1], entryList[2], entryList[4]))
            assertEquals(shouldResponse, responseBody)
        }
    }


    @Test
    fun createEntryWithCategory() = customTestApplicationWithLogin { client ->
        val categoryId = transaction { CategoryEntity.all().first().id.value + 2 }

        sendAuthenticatedRequestWithBody(
            client,
            HttpMethod.Post, "/entries",
            Entry.In("Second Phone", -50f, true, 5000)
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()

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
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequestWithBody(
            client,
            HttpMethod.Post, "/entries",
            Entry.In("Second Phone", -50f, true, categoryId)
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()

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
            assertEquals(shouldResponse, responseBody)
        }

    }

    @Test
    fun testChangeCategoryInEntry() = customTestApplicationWithLogin { client ->
        val categoryId = transaction { CategoryEntity.all().first().id.value + 2 }
        val entryId = transaction { EntryEntity.all().last().id.value }

        sendAuthenticatedRequestWithBody(
            client,
            HttpMethod.Patch, "/entries/$entryId",
            Entry.Patch(category = Entry.Category(categoryId - 1))
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()
            val shouldResponse: APIResponse<Entry> = wrapFailure("you can't change this Entry")
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequestWithBody(
            client,
            HttpMethod.Patch, "/entries/$entryId",
            Entry.Patch(category = Entry.Category(categoryId))
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()

            transaction {
                assertEquals(categoryId, EntryEntity[entryId].category.id.value)
            }
            val shouldResponse = wrapSuccess(Entry(entryId, "Monthly Pay", 3000f, true, categoryId))
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequestWithBody(
            client,
            HttpMethod.Patch, "/entries/$entryId",
            Entry.Patch(category = Entry.Category(5000))
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Entry> = response.body()

            transaction {
                EntryEntity[entryId].let {
                    assertEquals(it.user.category, it.category.id)
                }
            }
            val shouldResponse = wrapSuccess(Entry(entryId, "Monthly Pay", 3000f, true, null))
            assertEquals(shouldResponse, responseBody)
        }
    }


    @Test
    fun testChangeOldCategoryHasOldEntries() = customTestApplicationWithLogin { client ->
        val categoryId = transaction { CategoryEntity.all().first().id.value + 2 }
        val entryId = transaction { EntryEntity.all().first().id.value + 1 }

        sendAuthenticatedRequestWithBody(
            client,
            HttpMethod.Patch, "/categories/$categoryId",
            Category.Patch(budget = 200f)
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()

            val id = transaction {
                val oldCategory = CategoryEntity[categoryId]
                assertNotNull(oldCategory.ended)
                assertNotNull(oldCategory.child)
                val newCategory = CategoryEntity[oldCategory.child!!]

                assertEquals(100f, oldCategory.budget)
                assertEquals(200f, newCategory.budget)

                val oldInternetEntry = EntryEntity[entryId]
                val oldPhoneEntry = EntryEntity[entryId + 1]
                val oldPhoneOneTimeEntry = EntryEntity[entryId + 2]

                assertNotNull(oldInternetEntry.child)
                assertNotNull(oldInternetEntry.ended)
                val newInternetEntry = EntryEntity[oldInternetEntry.child!!]

                assertNotNull(oldPhoneEntry.child)
                assertNotNull(oldPhoneEntry.ended)
                val newPhoneEntry = EntryEntity[oldPhoneEntry.child!!]

                assertEquals(oldInternetEntry.name, newInternetEntry.name)
                assertEquals(oldInternetEntry.repeat, newInternetEntry.repeat)
                assertNotEquals(oldInternetEntry.category, newInternetEntry.category)
                assertEquals(newInternetEntry.id, oldInternetEntry.child)
                assertNull(newInternetEntry.child)
                assertNull(newInternetEntry.ended)

                assertEquals(oldPhoneEntry.name, newPhoneEntry.name)
                assertEquals(oldPhoneEntry.repeat, newPhoneEntry.repeat)
                assertNotEquals(oldPhoneEntry.category, newPhoneEntry.category)
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
            assertEquals(shouldResponse, responseBody)
        }
    }


    @Test
    fun testChangeOldCategoryHasNewEntries() = customTestApplicationWithLogin { client ->
        val categoryId = transaction { CategoryEntity.all().first().id.value + 2 }
        transaction {
            val userEntity = UserEntity.all().first()
            val categoryEntity = CategoryEntity[categoryId]

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
                created = LocalDateTime.now()
                ended = null
                child = null
                user = userEntity
                category = categoryEntity
            }
        }

        val entryId = transaction { EntryEntity.all().last().id.value - 1 }

        sendAuthenticatedRequestWithBody(
            client,
            HttpMethod.Patch, "/categories/$categoryId",
            Category.Patch(budget = 200f)
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()

            val id = transaction {
                val oldCategory = CategoryEntity[categoryId]
                assertNotNull(oldCategory.ended)
                assertNotNull(oldCategory.child)
                val newCategory = CategoryEntity[oldCategory.child!!]

                assertEquals(100f, oldCategory.budget)
                assertEquals(200f, newCategory.budget)

                val mobileEntry = EntryEntity[entryId]
                val mobileOneEntry = EntryEntity[entryId + 1]

                assertNull(mobileEntry.child)
                assertNull(mobileEntry.ended)
                assert(mobileEntry.repeat)
                assertEquals(newCategory, mobileEntry.category)

                assertNull(mobileOneEntry.child)
                assertNull(mobileOneEntry.ended)
                assert(!mobileOneEntry.repeat)
                assertEquals(newCategory, mobileOneEntry.category)

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
            assertEquals(shouldResponse, responseBody)
        }
    }


    @Test
    fun testChangeOldCategoryHasOnlyNewEntries() = customTestApplicationWithLogin { client ->
        val categoryId = transaction { CategoryEntity.all().first().id.value + 2 }
        transaction {
            val userEntity = UserEntity.all().first()
            val categoryEntity = CategoryEntity[categoryId]

            categoryEntity.entries.forEach { it.delete() }

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
                created = LocalDateTime.now()
                ended = null
                child = null
                user = userEntity
                category = categoryEntity
            }
        }

        val entryId = transaction { EntryEntity.all().last().id.value - 1 }

        sendAuthenticatedRequestWithBody(
            client,
            HttpMethod.Patch, "/categories/$categoryId",
            Category.Patch(budget = 200f)
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()

            val id = transaction {
                val categoryEntity = CategoryEntity[categoryId]
                assertNull(categoryEntity.ended)
                assertNull(categoryEntity.child)

                assertEquals(200f, categoryEntity.budget)

                val mobileEntry = EntryEntity[entryId]
                val mobileOneEntry = EntryEntity[entryId + 1]

                assertNull(mobileEntry.child)
                assertNull(mobileEntry.ended)
                assert(mobileEntry.repeat)

                assertEquals(categoryEntity, mobileEntry.category)
                assertEquals(categoryEntity, mobileOneEntry.category)

                assertNull(mobileOneEntry.child)
                assertNull(mobileOneEntry.ended)
                assert(!mobileOneEntry.repeat)

                categoryEntity.id.value
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
            assertEquals(shouldResponse, responseBody)
        }
    }


    @Test
    fun testChangeNewCategoryHasNewEntries() = customTestApplicationWithLogin { client ->
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

        sendAuthenticatedRequestWithBody(
            client,
            HttpMethod.Patch, "/categories/$categoryId",
            Category.Patch(budget = 200f)
        ) { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()

            transaction {
                val categoryEntity = CategoryEntity[categoryId]
                assertNull(categoryEntity.ended)
                assertNull(categoryEntity.child)
                assertEquals(200f, categoryEntity.budget)

                val mobileEntry = EntryEntity[entryId]
                val mobileOneEntry = EntryEntity[entryId + 1]

                assertNull(mobileEntry.child)
                assertNull(mobileEntry.ended)
                assert(mobileEntry.repeat)

                assertEquals(categoryEntity, mobileEntry.category)
                assertEquals(categoryEntity, mobileOneEntry.category)

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
            assertEquals(shouldResponse, responseBody)
        }
    }


    @Test
    fun testDeleteOldCategoryHasOldEntries() = customTestApplicationWithLogin { client ->
        val categoryId = transaction { CategoryEntity.all().first().id.value + 2 }
        val entryId = transaction { EntryEntity.all().first().id.value + 1 }

        sendAuthenticatedRequest(client, HttpMethod.Delete, "/categories/$categoryId") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()

            transaction {
                val categoryEntity = CategoryEntity.findById(categoryId)
                assertNotNull(categoryEntity)
                assertNotNull(categoryEntity.ended)
                assertNull(categoryEntity.child)

                val oldInternetEntry = EntryEntity[entryId]
                val oldPhoneEntry = EntryEntity[entryId + 1]
                val oldPhoneOneTimeEntry = EntryEntity[entryId + 2]
                assertNull(oldPhoneOneTimeEntry.child)
                assertNull(oldPhoneOneTimeEntry.ended)

                assertNotNull(oldInternetEntry.child)
                assertNotNull(oldInternetEntry.ended)
                val newInternetEntry = EntryEntity[oldInternetEntry.child!!]
                assertNull(newInternetEntry.child)
                assertNull(newInternetEntry.ended)

                assertNotNull(oldPhoneEntry.child)
                assertNotNull(oldPhoneEntry.ended)
                val newPhoneEntry = EntryEntity[oldPhoneEntry.child!!]
                assertNull(newPhoneEntry.child)
                assertNull(newPhoneEntry.ended)

                assertEquals(categoryEntity, oldInternetEntry.category)
                assertEquals(categoryEntity, oldPhoneEntry.category)
                assertEquals(newInternetEntry.user.category, newInternetEntry.category.id)
                assertEquals(newPhoneEntry.user.category, newPhoneEntry.category.id)

                assertEquals(categoryEntity, oldPhoneOneTimeEntry.category)
            }

            val shouldResponse = wrapSuccess(
                Category(
                    categoryId,
                    "Internet-Phone",
                    TestCategories.color,
                    TestCategories.image,
                    100f
                )
            )
            assertEquals(shouldResponse, responseBody)
        }
    }


    @Test
    fun testDeleteOldCategoryHasNewEntries() = customTestApplicationWithLogin { client ->
        val categoryId = transaction { CategoryEntity.all().first().id.value + 2 }
        transaction {
            val userEntity = UserEntity.all().first()
            val categoryEntity = CategoryEntity[categoryId]

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
                created = LocalDateTime.now()
                ended = null
                child = null
                user = userEntity
                category = categoryEntity
            }
        }

        val entryId = transaction { EntryEntity.all().last().id.value - 1 }

        sendAuthenticatedRequest(client, HttpMethod.Delete, "/categories/$categoryId") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()

            transaction {
                val categoryEntity = CategoryEntity.findById(categoryId)
                assertNotNull(categoryEntity)
                assertNotNull(categoryEntity.ended)
                assertNull(categoryEntity.child)

                val mobileEntry = EntryEntity[entryId]
                val mobileOneEntry = EntryEntity[entryId + 1]

                assertNull(mobileEntry.child)
                assertNull(mobileEntry.ended)
                assertNull(mobileOneEntry.child)
                assertNull(mobileOneEntry.ended)

                assertEquals(mobileEntry.user.category, mobileEntry.category.id)
                assertEquals(mobileOneEntry.user.category, mobileOneEntry.category.id)
            }

            val shouldResponse = wrapSuccess(
                Category(
                    categoryId,
                    "Internet-Phone",
                    TestCategories.color,
                    TestCategories.image,
                    100f
                )
            )
            assertEquals(shouldResponse, responseBody)
        }
    }


    @Test
    fun testDeleteOldCategoryHasOnlyNewEntries() = customTestApplicationWithLogin { client ->
        val categoryId = transaction { CategoryEntity.all().first().id.value + 2 }
        transaction {
            val userEntity = UserEntity.all().first()
            val categoryEntity = CategoryEntity[categoryId]

            categoryEntity.entries.forEach { it.delete() }

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
                created = LocalDateTime.now()
                ended = null
                child = null
                user = userEntity
                category = categoryEntity
            }
        }

        val entryId = transaction { EntryEntity.all().last().id.value - 1 }

        sendAuthenticatedRequest(client, HttpMethod.Delete, "/categories/$categoryId") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()

            transaction {
                val categoryEntity = CategoryEntity.findById(categoryId)
                assertNull(categoryEntity)

                val mobileEntry = EntryEntity[entryId]
                val mobileOneEntry = EntryEntity[entryId + 1]

                assertNull(mobileEntry.child)
                assertNull(mobileEntry.ended)
                assertNull(mobileOneEntry.child)
                assertNull(mobileOneEntry.ended)

                assertEquals(mobileEntry.user.category, mobileEntry.category.id)
                assertEquals(mobileOneEntry.user.category, mobileOneEntry.category.id)
            }

            val shouldResponse = wrapSuccess(
                Category(
                    categoryId,
                    "Internet-Phone",
                    TestCategories.color,
                    TestCategories.image,
                    100f
                )
            )
            assertEquals(shouldResponse, responseBody)
        }
    }


    @Test
    fun testDeleteNewCategoryHasNewEntries() = customTestApplicationWithLogin { client ->
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

        sendAuthenticatedRequest(client, HttpMethod.Delete, "/categories/$categoryId") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<Category> = response.body()

            transaction {
                val categoryEntity = CategoryEntity.findById(categoryId)
                assertNull(categoryEntity)

                val mobileEntry = EntryEntity[entryId]
                val mobileOneEntry = EntryEntity[entryId + 1]

                assertNull(mobileEntry.child)
                assertNull(mobileEntry.ended)
                assertNull(mobileOneEntry.child)
                assertNull(mobileOneEntry.ended)

                assertEquals(mobileEntry.user.category, mobileEntry.category.id)
                assertEquals(mobileOneEntry.user.category, mobileOneEntry.category.id)
            }

            val shouldResponse = wrapSuccess(
                Category(
                    categoryId,
                    "Mobile",
                    TestCategories.color,
                    TestCategories.image,
                    50f
                )
            )
            assertEquals(shouldResponse, responseBody)
        }
    }
}
