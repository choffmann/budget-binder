package de.hsfl.budgetBinder.domain.use_case

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.domain.repository.CategoryRepository
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CategoriesUseCase(
    private val repository: CategoryRepository
) {
    fun getAllCategories(): Flow<DataResponse<List<Category>>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.getAllCategories().let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: emit(DataResponse.Error(response.error!!.message))
            }
        } catch (e: IOException) {
            emit(DataResponse.Error("Couldn't reach the server"))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataResponse.Error(e.message.toString()))
        }
    }

    fun getAllCategories(period: String): Flow<DataResponse<Category>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.getAllCategories(period).let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: emit(DataResponse.Error(response.error!!.message))
            }
        } catch (e: IOException) {
            emit(DataResponse.Error("Couldn't reach the server"))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataResponse.Error(e.message.toString()))
        }
    }

    fun createNewCategory(category: Category.In): Flow<DataResponse<Category>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.createNewCategory(category).let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: emit(DataResponse.Error(response.error!!.message))
            }
        } catch (e: IOException) {
            emit(DataResponse.Error("Couldn't reach the server"))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataResponse.Error(e.message.toString()))
        }
    }

    fun getCategoryById(id: Int): Flow<DataResponse<Category>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.getCategoryById(id).let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: emit(DataResponse.Error(response.error!!.message))
            }
        } catch (e: IOException) {
            emit(DataResponse.Error("Couldn't reach the server"))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataResponse.Error(e.message.toString()))
        }
    }

    fun changeCategoryById(category: Category.In, id: Int): Flow<DataResponse<Category>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.changeCategoryById(category, id).let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: emit(DataResponse.Error(response.error!!.message))
            }
        } catch (e: IOException) {
            emit(DataResponse.Error("Couldn't reach the server"))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataResponse.Error(e.message.toString()))
        }
    }

    fun removeCategoryById(id: Int): Flow<DataResponse<Category>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.removeCategoryById(id).let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: emit(DataResponse.Error(response.error!!.message))
            }
        } catch (e: IOException) {
            emit(DataResponse.Error("Couldn't reach the server"))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataResponse.Error(e.message.toString()))
        }
    }

    fun getAllEntriesFromCategory(id: Int): Flow<DataResponse<List<Category>>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.getEntriesFromCategory(id).let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: emit(DataResponse.Error(response.error!!.message))
            }
        } catch (e: IOException) {
            emit(DataResponse.Error("Couldn't reach the server"))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataResponse.Error(e.message.toString()))
        }
    }
}