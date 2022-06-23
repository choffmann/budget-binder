package de.hsfl.budgetBinder.server

import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.test.*

class BaseRoutesTests {

    @Test
    fun testGetOpenApi() = customTestApplication { client ->
        client.get("/openapi.json").let { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(ContentType.Application.Json, response.contentType())
        }
    }

    @Test
    fun testFavicon() = customTestApplication { client ->
        client.get("/favicon.ico").let { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(ContentType.Image.XIcon, response.contentType())
        }
    }

    @Test
    fun testDocs() = customTestApplication { client ->
        client.get("/docs").let { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(ContentType.Text.Html.withCharset(Charsets.UTF_8), response.contentType())
        }
    }
}
