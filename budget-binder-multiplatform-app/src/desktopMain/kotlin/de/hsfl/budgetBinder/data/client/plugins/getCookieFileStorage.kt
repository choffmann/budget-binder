package de.hsfl.budgetBinder.data.client.plugins

import java.io.File
import kotlin.io.path.createDirectories

actual fun getCookieFileStorage(): File {
    var path = System.getProperty("user.home")
    if (System.getProperty("os.name").lowercase().indexOf("mac") >= 0) {
        path += "/Library/Application Support"
    }
    path += "/.bb-client"
    File(path).toPath().createDirectories()

    path += "/cookies.txt"
    val file = File(path)
    file.createNewFile()
    return file
}
