package de.hsfl.budgetBinder.model

actual class Platform actual constructor() {
    actual val platform: String = System.getProperty("os.name")
}