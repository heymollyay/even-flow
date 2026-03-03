package com.example.periodtracker.ui.Onboarding


import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.periodtracker.data.UserData

@Composable
fun OnboardingScreen(onFinish: () -> Unit, modifier: Modifier = Modifier)
{
    var currentQuestion by rememberSaveable { mutableIntStateOf(0) }

    val context = LocalContext.current

    var username by remember { mutableStateOf(UserData.getUsername(context)) }

    var lastPeriod by remember { mutableStateOf<Long?>(UserData.getLastPeriodStart(context))}

    var periodLength by remember { mutableIntStateOf(UserData.getMenstrualLength(context)) }

    var cycleLength by remember { mutableIntStateOf(UserData.getCycleLength(context))}

    var contraceptives by remember {mutableStateOf(UserData.getLongTermContraceptives(context))}

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
            answer = periodLength,
            onAnswerChange = {periodLength = it},
            onNext = {
                UserData.saveMenstrualLength(context,periodLength)
                currentQuestion++ }
        )
        4 -> OnboardingCycleLengthQuestion(
            answer = cycleLength,
            onAnswerChange = {cycleLength = it},
            onNext = {
                UserData.saveCycleLength(context,cycleLength)
                currentQuestion++ }
        )
        5 -> OnboardingContraceptivesQuestion(
            onComplete = { onFinish() }
        )
    }
}




