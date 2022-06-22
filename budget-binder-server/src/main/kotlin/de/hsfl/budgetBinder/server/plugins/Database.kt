package de.hsfl.budgetBinder.server.plugins

import de.hsfl.budgetBinder.server.config.Config
import de.hsfl.budgetBinder.server.models.Categories
import de.hsfl.budgetBinder.server.models.Entries
import de.hsfl.budgetBinder.server.models.Users
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import java.sql.DriverManager

fun Application.configureDatabase() {

    val config: Config by closestDI().instance()

    val url: String = when (config.database.dbType) {
        Config.DBType.SQLITE -> {
            val url = "jdbc:sqlite:${config.database.sqlitePath}"
            /*
            * The url is used in the tests to not create or alter the normal database.
            * the connection must be held because exposed closes the connection to the db
            * after every transaction and if no connection is alive the memory database will be deleted
            * */
            if (url == "jdbc:sqlite:file:test?mode=memory&cache=shared") {
                DriverManager.getConnection(url)
            }
            url
        }
        Config.DBType.MYSQL -> "jdbc:mysql://${config.database.serverAddress}:${config.database.serverPort}/${config.database.name}"
        Config.DBType.POSTGRES -> "jdbc:postgresql://${config.database.serverAddress}:${config.database.serverPort}/${config.database.name}"
    }

    Database.connect(url, user = config.database.user, password = config.database.password)

    transaction {
        SchemaUtils.create(Users, Categories, Entries)
    }
}
