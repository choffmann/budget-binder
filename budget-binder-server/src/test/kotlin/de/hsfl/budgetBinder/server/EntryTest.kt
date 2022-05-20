package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.server.models.CategoryEntity
import de.hsfl.budgetBinder.server.models.EntryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import io.ktor.application.*
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
    @Ignore("Test Not implemented")
    fun testCreateEntry() {
        withCustomTestApplication(Application::mainModule) {
            loginUser()

            TODO()
        }
    }

    @Test
    @Ignore("Test Not implemented")
    fun testGetEntries() {
        withCustomTestApplication(Application::mainModule) {
            loginUser()

            TODO()
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