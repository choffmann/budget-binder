package de.hsfl.budgetBinder.domain.usecase

import de.hsfl.budgetBinder.common.*
import de.hsfl.budgetBinder.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAllCategoriesUseCase(private val repository: CategoryRepository) {
    operator fun invoke(period: String? = null): Flow<DataResponse<List<Category>>> = flow {
        useCaseHelper {
            period?.let {
                repository.getAllCategories(period = it)
            } ?: repository.getAllCategories()
        }
    }
}

class CreateCategoryUseCase(private val repository: CategoryRepository) {
    operator fun invoke(category: Category.In): Flow<DataResponse<Category>> = flow {
        useCaseHelper { repository.createNewCategory(category) }
    }
}

class GetCategoryByIdUseCase(private val repository: CategoryRepository) {
    operator fun invoke(id: Int): Flow<DataResponse<Category>> = flow {
        useCaseHelper { repository.getCategoryById(id) }
    }
}

class ChangeCategoryByIdUseCase(private val repository: CategoryRepository) {
    operator fun invoke(category: Category.Patch, id: Int): Flow<DataResponse<Category>> = flow {
        useCaseHelper { repository.changeCategoryById(category, id) }
    }
}


class DeleteCategoryByIdUseCase(private val repository: CategoryRepository) {
    operator fun invoke(id: Int): Flow<DataResponse<Category>> = flow {
        useCaseHelper { repository.deleteCategoryById(id) }
    }
}

class GetAllEntriesByCategoryUseCase(private val repository: CategoryRepository) {
    operator fun invoke(id: Int?, period: String? = null): Flow<DataResponse<List<Entry>>> = flow {
        useCaseHelper {
            period?.let {
                repository.getEntriesFromCategory(id, period)
            } ?: repository.getEntriesFromCategory(id)
        }
    }
}
