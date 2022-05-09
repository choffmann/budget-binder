package de.hsfl.budgetBinder.common

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Int,
    val name: String,
    val color: String,
    val image: Image,
    val budget: Float
) {
    enum class Image {
        DEFAULT,
        SHOPPING
    }
}