package de.hsfl.budgetBinder.presentation.component.login

import com.arkivanov.decompose.ComponentContext
import com.badoo.reaktive.base.Consumer
import com.badoo.reaktive.base.invoke
import de.hsfl.budgetBinder.presentation.component.login.LoginView.Output

class LoginViewComponent(
    componentContext: ComponentContext,
    private val output: Consumer<Output>
) : LoginView,
    ComponentContext by componentContext {

    override fun onLoginSuccess() {
        output(Output.LoggedIn)
    }

    override fun login(username: String, password: String) {
        TODO("Not yet implemented")
    }
}