package de.hsfl.budgetBinder.domain.usecase

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.ErrorModel
import io.ktor.http.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.flow.FlowCollector

suspend fun <T> FlowCollector<DataResponse<T>>.useCaseHelper(callback: suspend () -> APIResponse<T>) {
    try {
        emit(DataResponse.Loading())
        callback().let { response ->
            response.data?.let {
                emit(DataResponse.Success(it))
            } ?: response.error!!.let { error ->
                when (error.code) {
                    HttpStatusCode.Unauthorized.value -> emit(DataResponse.Unauthorized(error))
                    else -> emit(DataResponse.Error(error))
                }
            }
        }
    } catch (e: IOException) {
        emit(DataResponse.Error(ErrorModel("Couldn't reach the server")))
    } catch (e: Exception) {
        e.printStackTrace()
        emit(DataResponse.Error(ErrorModel("Something went wrong")))
    }
}
