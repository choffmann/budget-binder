package de.hsfl.budgetBinder.domain.usecase.storage

import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StoreServerUrlUseCase {
    operator fun invoke(serverUrl: Url): Flow<Url> = flow {
        emit(serverUrl)
    }
}