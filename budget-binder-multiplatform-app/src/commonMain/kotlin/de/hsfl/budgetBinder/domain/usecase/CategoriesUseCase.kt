package de.hsfl.budgetBinder.domain.usecase

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.domain.repository.CategoryRepository
import io.ktor.http.*
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

    fun categories(period: String): Flow<DataResponse<List<Category>>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.getAllCategories(period).let { response ->
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
}

class CreateCategoryUseCase(private val repository: CategoryRepository) {
    operator fun invoke(category: Category.In): Flow<DataResponse<Category>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.createNewCategory(category).let { response ->
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
}

class GetCategoryByIdUseCase(private val repository: CategoryRepository) {
    operator fun invoke(id: Int): Flow<DataResponse<Category>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.getCategoryById(id).let { response ->
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
}

class ChangeCategoryByIdUseCase(private val repository: CategoryRepository) {
    operator fun invoke(category: Category.Patch, id: Int): Flow<DataResponse<Category>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.changeCategoryById(category, id).let { response ->
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
}

class DeleteCategoryByIdUseCase(private val repository: CategoryRepository) {
    operator fun invoke(id: Int): Flow<DataResponse<Category>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.deleteCategoryById(id).let { response ->
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
}

class GetAllEntriesByCategoryUseCase(private val repository: CategoryRepository) {
    operator fun invoke(id: Int?): Flow<DataResponse<List<Entry>>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.getEntriesFromCategory(id).let { response ->
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
}
