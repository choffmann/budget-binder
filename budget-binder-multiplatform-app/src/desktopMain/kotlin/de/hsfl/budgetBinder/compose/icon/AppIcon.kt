package de.hsfl.budgetBinder.compose.icon

import androidx.compose.foundation.Image
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@Composable
actual fun AppIcon(modifier: Modifier) {
    Image(modifier = modifier, painter = painterResource("budgetbinder.svg"), contentDescription = null)
}

@Composable
actual fun AvatarImage(modifier: Modifier) {
    Image(modifier = modifier, painter = painterResource("undraw_profile_pic_ic-5-t.svg"), contentDescription = null)
}

@Composable
actual fun DashboardIcon() {
    Icon(imageVector = Icons.Default.Dashboard, contentDescription = null)
}

@Composable
actual fun CategoryIcon() {
    Icon(imageVector = Icons.Default.Category, contentDescription = null)
}

@Composable
actual fun SettingsIcon() {
    Icon(imageVector = Icons.Default.Settings, contentDescription = null)
}

@Composable
actual fun LogoutIcon() {
    Icon(imageVector = Icons.Default.Logout, contentDescription = null)
}

@Composable
actual fun DarkModeIcon() {
    Icon(imageVector = Icons.Default.DarkMode, contentDescription = null)
}

@Composable
actual fun ServerIcon() {
    Icon(imageVector = Icons.Default.Dns, contentDescription = null)
}

@Composable
actual fun AccountIcon() {
    Icon(imageVector = Icons.Default.ManageAccounts, contentDescription = null)
}

@Composable
actual fun DeleteForeverIcon() {
    Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null)
}

@Composable
actual fun SaveIcon() {
    Icon(imageVector = Icons.Default.Save, contentDescription = null)
}

@Composable
actual fun ReplyIcon() {
    Icon(imageVector = Icons.Default.Reply, contentDescription = null)
}

@Composable
actual fun ForwardIcon() {
    Icon(imageVector = Icons.Default.Forward, contentDescription = null)
}

@Composable
actual fun EuroIcon() {
    Icon(imageVector = Icons.Default.Euro, contentDescription = null)
}
