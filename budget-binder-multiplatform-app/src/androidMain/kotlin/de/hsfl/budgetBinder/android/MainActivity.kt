package de.hsfl.budgetBinder.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import de.hsfl.budgetBinder.compose.App
import de.hsfl.budgetBinder.prototyp.Prototype

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            //App()
            Prototype()
        }
    }
}