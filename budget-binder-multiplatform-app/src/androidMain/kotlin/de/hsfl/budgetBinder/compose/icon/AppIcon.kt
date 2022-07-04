package de.hsfl.budgetBinder.compose.icon

import androidx.compose.foundation.Image
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import de.hsfl.budgetBinder.android.R

@Composable
actual fun AppIcon(modifier: Modifier) {
    Image(
        modifier = modifier,
        imageVector = ImageVector.vectorResource(id = R.drawable.ic_budgetbinder),
        contentDescription = null
    )
}

@Composable
actual fun AvatarImage(modifier: Modifier) {
    Image(
        modifier = modifier,
        imageVector = ImageVector.vectorResource(id = R.drawable.ic_avataricon),
        contentDescription = null
    )
}

@Composable
actual fun WelcomeImage(modifier: Modifier) {
    Image(
        modifier = modifier,
        imageVector = ImageVector.vectorResource(id = R.drawable.ic_undraw_welcome),
        contentDescription = null
    )
}

@Composable
actual fun SavingsImage(modifier: Modifier) {
    Image(
        modifier = modifier,
        imageVector = ImageVector.vectorResource(id = R.drawable.ic_undraw_savings),
        contentDescription = null
    )
}

@Composable
actual fun GetStartedImage(modifier: Modifier) {
    Image(
        modifier = modifier,
        imageVector = ImageVector.vectorResource(id = R.drawable.ic_undraw_awesome),
        contentDescription = null
    )
}


@Composable
actual fun DashboardIcon() {
    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_dashboard_24), contentDescription = null)
}

@Composable
actual fun CategoryIcon() {
    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_category_24), contentDescription = null)
}

@Composable
actual fun SettingsIcon() {
    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_settings_24), contentDescription = null)
}

@Composable
actual fun LogoutIcon() {
    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_logout_fill1_wght400_grad0_opsz24__1_), contentDescription = null)
}

@Composable
actual fun DarkModeIcon() {
    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_dark_mode_fill1_wght400_grad0_opsz24), contentDescription = null)
}

@Composable
actual fun ServerIcon() {
    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_dns_24), contentDescription = null)
}

@Composable
actual fun AccountIcon() {
    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_account_circle_24), contentDescription = null)
}

@Composable
actual fun DeleteForeverIcon() {
    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_delete_forever_24), contentDescription = null)
}

@Composable
actual fun SaveIcon() {
    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_save_24), contentDescription = null)
}

@Composable
actual fun ReplyIcon() {
    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_reply_fill1_wght400_grad0_opsz24__1_), contentDescription = null)
}

@Composable
actual fun ForwardIcon() {
    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_forward_fill1_wght400_grad0_opsz24), contentDescription = null)
}

@Composable
actual fun EuroIcon() {
    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_euro_24), contentDescription = null)
}

@Composable
actual fun ResetIcon() {
    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_device_reset_fill1_wght400_grad0_opsz24), contentDescription = null)
}
