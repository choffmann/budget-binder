package de.hsfl.budgetBinder.client

import de.hsfl.budgetBinder.common.User
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*

class Client {
    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    suspend fun getUsers(): List<User> {
        return client.get("https://jsonplaceholder.typicode.com/users")
    }
}