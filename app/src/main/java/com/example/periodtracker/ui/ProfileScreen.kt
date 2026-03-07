package com.example.periodtracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.periodtracker.data.UserData
import androidx.fragment.app.FragmentActivity //UPDATED TO USE THIS FOR BIOMETRICS CAPABILITY
import com.example.periodtracker.Biometrics
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.DateRange
import com.example.periodtracker.calculateCurrentDay
import com.example.periodtracker.calculateCurrentPhase
import com.example.periodtracker.calculateDaysTillNextPeriod
import com.example.periodtracker.calculatePhaseLengths
import com.example.periodtracker.onboarding.DatePickerModal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    var username by remember { mutableStateOf(UserData.getUsername(context)) }
    var editMode by remember { mutableStateOf(false) }
    var tempUsername by remember { mutableStateOf("") }
    var cycleLength by remember { mutableStateOf(UserData.getCycleLength(context).toString()) }
    var periodLength by remember { mutableStateOf(UserData.getMenstrualLength(context).toString()) }
    var lastPeriod by remember { mutableStateOf<Long?>(UserData.getLastPeriodStart(context)) }
    var contraceptive by remember { mutableStateOf(UserData.getLongTermContraceptives(context)) }

    var contraceptiveExpanded by remember { mutableStateOf(false) }

    val contraceptiveOptions = listOf(
        "None",
        "Pill",
        "IUD",
        "Implant",
        "Patch",
        "Injection",
        "Ring"

    )

    //uses main's activity tracker (which MUST be AppCompatActivity: FragmentActivity for biometrics )
    val activity = remember(context) {
        var ctx: android.content.Context = context
        while (ctx is android.content.ContextWrapper) {
            if (ctx is FragmentActivity) return@remember ctx
            ctx = ctx.baseContext
        }
        null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // avatar person logo - NOT A PHOTO UPLOAD
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile avatar",
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = if (username.isBlank()) "Set your username" else username,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                //edit username capabilities
                if (editMode) {
                    OutlinedTextField(
                        value = tempUsername,
                        onValueChange = { tempUsername = it },
                        label = { Text("Username") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha=0.4f)
                    )

                    //edit period length
                    OutlinedTextField(
                        value = periodLength,
                        onValueChange = { value ->
                            if (value.all { it.isDigit() } && value.length <= 2) {
                                periodLength = value
                            }
                        },
                        label = { Text("Period length (days)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    //edit cycle length
                    OutlinedTextField(
                        value = cycleLength,
                        onValueChange = { value ->
                            if (value.all { it.isDigit() } && value.length <= 2) {
                                cycleLength = value
                            }
                        },
                        label = { Text("Cycle length (days)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    //date of last period

                    var showDatePicker by remember { mutableStateOf(false) }

                    val displayDate = remember(lastPeriod) {
                        lastPeriod?.let {
                            val sdf = java.text.SimpleDateFormat("dd / MM / yyyy", java.util.Locale.getDefault())
                            sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
                            sdf.format(java.util.Date(it))
                        } ?: ""
                    }

                    if (showDatePicker) {
                        DatePickerModal(
                            onDateSelected = { millis ->
                                lastPeriod = millis
                                showDatePicker = false
                            },
                            onDismiss = { showDatePicker = false }
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Start of last period (date)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable { showDatePicker = true }
                                .padding(horizontal = 20.dp, vertical = 18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = displayDate.ifEmpty { "DD / MM / YYYY" },
                                    color = if (displayDate.isEmpty())
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Pick date",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    //contraceptives
                    ExposedDropdownMenuBox(
                        expanded = contraceptiveExpanded,
                        onExpandedChange = { contraceptiveExpanded = !contraceptiveExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = contraceptive,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Hormonal contraceptive use") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = contraceptiveExpanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = contraceptiveExpanded,
                            onDismissRequest = { contraceptiveExpanded = false }
                        ) {
                            contraceptiveOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        contraceptive = option
                                        contraceptiveExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // save button - immediately overwrites old username with new one
                        Button(onClick = {
                            UserData.saveUsername(context, tempUsername)
                            UserData.saveCycleLength(context, cycleLength.toInt())
                            UserData.saveMenstrualLength(context, periodLength.toInt())
                            UserData.saveLastPeriodStart(context, lastPeriod)
                            UserData.saveLongTermContraceptives(context, contraceptive)

                            //Update logic very important
                            calculatePhaseLengths(context)
                            calculateDaysTillNextPeriod(context)
                            calculateCurrentPhase(context)
                            calculateCurrentDay(context)

                            username = tempUsername
                            editMode = false
                        }) {
                            Text("Save")
                        }
                        //cancel button
                        Button(onClick = { editMode = false }) {
                            Text("Cancel")
                        }
                    }
                } else {
                    //edit button on profile overview
                    OutlinedButton(
                        onClick = {
                            //biometrics check
                            if (activity != null) {
                                Biometrics.authenticate(
                                    activity = activity,
                                    title = "Even Flow",
                                    subtitle = "Verify your identity to edit profile",
                                    onSuccess = {
                                        tempUsername = username
                                        editMode = true
                                    },
                                    onFailure = { errorCode ->
                                        // ERROR_NO_DEVICE_CREDENTIAL = 14: no screen lock set up
                                        if (errorCode == BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL) {
                                            android.widget.Toast.makeText(
                                                context,
                                                "Please set up a PIN or biometrics in your device settings.",
                                                android.widget.Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                )
                            }
                            else {
                                android.widget.Toast.makeText(
                                    context,
                                    "Oops, something went wrong.",
                                    android.widget.Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Edit Profile", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        // privacy disclaimer
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
                    "Privacy",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "All data is stored locally and encrypted on your device. Nothing is shared or transmitted.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}