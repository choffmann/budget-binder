package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.User

object TestUser {
    const val email = "test@test.com"
    const val password = "test-test"
    const val firstName = "test"
    const val surName = "Test"

    val userIn = User.In(firstName, surName, email, password)

    fun getTestUser(id: Int): User {
        return User(id, firstName, surName, email)
    }

    var accessToken: String? = null
}

object TestCategories {

    const val color = "111111"
    val image = Category.Image.SHOPPING

    data class TestCategory(
        val name: String,
        val budget: Float,
        val createdMonthOffset: Int,
        val endedMonthOffset: Int?
    )

    val categories = listOf(
        TestCategory("testCategory1", 1.0f, 0, null),
        TestCategory("testCategory2", 1.0f, 0, null),
        TestCategory("testCategory3", 1.0f, 0, null),
        TestCategory("testCategory4", 1.0f, 0, null),
        TestCategory("testCategory5", 1.0f, 0, null),
    )

    fun getTestCategory(id: Int, index: Int): Category {
        val testCategory = categories[index]
        return Category(
            id,
            testCategory.name,
            color,
            image,
            testCategory.budget
        )
    }
}
