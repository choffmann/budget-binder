package de.hsfl.budgetBinder.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.res.painterResource
import de.hsfl.budgetBinder.compose.ApplicationView

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ApplicationView(painterResource(id = R.drawable.hello_world))
        }
    }
}