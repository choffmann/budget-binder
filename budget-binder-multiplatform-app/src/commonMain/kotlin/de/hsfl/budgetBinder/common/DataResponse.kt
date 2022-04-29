package de.hsfl.budgetBinder.common

sealed class DataResponse<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : DataResponse<T>(data)
    class Error<T>(message: String, data: T? = null) : DataResponse<T>(data, message)
    class Loading<T>(data: T? = null) : DataResponse<T>(data)
}
