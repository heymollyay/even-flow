package com.example.periodtracker.onboarding
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.periodtracker.R
import com.example.periodtracker.data.UserData
import com.example.periodtracker.ui.Welcome
import com.example.periodtracker.ui.theme.DeepPurple
import com.example.periodtracker.ui.theme.LightLavender
import com.example.periodtracker.ui.theme.MediumPurple
import com.example.periodtracker.ui.theme.TextDark
import com.example.periodtracker.ui.theme.TextMedium
import com.example.periodtracker.ui.theme.White

@Composable
fun OnboardingQuiz(onFinish: () -> Unit, modifier: Modifier = Modifier) {

    var currentQuestion by rememberSaveable { mutableIntStateOf(0) }
    val context = LocalContext.current

    var username     by remember { mutableStateOf(UserData.getUsername(context)) }
    var lastPeriod   by remember { mutableStateOf<Long?>(UserData.getLastPeriodEnd(context)) }
    var periodLength by remember { mutableIntStateOf(UserData.getMenstrualLength(context)) }
    var cycleLength  by remember { mutableIntStateOf(UserData.getCycleLength(context)) }

    when (currentQuestion) {
        0 -> Welcome(
            onContinueClicked = { currentQuestion++ }
        )
        1 -> OnboardingUsernameQuestion(
            answer = username,
            onAnswerChange = { username = it },
            onBack = { currentQuestion-- },
            onNext = {
                UserData.saveUsername(context, username)
                currentQuestion++
            }
        )
        2 -> OnboardingLastPeriodQuestion(
            answer = lastPeriod,
            onAnswerChange = { lastPeriod = it },
            onBack = { currentQuestion-- },
            onNext = {
                UserData.saveLastPeriodEnd(context, lastPeriod)
                currentQuestion++
            }
        )
        3 -> OnboardingPeriodDurationQuestion(
            answer = periodLength,
            onAnswerChange = { periodLength = it },
            onBack = { currentQuestion-- },
            onNext = {
                UserData.saveMenstrualLength(context, periodLength)
                currentQuestion++
            }
        )
        4 -> OnboardingCycleLengthQuestion(
            answer = cycleLength,
            onAnswerChange = { cycleLength = it },
            onBack = { currentQuestion-- },
            onNext = {
                UserData.saveCycleLength(context, cycleLength)
                currentQuestion++
            }
        )
        5 -> OnboardingContraceptivesQuestion(
            onBack = { currentQuestion-- },
            onComplete = { onFinish() }
        )
    }
}

@Composable
private fun FlowerHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Even Flow.",
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = White,
            fontFamily = FontFamily.Serif
        )
    }
}

@Composable
private fun NavButton(icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(MediumPurple)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun QuestionScaffold(
    onBack: () -> Unit,
    onNext: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepPurple),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp)
        ) {
            FlowerHeader()
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(LightLavender)
                    .padding(24.dp)
            ) {
                content()
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NavButton(icon = Icons.AutoMirrored.Filled.ArrowBack, onClick = onBack)
                    NavButton(icon = Icons.AutoMirrored.Filled.ArrowForward, onClick = onNext)
                }
            }
        }
    }
}

@Composable
private fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        textStyle = TextStyle(fontSize = 18.sp, color = TextDark),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(White)
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                if (value.isEmpty()) {
                    Text(placeholder, color = TextMedium, fontSize = 18.sp)
                }
                innerTextField()
            }
        }
    )
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
fun OnboardingUsernameQuestion(
    answer: String,
    onAnswerChange: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    QuestionScaffold(onBack = onBack, onNext = onNext) {
        Text(
            text = "What should we call you?",
            fontSize = 20.sp,
            color = TextDark,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        InputField(
            value = answer,
            onValueChange = onAnswerChange,
            placeholder = "Username"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingLastPeriodQuestion(
    answer: Long?,
    onAnswerChange: (Long?) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val displayDate = remember(answer) {
        answer?.let {
            val sdf = java.text.SimpleDateFormat("dd / MM / yyyy", java.util.Locale.getDefault())
            sdf.format(java.util.Date(it))
        } ?: ""
    }

    var showPicker by remember { mutableStateOf(false) }

    if (showPicker) {
        DatePickerModal(
            onDateSelected = { millis ->
                onAnswerChange(millis)
                showPicker = false
            },
            onDismiss = { showPicker = false }
        )
    }

    QuestionScaffold(onBack = onBack, onNext = onNext) {
        Text(
            text = "When did your last period end?",
            fontSize = 20.sp,
            color = TextDark,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(White)
                .clickable { showPicker = true }
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayDate.ifEmpty { "DD / MM / YYYY" },
                    color = if (displayDate.isEmpty()) TextMedium else TextDark,
                    fontSize = 18.sp
                )
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Pick date",
                    tint = MediumPurple,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun OnboardingPeriodDurationQuestion(
    answer: Int,
    onAnswerChange: (Int) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    QuestionScaffold(onBack = onBack, onNext = onNext) {
        Text(
            text = "How many days does your period usually last?",
            fontSize = 20.sp,
            color = TextDark,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        InputField(
            value = if (answer == 0) "" else answer.toString(),
            onValueChange = { if (it.length <= 2) onAnswerChange(it.toIntOrNull() ?: 0) },
            placeholder = "Enter number of days...",
            keyboardType = KeyboardType.Number
        )
    }
}

@Composable
fun OnboardingCycleLengthQuestion(
    answer: Int,
    onAnswerChange: (Int) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    QuestionScaffold(onBack = onBack, onNext = onNext) {
        Text(
            text = "How long is your average cycle?",
            fontSize = 20.sp,
            color = TextDark,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        InputField(
            value = if (answer == 0) "" else answer.toString(),
            onValueChange = { if (it.length <= 2) onAnswerChange(it.toIntOrNull() ?: 0) },
            placeholder = "Enter number of days...",
            keyboardType = KeyboardType.Number
        )
    }
}


@Composable
fun OnboardingContraceptivesQuestion(
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    var usesContraceptives by remember { mutableStateOf<Boolean?>(null) }
    var selected by remember { mutableStateOf(setOf<String>()) }

    val contraceptiveOptions = listOf(
        "Pill",
        "IUD",
        "Implant",
        "Patch",
        "Injection",
        "Ring"

    )

    QuestionScaffold(
        onBack = onBack,
        onNext = { onComplete() }
    ) {
        Text(
            text = "Do you use any hormonal long-term contraceptives?",
            fontSize = 20.sp,
            color = TextDark,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf(true to "Yes", false to "No").forEach { (isYes, label) ->
                val isSelected = usesContraceptives == isYes
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) MediumPurple else White)
                        .clickable { usesContraceptives = isYes }
                        .padding(vertical = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) White else TextDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = usesContraceptives == true,
            enter = fadeIn() + expandVertically(),
            exit  = fadeOut() + shrinkVertically()
        ) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text(
                    text = "Select all that apply:",
                    color = TextMedium,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                contraceptiveOptions.forEach { option ->
                    val checked = selected.contains(option)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (checked) MediumPurple else White)
                            .clickable {
                                selected = if (checked) selected - option else selected + option
                            }
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option,
                                color = if (checked) White else TextDark,
                                fontSize = 16.sp
                            )
                            if (checked) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}