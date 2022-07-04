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
    //Icon(imageVector = Icons.Default.Dashboard, contentDescription = null)
    Icon(painter = painterResource("dashboard_FILL1_wght400_GRAD0_opsz24.svg"), contentDescription = null)
}

@Composable
actual fun CategoryIcon() {
    Icon(painter = painterResource("category_FILL1_wght400_GRAD0_opsz24.svg"), contentDescription = null)
}

@Composable
actual fun SettingsIcon() {
    Icon(painter = painterResource("settings_FILL1_wght400_GRAD0_opsz24.svg"), contentDescription = null)
}

@Composable
actual fun LogoutIcon() {
    Icon(painter = painterResource("logout_FILL1_wght400_GRAD0_opsz24.svg"), contentDescription = null)
}

@Composable
actual fun DarkModeIcon() {
    Icon(painter = painterResource("dark_mode_FILL1_wght400_GRAD0_opsz24.svg"), contentDescription = null)
}

@Composable
actual fun ServerIcon() {
    Icon(painter = painterResource("dns_FILL1_wght400_GRAD0_opsz24.svg"), contentDescription = null)
}

@Composable
actual fun AccountIcon() {
    Icon(painter = painterResource("account_circle_FILL1_wght400_GRAD0_opsz24.svg"), contentDescription = null)
}

@Composable
actual fun DeleteForeverIcon() {
    Icon(painter = painterResource("delete_FILL1_wght400_GRAD0_opsz24.svg"), contentDescription = null)
}

@Composable
actual fun SaveIcon() {
    Icon(painter = painterResource("save_FILL1_wght400_GRAD0_opsz24.svg"), contentDescription = null)
}

@Composable
actual fun ReplyIcon() {
    Icon(painter = painterResource("reply_FILL1_wght400_GRAD0_opsz24.svg"), contentDescription = null)
}

@Composable
actual fun ForwardIcon() {
    Icon(painter = painterResource("forward_FILL1_wght400_GRAD0_opsz24.svg"), contentDescription = null)
}

@Composable
actual fun EuroIcon() {
    Icon(painter = painterResource("euro_FILL1_wght400_GRAD0_opsz24.svg"), contentDescription = null)
}

@Composable
actual fun WelcomeImage(modifier: Modifier) {
    Image(modifier = modifier, painter = painterResource("undraw_welcome_re_h3d9.svg"), contentDescription = null)
}

@Composable
actual fun SavingsImage(modifier: Modifier) {
    Image(modifier = modifier, painter = painterResource("undraw_savings_re_eq4w.svg"), contentDescription = null)
}

@Composable
actual fun GetStartedImage(modifier: Modifier) {
    Image(modifier = modifier, painter = painterResource("undraw_awesome_rlvy.svg"), contentDescription = null)
}
@Composable
actual fun ResetIcon() {
    Icon(painter = painterResource("device_reset_FILL1_wght400_GRAD0_opsz24.svg"), contentDescription = null)
}
