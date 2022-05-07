package de.hsfl.budgetBinder.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import de.hsfl.budgetBinder.domain.use_case.auth_user.LoginUseCase
import de.hsfl.budgetBinder.presentation.LoginState
import de.hsfl.budgetBinder.presentation.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@Composable
fun LoginView() {
    val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val di = localDI()
    val loginUseCase: LoginUseCase by di.instance()
    val viewModel = LoginViewModel(loginUseCase, scope)
    val state by viewModel.state.collectAsState(scope)

    var emailTextFieldState by remember { mutableStateOf("") }
    var passwordTextFieldState by remember { mutableStateOf("") }

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val localFocusManager = LocalFocusManager.current

            OutlinedTextField(
                value = emailTextFieldState,
                onValueChange = { emailTextFieldState = it },
                label = { Text("Email") },
            )

            OutlinedTextField(
                value = passwordTextFieldState,
                onValueChange = { passwordTextFieldState = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            when(state) {
                is LoginState.Success -> {
                    Text((state as LoginState.Success).login.toString())
                }
                is LoginState.Error -> {
                    Text(text = (state as LoginState.Error).error,
                    color = MaterialTheme.colors.error,
                    fontWeight = FontWeight.Bold
                    )
                }
                is LoginState.Loading -> {
                    CircularProgressIndicator()
                }
            }

            Button(onClick = {
                localFocusManager.clearFocus()
                viewModel.auth(emailTextFieldState, passwordTextFieldState)

            }) {
                Text("Login")
            }
        }
    }
}