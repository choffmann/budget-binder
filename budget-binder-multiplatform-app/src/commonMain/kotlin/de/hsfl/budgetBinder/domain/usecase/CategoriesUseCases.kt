package de.hsfl.budgetBinder.domain.usecase

data class CategoriesUseCases(
    val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    val getCategoryByIdUseCase: GetCategoryByIdUseCase,
    val createCategoryUseCase: CreateCategoryUseCase,
    val changeCategoryByIdUseCase: ChangeCategoryByIdUseCase,
    val deleteCategoryByIdUseCase: DeleteCategoryByIdUseCase,
    val getAllEntriesByCategoryUseCase: GetAllEntriesByCategoryUseCase
)
