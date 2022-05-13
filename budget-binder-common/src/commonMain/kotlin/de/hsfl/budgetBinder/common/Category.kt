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

    @Serializable
    data class In(
        val name: String,
        val color: String,
        val image: Image = Image.DEFAULT,
        val budget: Float
    )

    @Serializable
    data class Patch(
        val name: String? = null,
        val color: String? = null,
        val image: Image? = null,
        val budget: Float? = null
    )
}