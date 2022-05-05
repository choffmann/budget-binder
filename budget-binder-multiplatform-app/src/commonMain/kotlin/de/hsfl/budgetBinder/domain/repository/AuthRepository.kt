package de.hsfl.budgetBinder.domain.repository

interface AuthRepository {
    suspend fun authorize(username: String, password: String)
}