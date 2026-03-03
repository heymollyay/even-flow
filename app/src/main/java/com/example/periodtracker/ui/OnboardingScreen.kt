package com.example.periodtracker.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.periodtracker.data.UserData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun OnboardingScreen(onFinish: () -> Unit, modifier: Modifier = Modifier)
{
    var currentQuestion by rememberSaveable { mutableStateOf(0) }

    val context = LocalContext.current

    var username by remember { mutableStateOf(UserData.getUsername(context)) }

    var lastPeriod by remember { mutableStateOf<Long?>(UserData.getLastPeriodStart(context))}

    when(currentQuestion) {
        0 -> OnboardingWelcome(
            onContinueClicked = { currentQuestion++ }
        )
        1 -> OnboardingNameQuestion(
            answer = username,
            onAnswerChange = {username = it},
            onNext = {
                UserData.saveUsername(context,username)
                currentQuestion++ }
        )
        2 -> OnboardingLastPeriodQuestion(
            answer = lastPeriod,
            onAnswerChange = {lastPeriod = it},
            onNext = {
                UserData.saveLastPeriodStart(context,lastPeriod)
                currentQuestion++}

        )
        3 -> OnboardingPeriodDurationQuestion(
            onNext = { currentQuestion++ }
        )
        4 -> OnboardingCycleLengthQuestion(
            onNext = { currentQuestion++ }
        )
        5 -> OnboardingContraceptivesQuestion(
            onComplete = { onFinish()}
        )
    }
}

@Composable
fun OnboardingWelcome(onContinueClicked: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Tracking your period just got easier.")
        Button (
            modifier = Modifier.padding(vertical = 24.dp),
            onClick = onContinueClicked
        ) {
            Text("Continue")
        }
    }
}

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

@Composable
fun OnboardingLastPeriodQuestion(
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    answer: Long?,
    onAnswerChange: (Long?) -> Unit
) {
    var showModal by remember { mutableStateOf(false)}

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment =  Alignment.CenterHorizontally
    ) {
        Text("What date did your last period start?")
        Button(onClick = {showModal = true}) {
            Text(if (answer != null) convertMillisToDate(answer) else "Select Date")
        }

        Button(
            modifier = Modifier.padding(vertical = 24.dp),
            onClick = onNext,
            enabled = answer != null
        ) {
            Text("Next")
        }
    }

    if (showModal) {
        DatePickerModal(
            onDateSelected = {onAnswerChange(it)},
            onDismiss = {showModal = false}
        )
    }

}
fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(Date(millis))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun OnboardingPeriodDurationQuestion(onNext: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment =  Alignment.CenterHorizontally
    ) {
        Text ("How many days does your period typically last?")
        Button (
            modifier = Modifier.padding(vertical = 24.dp),
            onClick = onNext
        ) {
            Text("Next")
        }
    }
}

@Composable
fun OnboardingCycleLengthQuestion(onNext: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment =  Alignment.CenterHorizontally
    ) {
        Text ("How many days is your average menstrual cycle?")
        Button (
            modifier = Modifier.padding(vertical = 24.dp),
            onClick = onNext
        ) {
            Text("Next")
        }
    }
}

@Composable
fun OnboardingContraceptivesQuestion(onComplete: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment =  Alignment.CenterHorizontally
    ) {
        Text ("Do you use long-term contraceptives that could affect your cycle?")
        Button (
            modifier = Modifier.padding(vertical = 24.dp),
            onClick = onComplete
        ) {
            Text("Complete")
        }
    }
}