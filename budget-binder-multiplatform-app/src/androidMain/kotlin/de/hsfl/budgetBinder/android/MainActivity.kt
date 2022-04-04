package de.hsfl.budgetBinder.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import de.hsfl.budgetBinder.compose.ApplicationView

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ApplicationView()
        }
    }
}

fun test() {

}