package de.hsfl.budgetBinder.compose.theme

import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
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


    //* MainFlexContainer *//
    //Container for flex elements, used in MainFlexContainer
    val flexContainer by style {
        display(DisplayStyle.Flex)
        justifyContent(JustifyContent.Center)
    }

    //Container for empty sides, used in MainFlexContainer
    val pufferFlexContainer by style {
        media(mediaMaxWidth(1000.px)) {
            self style {
                display(DisplayStyle.None)
            }
        }
        flex("25%")
    }

    //Container for main content, used in MainFlexContainer
    val contentFlexContainer by style {
        flex("50%")
        position(Position.Relative)
    }

    //Container for main content in BudgetBar
    val budgetBarContainer by style {
        flex("90%")
    }

    //Container for arrow in BudgetBar
    val arrowFlexContainer by style {
        flex("0.1 0.1 5%")
        height(auto)
    }


    val categoryImageList by style {
        justifyContent(JustifyContent.Center)
    }

    val text by style {
        textAlign("center")
        padding(10.px)
    }
    val leftText by style {
        textAlign("left")
        padding(10.px)
    }

    //EntryList
    val entryListElement by style {
        flexDirection(FlexDirection("row"))
        alignItems(AlignItems.Center)
        margin(10.px)
        marginTop(0.px)
    }
    val entryListElementText by style {
        flex("2 2 90%")
    }
    val moneyText by style {
        textAlign("center")
        padding(10.px)
        whiteSpace("nowrap")
    }
    val newEntryButton by style {
        position(Position.Fixed)
        bottom(16.px)
        marginRight(20.px)
    }
    val categoryListElement by style {
        flexDirection(FlexDirection("row"))
        alignItems(AlignItems.Center)
        margin(10.px)
        marginTop(0.px)
    }

    val categoryListElementText by style {
        flex("2 2 90%")
    }

    val imageFlexContainer by style {
        flex("0.1 0.1 0%")
        height(auto)
    }
    val colorFlexContainer by style {
        flex("0.1 0.1 5%")
        width(40.px)
        height(auto)
    }

    val card by style {
        margin(10.px)
        marginTop(25.px)
    }

    val image by style {
        padding(0.px)
    }
    val extraImagePadding by style {
        paddingLeft(8.px)
        paddingRight(8.px)
    }

    val margin by style {
        margin(10.px)
    }

    val marginRight by style {
        marginRight(1.percent)
    }

    val loadingImage by style {
        property("z-index", 1)
        width(20.percent)
        position(Position.Fixed)
        top(40.percent)
        left(40.percent)
    }

    val h1 by style {
        margin(2.percent)
    }

    val width by style {
        width(100.percent)
    }

    val flex50 by style {
        flex(50.percent)
    }

    val flex100 by style {
        flex(100.percent)
    }

    val buttonOverview by style {
        flex(33.percent)
        margin(1.5.percent)
    }
}
