package de.hsfl.budgetBinder.common

import kotlinx.serialization.Serializable

@Serializable
data class APIResponse<T>(val data: T?, val error: String? = null, val success: Boolean = true)
