package de.hsfl.budgetBinder.common

import kotlinx.serialization.Serializable

// https://jsonplaceholder.typicode.com/posts

@Serializable
data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)
