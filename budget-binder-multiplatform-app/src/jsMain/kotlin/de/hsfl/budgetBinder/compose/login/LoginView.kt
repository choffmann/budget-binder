package de.hsfl.budgetBinder.compose.login

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.presentation.UiState
import org.jetbrains.compose.web.dom.*

@Composable
fun LoginView(
    state: State<Any>,
    onLoginButtonPressed: (email: String, password: String) -> Unit,
    onLoginSuccess: () -> Unit
){
    var emailTextFieldState by remember { mutableStateOf("") }
    var passwordTextFieldState by remember { mutableStateOf("") }
    val viewState by remember { state }

    // -- Login Form --
    Div(
        attrs = {
            classes()
            style {

            }
        }
    ) {
        Form(attrs = {
            this.addEventListener("submit") {
                console.log("${emailTextFieldState}, ${passwordTextFieldState}")
                onLoginButtonPressed(emailTextFieldState, passwordTextFieldState)
                it.preventDefault()
            }
        })
        {
            Label { Text("Email") }
            EmailInput(value = emailTextFieldState,
                attrs = {
                    onInput {
                        emailTextFieldState = it.value
                    }
                })
            Label { Text("Password") }
            PasswordInput(value = passwordTextFieldState,
                attrs = {
                    onInput {
                        passwordTextFieldState = it.value
                    }
                })
            SubmitInput (attrs = {
                value("Submit")
            })
        }

        // -- Login Request Management --
        when (viewState) {
            is UiState.Success<*> -> {
                onLoginSuccess()
            }
            is UiState.Error -> {
                Text((viewState as UiState.Error).error)
            }
            is UiState.Loading -> {
                //CircularProgressIndicator()
            }
            else -> {}
        }



    }
}
