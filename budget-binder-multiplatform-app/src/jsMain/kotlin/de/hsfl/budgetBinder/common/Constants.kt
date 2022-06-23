package de.hsfl.budgetBinder.common

actual object Constants {
    actual val BASE_URL: String = ""
    //Set to nothing because it hosts itself (so we don't have to deal with CORS)
    //for local server: http://localhost:8080
    //for online server: https://bb-server.fpcloud.de
}
