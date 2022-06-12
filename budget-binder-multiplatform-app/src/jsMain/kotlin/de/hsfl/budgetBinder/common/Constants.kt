package de.hsfl.budgetBinder.common

actual object Constants {
    actual val BASE_URL: String = "http://localhost:8080"
    //for local server: http://localhost:8080
    //for online server: https://bb-server.fpcloud.de
    val DEFAULTCATEGORY = (Category(0, "", "FFFFFF", Category.Image.DEFAULT, 0f))
}