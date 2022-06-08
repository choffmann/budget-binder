package de.hsfl.budgetBinder.compose.login

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.*

@Composable
fun LoginView(state: State<Any>,
              onLoginButtonPressed: (email: String, password: String) -> Unit,
              onLoginSuccess: () -> Unit){
    var emailTextFieldState by remember { mutableStateOf("") }
    var passwordTextFieldState by remember { mutableStateOf("") }
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
            EmailInput(value = emailTextFieldState,
                attrs = {
                    onInput {
                        emailTextFieldState = it.value
                    }
                })
            PasswordInput(value = passwordTextFieldState,
                attrs = {
                    onInput {
                        passwordTextFieldState = it.value
                    }
                })
            SubmitInput {  }
        }

    }
}
