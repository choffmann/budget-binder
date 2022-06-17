package de.hsfl.budgetBinder.presentation

import androidx.compose.runtime.Composable
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
actual fun CategoryImageToIcon(icon: Category.Image) {
    Div(attrs = {classes(AppStylesheet.imageFlexContainer)}){
        Span(
        attrs = {
            classes("material-icons")
            style { padding(8.px) }
        }
    ) {
        Text(
            when (icon) {
                Category.Image.DEFAULT -> "check_box_outline_blank"
                Category.Image.SHOPPINGCART -> "shopping_cart"
                Category.Image.SHOPPINGBASKET -> "shopping_basket"
                Category.Image.CHECKMARK -> "done"
                Category.Image.WRONG -> "dangerous"
                Category.Image.HOME -> "home"
                Category.Image.FOOD -> "bakery_dining"
                Category.Image.FASTFOOD -> "bakery_dining"
                Category.Image.RESTAURANT -> "restaurant"
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
                Category.Image.KEYBOARD -> "redeem"
                Category.Image.PRINTER -> "print"
                Category.Image.WATER -> "water_drop"
                Category.Image.FIRE -> "fire"
                Category.Image.STAR -> "grade"
                Category.Image.SAVINGS -> "savings"
                Category.Image.CAR -> "minor_crash"
                Category.Image.BIKE -> "pedal_bike"
                Category.Image.TRAIN -> "directions_transit"
                Category.Image.MOPED -> "moped"
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
                Category.Image.PEST -> "pest_control"
            }
        )
    }}
}
