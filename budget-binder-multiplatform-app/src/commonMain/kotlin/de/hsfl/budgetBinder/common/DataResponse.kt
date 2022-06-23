package de.hsfl.budgetBinder.common

import de.hsfl.budgetBinder.domain.usecase.NavigateToScreenUseCase
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.event.UiEvent
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

sealed class DataResponse<T> {
    class Success<T>(val data: T) : DataResponse<T>()
    class Error<T>(val error: ErrorModel) : DataResponse<T>()
    class Loading<T> : DataResponse<T>()
    class Unauthorized<T>(val error: ErrorModel) : DataResponse<T>()

    suspend fun <out: T> handleDataResponse(
        scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob()),
        routerFlow: RouterFlow = RouterFlow(NavigateToScreenUseCase(), scope),
        onSuccess: suspend (T) -> Unit,
        onError: suspend (ErrorModel) -> Unit = {
            UiEventSharedFlow.mutableEventFlow.emit(UiEvent.ShowError(it.message))
        },
        onLoading: suspend () -> Unit = {
            UiEventSharedFlow.mutableEventFlow.emit(UiEvent.ShowLoading)
        },
        onUnauthorized: suspend (ErrorModel) -> Unit = {
            routerFlow.navigateTo(Screen.Login)
            UiEventSharedFlow.mutableEventFlow.emit(UiEvent.ShowError(it.message))
        }
    ) = when (this) {
        is Error -> onError(this.error)
        is Loading -> onLoading()
        is Success -> {
            UiEventSharedFlow.mutableEventFlow.emit(UiEvent.HideSuccess)
            onSuccess(this.data)
        }
        is Unauthorized -> onUnauthorized(this.error)
    }
}
