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


@Composable
fun OnboardingNameQuestion(
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    answer: String,
    onAnswerChange: (String) -> Unit

) {

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment =  Alignment.CenterHorizontally
    ) {
        Text ("Username")
        OutlinedTextField(
            value = answer,
            onValueChange =  onAnswerChange,
            label = { Text("Username") },
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