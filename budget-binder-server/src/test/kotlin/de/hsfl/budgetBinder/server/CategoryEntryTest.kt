package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.server.models.CategoryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import io.ktor.application.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

class CategoryEntryTest {

    @BeforeTest
    fun before() {
        withCustomTestApplication(Application::mainModule) {
            registerUser()


        }
    }

    @AfterTest
    fun after() {
        withCustomTestApplication(Application::mainModule) {
            transaction {
                UserEntity.all().forEach {
                    CategoryEntity[it.category!!].delete()
                    it.delete()
                }
            }
        }
    }

    @Test
    @Ignore("Test Not implemented")
    fun test() {
        withCustomTestApplication(Application::mainModule) {
            loginUser()

            TODO()
        }
    }
}