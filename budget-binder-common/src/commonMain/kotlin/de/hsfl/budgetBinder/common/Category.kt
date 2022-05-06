package de.hsfl.budgetBinder.common

data class Category(
    val name: String,
    val color: Color,
    val image: Image,
    val budget: Float,
    val user_id: Int
) {
    enum class Color {

    }

    enum class Image {

    }
}