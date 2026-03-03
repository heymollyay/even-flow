package com.example.periodtracker.ui.Onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly


@Composable
fun OnboardingPeriodDurationQuestion(
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    answer: Int,
    onAnswerChange: (Int) -> Unit

) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment =  Alignment.CenterHorizontally
    ) {
        Text ("How long does your period typically last?")
        OutlinedTextField(
            value = if (answer == 0) "" else answer.toString(),
            onValueChange = { input ->
                if (input.isDigitsOnly()) {
                    onAnswerChange(input.toIntOrNull() ?:0)
                }
            },
            placeholder = {Text("Number of days")},
            label = { Text("Period length") },
            singleLine = true,
            modifier = Modifier
        )
        Button (
            modifier = Modifier.padding(vertical = 24.dp),
            onClick = onNext
        ) {
            Text("Next")
        }
    }
}