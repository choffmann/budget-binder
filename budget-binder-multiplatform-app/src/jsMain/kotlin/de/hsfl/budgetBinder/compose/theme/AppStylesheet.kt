package de.hsfl.budgetBinder.compose.theme

import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.selectors.CSSSelector

/*All Information found about Stylesheets:
https://github.com/JetBrains/compose-jb/blob/master/tutorials/Web/Style_Dsl/README.md
*/
object AppStylesheet : StyleSheet() {

    init {
        // `universal` can be used instead of "*": `universal style {}`
        "*" style {
            fontSize(15.px)
            padding(0.px)
        }

        // raw selector
        "h1, h2, h3, h4, h5, h6" style {
            property("font-family", "Arial, Helvetica, sans-serif")

        }

        // combined selector
        type("A") + attr( // selects all tags <a> with href containing 'jetbrains'
            name = "href",
            value = "jetbrains",
            operator = CSSSelector.Attribute.Operator.Equals
        ) style {
            fontSize(25.px)
        }
    }

    // A convenient way to create a class selector
    // AppStylesheet.container can be used as a class in component attrs

    val container by style {
        flex(50.percent)
    }

    val flexContainer by style {
        display(DisplayStyle.Flex)
    }

    val card by style {
        marginTop(25.px)
    }

    val image by style {
        padding(0.px)
    }

    val margin by style {
        margin(10.px)
    }
}