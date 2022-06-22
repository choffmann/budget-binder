package de.hsfl.budgetBinder.presentation.event

sealed class LifecycleEvent {
    // Call on LaunchEffect
    object OnLaunch: LifecycleEvent()

    // Call on DisposableEffect
    object OnDispose: LifecycleEvent()
}


/**
 * Callback on launch or on dispose
 * @param onLaunch Call on Launch
 * @param onDispose Call on Dispose
 */
fun LifecycleEvent.handleLifeCycle(onLaunch: () -> Unit, onDispose: () -> Unit) {
    when (this) {
        is LifecycleEvent.OnLaunch -> onLaunch()
        is LifecycleEvent.OnDispose -> onDispose()
    }
}
