package de.hsfl.budgetBinder.data.client.plugins

import de.hsfl.budgetBinder.android.BudgetBinderApplication
import java.io.File

actual fun getCookieFileStorage(): File {
    return File(BudgetBinderApplication.instance.filesDir, "cookies.txt")
}
