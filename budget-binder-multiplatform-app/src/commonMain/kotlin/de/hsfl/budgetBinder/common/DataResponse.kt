package de.hsfl.budgetBinder.common

import de.hsfl.budgetBinder.domain.usecase.NavigateToScreenUseCase
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.event.UiEvent
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

sealed class DataResponse<T>(val data: T? = null, val error: ErrorModel? = null) {
    class Success<T>(data: T) : DataResponse<T>(data)
    class Error<T>(error: ErrorModel?, data: T? = null) : DataResponse<T>(data, error)
    class Loading<T>(data: T? = null) : DataResponse<T>(data)
    class Unauthorized<T>(error: ErrorModel? = null, data: T? = null) : DataResponse<T>(data, error)
}

fun <T> DataResponse<T>.handleDataResponse(
    scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob()),
    routerFlow: RouterFlow = RouterFlow(NavigateToScreenUseCase(), scope),
    onSuccess: (T) -> Unit
) = scope.launch {
    when (this@handleDataResponse) {
        is DataResponse.Error -> UiEventSharedFlow.mutableEventFlow.emit(UiEvent.ShowError(this@handleDataResponse.error!!.message))
        is DataResponse.Loading -> UiEventSharedFlow.mutableEventFlow.emit(UiEvent.ShowLoading)
        is DataResponse.Success -> {
            UiEventSharedFlow.mutableEventFlow.emit(UiEvent.HideSuccess)
            onSuccess(this@handleDataResponse.data!!)
        }
        is DataResponse.Unauthorized -> {
            routerFlow.navigateTo(Screen.Login)
            UiEventSharedFlow.mutableEventFlow.emit(UiEvent.ShowError(this@handleDataResponse.error!!.message))
        }
    }
}
