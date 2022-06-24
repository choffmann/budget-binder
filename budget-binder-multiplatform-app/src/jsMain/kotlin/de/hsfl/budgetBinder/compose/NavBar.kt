package de.hsfl.budgetBinder.compose

import androidx.compose.runtime.Composable
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.viewmodel.navdrawer.NavDrawerEvent
import de.hsfl.budgetBinder.presentation.viewmodel.navdrawer.NavDrawerViewModel
import di
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.dom.*
import org.kodein.di.instance

//Top Navigation Bar
@Composable
fun NavBar(content: @Composable () -> Unit) {
    val viewModel: NavDrawerViewModel by di.instance()
    Header(
        attrs = {
            classes("mdc-top-app-bar")
        }
    ) {
        Div(
            attrs = {
                classes("mdc-top-app-bar__row")
            }
        ) {
            Section(
                attrs = {
                    classes("mdc-top-app-bar__section", "mdc-top-app-bar__section--align-start")
                }
            ) {
                Img(
                    src = "images/Logo.png", alt = "Logo", attrs = {
                        classes("mdc-icon-button", AppStylesheet.image)
                        onClick { viewModel.onEvent(NavDrawerEvent.OnDashboard) }
                    }
                )
                Span(
                    attrs = {
                        classes("mdc-top-app-bar__title")
                    }
                ) {
                    Text("Budget-Binder")
                }
            }
            Section(
                attrs = {
                    classes("mdc-top-app-bar__section", "mdc-top-app-bar__section--align-end")
                }
            ) {
                Button(
                    attrs = {
                        classes(
                            "mdc-button",
                            "mdc-button--raised",
                            "mdc-top-app-bar__navigation-icon",
                            AppStylesheet.marginRight
                        )
                        onClick { viewModel.onEvent(NavDrawerEvent.OnCreateEntry) }
                    }
                ) {
                    Span(
                        attrs = {
                            classes("mdc-button__label")
                        }
                    ) {
                        Text("New Entry")
                    }
                }
                Button(
                    attrs = {
                        classes(
                            "mdc-button",
                            "mdc-button--raised",
                            "mdc-top-app-bar__navigation-icon",
                            AppStylesheet.marginRight
                        )
                        onClick { viewModel.onEvent(NavDrawerEvent.OnDashboard) }
                    }
                ) {
                    Span(
                        attrs = {
                            classes("mdc-button__label")
                        }
                    ) {
                        Text("Dashboard")
                    }
                }
                Button(
                    attrs = {
                        classes(
                            "mdc-button",
                            "mdc-button--raised",
                            "mdc-top-app-bar__navigation-icon",
                            AppStylesheet.marginRight
                        )
                        onClick { viewModel.onEvent(NavDrawerEvent.OnCategory) }
                    }
                ) {
                    Span(
                        attrs = {
                            classes("mdc-button__label")
                        }
                    ) {
                        Text("Categories")
                    }
                }
                Button(
                    attrs = {
                        classes(
                            "mdc-button",
                            "mdc-button--raised",
                            "mdc-top-app-bar__navigation-icon",
                            AppStylesheet.marginRight
                        )
                        onClick { viewModel.onEvent(NavDrawerEvent.OnSettings) }
                    }
                ) {
                    Span(
                        attrs = {
                            classes("mdc-button__label")
                        }
                    ) {
                        Text("Settings")
                    }
                }
                Button(
                    attrs = {
                        classes(
                            "mdc-button",
                            "mdc-button--raised",
                            "mdc-top-app-bar__navigation-icon"
                        )
                        onClick { viewModel.onEvent(NavDrawerEvent.OnLogout) }
                        style { backgroundColor(Color("#b00020")) }
                    }
                ) {
                    Span(
                        attrs = {
                            classes("mdc-button__label")
                        }
                    ) {
                        Text("Logout")
                    }
                }
            }
        }
    }
    content()
}
