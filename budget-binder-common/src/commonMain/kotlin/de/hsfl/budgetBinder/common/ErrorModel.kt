package de.hsfl.budgetBinder.common

import kotlinx.serialization.Serializable

@Serializable
data class ErrorModel(val message: String, val code: Int = 200)
