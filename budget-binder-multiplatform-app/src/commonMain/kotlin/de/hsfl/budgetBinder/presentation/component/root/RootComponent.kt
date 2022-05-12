package de.hsfl.budgetBinder.presentation.component.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.RouterState
import com.arkivanov.decompose.router.pop
import com.arkivanov.decompose.router.push
import com.arkivanov.decompose.router.router
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.badoo.reaktive.base.Consumer
import de.hsfl.budgetBinder.presentation.component.login.LoginView
import de.hsfl.budgetBinder.presentation.component.login.LoginViewComponent
import de.hsfl.budgetBinder.presentation.component.user.UserView
import de.hsfl.budgetBinder.presentation.component.user.UserViewComponent
import de.hsfl.budgetBinder.presentation.component.root.AppRoot.Child
import de.hsfl.budgetBinder.utils.Consumer

class RootComponent internal constructor(
    componentContext: ComponentContext,
    private val loginView: (ComponentContext, Consumer<LoginView.Output>) -> LoginView,
    private val userView: (ComponentContext, Consumer<UserView.Output>) -> UserView,
) : AppRoot, ComponentContext by componentContext {
    constructor(componentContext: ComponentContext) : this(
        componentContext = componentContext,
        loginView = { childContext, output ->
            LoginViewComponent(componentContext = childContext, output = output)
        },
        userView = { childContext, output ->
            UserViewComponent(componentContext = childContext, output = output)
        }
    )

    private val router =
        router<Configuration, Child>(
            initialConfiguration = Configuration.Login,
            handleBackButton = true,
            childFactory = ::createChild
        )

    override val routerState: Value<RouterState<*, Child>> = router.state

    private fun createChild(configuration: Configuration, componentContext: ComponentContext): Child =
        when(configuration) {
            is Configuration.Login -> Child.Login(loginView(componentContext, Consumer(::onLoginOutput)))
            is Configuration.User -> Child.User(userView(componentContext, Consumer(::onLogoutOutput)))
        }

    private fun onLoginOutput(output: LoginView.Output): Unit =
        when (output) {
            is LoginView.Output.LoggedIn -> router.push(Configuration.User)
            else -> {}
        }

    private fun onLogoutOutput(output: UserView.Output): Unit =
        when (output) {
            is UserView.Output.LoggedOut -> router.pop()
        }

    private sealed class Configuration : Parcelable {
        @Parcelize
        object Login : Configuration()

        @Parcelize
        object User : Configuration()
    }
}