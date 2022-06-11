package de.hsfl.budgetBinder.compose

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div


/*Main Container for every mayor layout*/
@Composable
fun MainFlexContainer(content: @Composable () -> Unit){
    Div (
        attrs = {
            classes()
            style {
                display(DisplayStyle("flex"))
            }
        }
            ){
        Div (attrs = {
            style {
                flex("25%")
            }})
        Div (attrs = {
            style {
                justifyContent(JustifyContent.Center)
                flex("50%")
            }})
        {
            content()
        }
        Div (attrs = {
            style {
                flex("25%")
            }})
    }
}