package de.hsfl.budgetBinder.prototype.screens.welcome

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.prototype.screens.CategoriesListView

@Composable
fun ChooseCategories() {
    Column(modifier = Modifier.fillMaxSize()) {
        TextHeader(modifier = Modifier.fillMaxWidth().padding(16.dp))
        CategoriesListView(modifier = Modifier.fillMaxWidth().padding(16.dp).weight(1F))
        Button(modifier = Modifier.fillMaxWidth().padding(16.dp), onClick = {}) {
            Text(text = "Weiter")
        }
    }
}

@Composable
fun TextHeader(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = "Kategorie",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold
        )
        Text(text = "Wähle die Kategorie, für die du ein Budget erstellen möchtest", textAlign = TextAlign.Center)
    }
}