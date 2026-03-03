package com.example.periodtracker.ui.Onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun OnboardingContraceptivesQuestion(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,


) {

    var showOptions by remember {mutableStateOf(false)}

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment =  Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer)

        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Do you use long-term contraceptives that could affect your cycle?",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )



            }
        }


        Button(
            onClick = {showOptions = true}
        ) {
            Text("Yes")
        }
        Button(
            onClick = {showOptions = false}
        ) {
            Text("No")
        }

        if (showOptions) {
            Text("Which one?")
            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = {

                }
            ) {

            }

        }

        Button (
            modifier = Modifier.padding(vertical = 24.dp),
            onClick = onComplete
        ) {
            Text("Complete")
        }

    }
}