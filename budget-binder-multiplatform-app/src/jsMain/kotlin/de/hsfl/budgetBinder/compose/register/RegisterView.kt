package de.hsfl.budgetBinder.compose.register


import androidx.compose.runtime.*
import de.hsfl.budgetBinder.presentation.UiState
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.dom.*

@Composable
fun RegisterView(
    state: State<Any>,
    onRegisterButtonPressed: (firstName: String, lastName: String, email: String, password: String) -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var firstNameTextFieldState by remember { mutableStateOf("") }
    var lastNameTextFieldState by remember { mutableStateOf("") }
    var emailTextFieldState by remember { mutableStateOf("") }
    var passwordTextFieldState by remember { mutableStateOf("") }
    val viewState by remember { state }

    // -- Register Form --
    Div(
        attrs = {
            classes()
            style {

            }
        }
    ) {
        Form(attrs = { //Probably possible with just a button OnClick instead of Form&Submit
            this.addEventListener("submit") {
                console.log("$firstNameTextFieldState, $lastNameTextFieldState, $emailTextFieldState, $passwordTextFieldState")
                onRegisterButtonPressed(firstNameTextFieldState,lastNameTextFieldState,emailTextFieldState, passwordTextFieldState)
                it.preventDefault()
            }
        }) {

            Label { Text("First Name") }
            Input(type = InputType.Text) {
                value(firstNameTextFieldState)
                onInput {
                    firstNameTextFieldState = it.value
                }
            }
            Label { Text("Last Name") }
            Input(type = InputType.Text) {
                value(lastNameTextFieldState)
                onInput {
                    lastNameTextFieldState = it.value
                }
            }
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
            SubmitInput(attrs = {
                value("Submit")
            })
        }

        // -- Register Request Management --
        when (viewState) {
            is UiState.Success<*> -> {
                onRegisterSuccess()
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