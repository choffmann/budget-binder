package de.hsfl.budgetBinder.presentation.component.user

import com.arkivanov.decompose.ComponentContext
import com.badoo.reaktive.base.Consumer
import com.badoo.reaktive.base.invoke
import de.hsfl.budgetBinder.presentation.component.user.UserView.Output

class UserViewComponent(componentContext: ComponentContext, private val output: Consumer<Output>) : UserView,
    ComponentContext by componentContext {
    override fun onLogOut() {
        output(Output.LoggedOut)
    }
}