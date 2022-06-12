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
    val image = Category.Image.DEFAULT

}
