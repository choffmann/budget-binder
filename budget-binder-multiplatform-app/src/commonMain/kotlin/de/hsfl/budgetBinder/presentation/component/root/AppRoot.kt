package de.hsfl.budgetBinder.presentation.component.root

import com.arkivanov.decompose.router.RouterState
import com.arkivanov.decompose.value.Value
import de.hsfl.budgetBinder.presentation.component.login.LoginView
import de.hsfl.budgetBinder.presentation.component.user.UserView

interface AppRoot {
    val routerState: Value<RouterState<*, Child>>

    sealed class Child {
        data class Login(val component: LoginView) : Child()
        data class User(val component: UserView) : Child()
    }
}