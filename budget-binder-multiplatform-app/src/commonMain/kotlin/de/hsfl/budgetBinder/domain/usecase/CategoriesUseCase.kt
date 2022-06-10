package de.hsfl.budgetBinder.domain.usecase

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.domain.repository.CategoryRepository
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAllCategoriesUseCase(private val repository: CategoryRepository) {
    fun categories(): Flow<DataResponse<List<Category>>> = flow {
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

    fun categories(period: String): Flow<DataResponse<Category>> = flow {
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
}

class CreateCategoryUseCase(private val repository: CategoryRepository) {
    operator fun invoke(category: Category.In): Flow<DataResponse<Category>> = flow {
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
}

class GetCategoryByIdUseCase(private val repository: CategoryRepository) {
    operator fun invoke(id: Int): Flow<DataResponse<Category>> = flow {
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
}

class ChangeCategoryByIdUseCase(private val repository: CategoryRepository) {
    operator fun invoke(category: Category.In, id: Int): Flow<DataResponse<Category>> = flow {
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
}

class RemoveCategoryByIdUseCase(private val repository: CategoryRepository) {
    operator fun invoke(id: Int): Flow<DataResponse<Category>> = flow {
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
}

class GetAllEntriesByCategoryUseCase(private val repository: CategoryRepository) {
    operator fun invoke(id: Int): Flow<DataResponse<List<Category>>> = flow {
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