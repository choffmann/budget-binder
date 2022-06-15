package de.hsfl.budgetBinder.common

sealed class DataResponse<T>(val data: T? = null, val error: ErrorModel? = null) {
    class Success<T>(data: T) : DataResponse<T>(data)
    class Error<T>(error: ErrorModel?, data: T? = null) : DataResponse<T>(data, error)
    class Loading<T>(data: T? = null) : DataResponse<T>(data)
    class Unauthorized<T>(error: ErrorModel? = null, data: T? = null) : DataResponse<T>(data, error)
}
