package de.hsfl.budgetBinder.presentation

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import de.hsfl.budgetBinder.common.Category

@Composable
actual fun CategoryToIcon(icon: Category.Image) {
    when (icon) {
        Category.Image.DEFAULT -> Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
        Category.Image.SHOPPINGCART -> Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
        Category.Image.SHOPPINGBASKET -> Icon(imageVector = Icons.Default.ShoppingBasket, contentDescription = null)
        Category.Image.CHECKMARK -> Icon(imageVector = Icons.Default.Done, contentDescription = null)
        Category.Image.WRONG -> Icon(imageVector = Icons.Default.Dangerous, contentDescription = null)
        Category.Image.HOME -> Icon(imageVector = Icons.Default.Home, contentDescription = null)
        Category.Image.FOOD -> Icon(imageVector = Icons.Default.BakeryDining, contentDescription = null)
        Category.Image.FASTFOOD -> Icon(imageVector = Icons.Default.BakeryDining, contentDescription = null)
        else -> {
            Icon(imageVector = Icons.Default.QuestionAnswer, contentDescription = null)
        }
        /*Category.Image.RESTAURANT -> "restaurant"
        Category.Image.FAMILY -> "people"
        Category.Image.MONEY -> "payments"
        Category.Image.HEALTH -> "health_and_safety"
        Category.Image.MEDICATION -> "medication"
        Category.Image.INVEST -> "query_stats"
        Category.Image.SPORT -> "sports_soccer"
        Category.Image.CLOTH -> "checkroom"
        Category.Image.GIFT -> "redeem"
        Category.Image.WEALTH -> "monetization_on"
        Category.Image.FLOWER -> "local_florist"
        Category.Image.PET -> "pets"
        Category.Image.BILLS -> "receipt"
        Category.Image.KEYBOARD-> "redeem"
        Category.Image.PRINTER-> "print"
        Category.Image.WATER -> "water_drop"
        Category.Image.FIRE -> "fire"
        Category.Image.STAR -> "grade"
        Category.Image.SAVINGS -> "savings"
        Category.Image.CAR -> "minor_crash"
        Category.Image.BIKE -> "pedal_bike"
        Category.Image.TRAIN -> "directions_transit"
        Category.Image.MOPED-> "moped"
        Category.Image.MOTORCYCLE -> "two_wheeler"
        Category.Image.ELECTRONICS -> "electrical_services"
        Category.Image.BOOK -> "book"
        Category.Image.FLIGHT -> "flight_takeoff"
        Category.Image.WORK -> "work"
        Category.Image.MOON -> "nightlight_round"
        Category.Image.LOCK -> "https"
        Category.Image.PHONE -> "perm_phone_msg"
        Category.Image.STORE -> "store_mall_directory"
        Category.Image.BAR -> "nightlife"
        Category.Image.FOREST -> "forest"
        Category.Image.HARDWARE -> "hardware"
        Category.Image.PEST -> "pest_control"*/
    }
}