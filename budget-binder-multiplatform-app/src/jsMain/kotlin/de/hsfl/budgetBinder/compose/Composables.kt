package de.hsfl.budgetBinder.compose

import androidx.compose.runtime.Composable
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div


/*Main Container for every mayor layout*/
@Composable
fun MainFlexContainer(content: @Composable () -> Unit){
    Div (
        attrs = {
            classes("mdc-top-app-bar--fixed-adjust", AppStylesheet.flexContainer)
        }
            ){
        Div (attrs = { classes(AppStylesheet.pufferFlexContainer) })
        Div (attrs = { classes(AppStylesheet.contentFlexContainer)})
        {
            content()
        }
        Div (attrs = { classes(AppStylesheet.pufferFlexContainer)})
    }
}