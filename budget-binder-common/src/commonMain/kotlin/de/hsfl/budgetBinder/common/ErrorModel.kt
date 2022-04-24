package de.hsfl.budgetBinder.common

import kotlinx.serialization.Serializable

@Serializable
data class ErrorModel(val error: Boolean = false, val message: String? = null)
