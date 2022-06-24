package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.*
import de.hsfl.budgetBinder.server.utils.customTestApplication
import de.hsfl.budgetBinder.server.utils.wrapFailure
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlin.test.*

class UnauthorizedTests {
    private suspend inline fun <reified T> HttpResponse.checkUnauthorized() {
        assertEquals(HttpStatusCode.Unauthorized, this.status)
        val responseBody: APIResponse<T> = this.body()
        val shouldResponse: APIResponse<T> = wrapFailure("Your accessToken is absent or does not match.", 401)
        assertEquals(shouldResponse, responseBody)
    }

    @Test
    fun testLogout() = customTestApplication { client ->
        client.get("/logout").checkUnauthorized<AuthToken>()
    }

    @Test
    fun testGetMe() = customTestApplication { client ->
        client.get("/me").checkUnauthorized<User>()
    }

    @Test
    fun testPatchMe() = customTestApplication { client ->
        client.patch("/me").checkUnauthorized<User>()
    }

    @Test
    fun testDeleteMe() = customTestApplication { client ->
        client.delete("/me").checkUnauthorized<User>()
    }

    @Test
    fun testGetCategories() = customTestApplication { client ->
        client.get("/categories").checkUnauthorized<List<Category>>()
    }

    @Test
    fun testPostCategory() = customTestApplication { client ->
        client.post("/categories").checkUnauthorized<Category>()
    }

    @Test
    fun testGetCategoryByID() = customTestApplication { client ->
        client.get("/categories/1").checkUnauthorized<Category>()
    }

    @Test
    fun testPatchCategoryByID() = customTestApplication { client ->
        client.patch("/categories/1").checkUnauthorized<Category>()
    }

    @Test
    fun testDeleteCategoryByID() = customTestApplication { client ->
        client.delete("/categories/1").checkUnauthorized<Category>()
    }

    @Test
    fun testGetEntriesByCategoryID() = customTestApplication { client ->
        client.get("/categories/1/entries").checkUnauthorized<List<Entry>>()
    }

    @Test
    fun testGetEntries() = customTestApplication { client ->
        client.get("/entries").checkUnauthorized<List<Entry>>()
    }

    @Test
    fun testPostEntry() = customTestApplication { client ->
        client.post("/entries").checkUnauthorized<Entry>()
    }

    @Test
    fun testGetEntryByID() = customTestApplication { client ->
        client.get("/entries/1").checkUnauthorized<Entry>()
    }

    @Test
    fun testPatchEntryByID() = customTestApplication { client ->
        client.patch("/entries/1").checkUnauthorized<Entry>()
    }

    @Test
    fun testDeleteEntryByID() = customTestApplication { client ->
        client.delete("/entries/1").checkUnauthorized<Entry>()
    }
}
