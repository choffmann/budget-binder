package de.hsfl.budgetBinder.server.entries

import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.utils.customTestApplication
import de.hsfl.budgetBinder.server.utils.registerUser
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

class PatchEntryTests {

    @BeforeTest
    fun before() = customTestApplication { client ->
        client.registerUser()
    }

    @AfterTest
    fun after() = transaction {
        UserEntity.all().forEach { it.delete() }
    }


}
