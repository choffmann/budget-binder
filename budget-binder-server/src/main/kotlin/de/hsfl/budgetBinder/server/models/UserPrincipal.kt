package de.hsfl.budgetBinder.server.models

import io.ktor.server.auth.*


interface UserPrincipal : Principal {
    fun getUserID(): Int
    fun getUserTokenVersion(): Int
}
