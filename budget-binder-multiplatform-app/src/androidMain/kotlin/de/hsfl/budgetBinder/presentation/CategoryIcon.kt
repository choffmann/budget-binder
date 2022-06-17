package de.hsfl.budgetBinder.presentation

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import de.hsfl.budgetBinder.common.Category

@Composable
actual fun CategoryImageToIcon(icon: Category.Image) {
    when (icon) {
        Category.Image.DEFAULT -> Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
        Category.Image.SHOPPINGCART -> Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
        Category.Image.SHOPPINGBASKET -> Icon(imageVector = Icons.Default.ShoppingBasket, contentDescription = null)
        Category.Image.CHECKMARK -> Icon(imageVector = Icons.Default.Done, contentDescription = null)
        Category.Image.WRONG -> Icon(imageVector = Icons.Default.Dangerous, contentDescription = null)
        Category.Image.HOME -> Icon(imageVector = Icons.Default.Home, contentDescription = null)
        Category.Image.FOOD -> Icon(imageVector = Icons.Default.BakeryDining, contentDescription = null)
        Category.Image.FASTFOOD -> Icon(imageVector = Icons.Default.Fastfood, contentDescription = null)
        Category.Image.RESTAURANT -> Icon(imageVector = Icons.Default.Restaurant, contentDescription = null)
        Category.Image.FAMILY -> Icon(imageVector = Icons.Default.People, contentDescription = null)
        Category.Image.MONEY -> Icon(imageVector = Icons.Default.Payments, contentDescription = null)
        Category.Image.HEALTH -> Icon(imageVector = Icons.Default.HealthAndSafety, contentDescription = null)
        Category.Image.MEDICATION -> Icon(imageVector = Icons.Default.Medication, contentDescription = null)
        Category.Image.INVEST -> Icon(imageVector = Icons.Default.QueryStats, contentDescription = null)
        Category.Image.SPORT -> Icon(imageVector = Icons.Default.SportsSoccer, contentDescription = null)
        Category.Image.CLOTH -> Icon(imageVector = Icons.Default.Checkroom, contentDescription = null)
        Category.Image.GIFT -> Icon(imageVector = Icons.Default.Redeem, contentDescription = null)
        Category.Image.WEALTH -> Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null)
        Category.Image.FLOWER -> Icon(imageVector = Icons.Default.LocalFlorist, contentDescription = null)
        Category.Image.PET -> Icon(imageVector = Icons.Default.Pets, contentDescription = null)
        Category.Image.BILLS -> Icon(imageVector = Icons.Default.Receipt, contentDescription = null)
        Category.Image.KEYBOARD -> Icon(imageVector = Icons.Default.Redeem, contentDescription = null)
        Category.Image.PRINTER -> Icon(imageVector = Icons.Default.Print, contentDescription = null)
        Category.Image.WATER -> Icon(imageVector = Icons.Default.WaterDrop, contentDescription = null)
        Category.Image.FIRE -> Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = null)
        Category.Image.STAR -> Icon(imageVector = Icons.Default.Grade, contentDescription = null)
        Category.Image.SAVINGS -> Icon(imageVector = Icons.Default.Savings, contentDescription = null)
        Category.Image.CAR -> Icon(imageVector = Icons.Default.CarRepair, contentDescription = null)
        Category.Image.BIKE -> Icon(imageVector = Icons.Default.PedalBike, contentDescription = null)
        Category.Image.TRAIN -> Icon(imageVector = Icons.Default.DirectionsTransit, contentDescription = null)
        Category.Image.MOPED -> Icon(imageVector = Icons.Default.Moped, contentDescription = null)
        Category.Image.MOTORCYCLE -> Icon(imageVector = Icons.Default.TwoWheeler, contentDescription = null)
        Category.Image.ELECTRONICS -> Icon(imageVector = Icons.Default.ElectricalServices, contentDescription = null)
        Category.Image.BOOK -> Icon(imageVector = Icons.Default.Book, contentDescription = null)
        Category.Image.FLIGHT -> Icon(imageVector = Icons.Default.FlightTakeoff, contentDescription = null)
        Category.Image.WORK -> Icon(imageVector = Icons.Default.Work, contentDescription = null)
        Category.Image.MOON -> Icon(imageVector = Icons.Default.NightlightRound, contentDescription = null)
        Category.Image.LOCK -> Icon(imageVector = Icons.Default.Https, contentDescription = null)
        Category.Image.PHONE -> Icon(imageVector = Icons.Default.PermPhoneMsg, contentDescription = null)
        Category.Image.STORE -> Icon(imageVector = Icons.Default.StoreMallDirectory, contentDescription = null)
        Category.Image.BAR -> Icon(imageVector = Icons.Default.Nightlife, contentDescription = null)
        Category.Image.FOREST -> Icon(imageVector = Icons.Default.Redeem, contentDescription = null)
        Category.Image.HARDWARE -> Icon(imageVector = Icons.Default.Hardware, contentDescription = null)
        Category.Image.PEST -> Icon(imageVector = Icons.Default.PestControl, contentDescription = null)
        else -> {
            Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
        }
    }
}
