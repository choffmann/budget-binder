package de.hsfl.budgetBinder.compose.register


import androidx.compose.runtime.*

@Composable
fun RegisterView(
    state: State<Any>,
    onRegisterButtonPressed: (firstName: String, lastName: String, email: String, password: String) -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var firstNameTextState by remember { mutableStateOf("") }
    var lastNameTextState by remember { mutableStateOf("") }
    var emailTextState by remember { mutableStateOf("") }
    var passwordTextState by remember { mutableStateOf("") }
    val viewState by remember { state }



}