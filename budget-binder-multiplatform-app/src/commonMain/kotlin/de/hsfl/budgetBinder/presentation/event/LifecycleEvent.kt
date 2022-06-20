package de.hsfl.budgetBinder.presentation.event

sealed class LifecycleEvent {
    object OnLaunch: LifecycleEvent()
    object OnDispose: LifecycleEvent()
}

fun LifecycleEvent.handleLifeCycle(onLaunch: () -> Unit, onDispose: () -> Unit) {
    when (this) {
        is LifecycleEvent.OnLaunch -> onLaunch()
        is LifecycleEvent.OnDispose -> onDispose()
    }
}
