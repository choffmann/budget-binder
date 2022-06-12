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
    enum class Image { //Should be regrouped, sorted
        DEFAULT,
        CHECKMARK,
        WRONG,
        SHOPPINGCART,
        SHOPPINGBASKET,
        FOOD,
        FASTFOOD,
        RESTAURANT,
        MONEY,
        HOME,
        FAMILY,
        HEALTH,
        MEDICATION,
        KEYBOARD,
        PRINTER,
        INVEST,
        SPORT,
        CLOTH,
        GIFT,
        WEALTH,
        FLOWER,
        PET,
        BILLS,
        WATER,
        FIRE,
        STAR,
        SAVINGS,
        CAR,
        BIKE,
        TRAIN,
        MOTORCYCLE,
        MOPED,
        ELECTRONICS,
        BOOK,
        FLIGHT,
        WORK,
        MOON,
        LOCK,
        PHONE,
        STORE,
        BAR,
        FOREST,
        HARDWARE,
        PEST


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