package de.hsfl.budgetBinder.common

import kotlinx.serialization.Serializable

@Serializable
data class APIResponse<T>(
    val data: T,
    val error: ErrorModel = ErrorModel(error = false),
    val success: Boolean = true
)