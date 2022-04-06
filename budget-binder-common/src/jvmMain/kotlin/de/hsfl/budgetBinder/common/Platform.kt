package de.hsfl.budgetBinder.common

// Platform anhängige implementierung
actual class Platform actual constructor() {
    actual val platform: String = "System" //System.getProperty("os.name")
}