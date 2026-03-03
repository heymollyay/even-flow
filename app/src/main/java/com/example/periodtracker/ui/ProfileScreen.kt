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


@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    var username by remember { mutableStateOf(UserData.getUsername(context)) }
    var editMode by remember { mutableStateOf(false) }
    var tempUsername by remember { mutableStateOf("") }
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
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // save button - immediately overwrites old username with new one
                        Button(onClick = {
                            UserData.saveUsername(context, tempUsername)
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
                                        // ERROR_NO_DEVICE_CREDENTIAL = 14: truly no screen lock set up
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
                                ).show()// other codes (user cancelled, lockout, etc.) — do nothing
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