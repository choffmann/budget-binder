package de.hsfl.budgetBinder.common

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Int,
    val name: String,
    val color: Color,
    val image: Image,
    val budget: Float
) {
    enum class Color {
        RED
    }

    enum class Image {
        SHOPPING
    }
}