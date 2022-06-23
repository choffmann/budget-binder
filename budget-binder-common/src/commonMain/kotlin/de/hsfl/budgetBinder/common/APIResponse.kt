package de.hsfl.budgetBinder.common

import kotlinx.serialization.Serializable

@Serializable
data class APIResponse<T>(
    val error: ErrorModel? = null,
    val data: T? = null,
    val success: Boolean = false
)
