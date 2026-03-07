package com.example.periodtracker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.periodtracker.ui.theme.PeriodTrackerTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.example.periodtracker.ui.HomeScreen
import com.example.periodtracker.ui.JournalScreen
import com.example.periodtracker.ui.Login
import com.example.periodtracker.ui.ProfileScreen
import com.example.periodtracker.ui.CalendarScreen
import com.example.periodtracker.onboarding.OnboardingQuiz
import androidx.biometric.BiometricManager

import androidx.core.content.ContextCompat
import androidx.activity.result.ActivityResultLauncher

class MainActivity : FragmentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PeriodTrackerTheme {
                PeriodTrackerApp(
                    modifier = Modifier.fillMaxSize(),
                    requestPermissionLauncher = requestPermissionLauncher
                )
            }
        }

        CycleNotifications.createNotificationChannel(this)

        if (calculateIfStartOfPhase(this)) {
            CycleNotifications.buildNotification(this)
        }
    }
}

@Composable
fun PeriodTrackerApp(
    modifier: Modifier = Modifier,
    requestPermissionLauncher: ActivityResultLauncher<String>
) {

    val context = LocalContext.current

    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    val activity = remember(context) {
        var ctx: android.content.Context = context
        while (ctx is android.content.ContextWrapper) {
            if (ctx is FragmentActivity) return@remember ctx
            ctx = ctx.baseContext
        }
        null
    }

    val sharedPrefs = remember {
        context.getSharedPreferences("com.example.periodtracker", Context.MODE_PRIVATE)
    }

    var shouldShowOnboarding by rememberSaveable {
        val isFirstTime = sharedPrefs.getString("token", null) == null
        if (isFirstTime) {
            sharedPrefs.edit().putString("token", "true").apply()
        }
        mutableStateOf(isFirstTime)
    }

    var isAuthenticated by rememberSaveable { mutableStateOf(false) }

    when {
        // First ever launch → onboarding
        shouldShowOnboarding -> {
            OnboardingQuiz(
                onFinish = {
                    calculatePhaseLengths(context)
                    calculateDaysTillNextPeriod(context)
                    shouldShowOnboarding = false

                    when {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED -> { }
                        else -> {
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                    shouldShowOnboarding = false
                }
            )
        }
        !isAuthenticated -> {
            Login(
                onContinueClicked = {
                    val manager = BiometricManager.from(context)
                    val hasCredential = manager.canAuthenticate(
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    ) == BiometricManager.BIOMETRIC_SUCCESS

                    if (!hasCredential) {
                        android.widget.Toast.makeText(
                            context,
                            "This app requires password protection for your data. Please set up a PIN or biometric lock in your device settings.",
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                    } else if (activity != null) {
                        Biometrics.authenticate(
                            activity = activity,
                            title = "Even Flow",
                            subtitle = "Verify your identity to enter app",
                            onSuccess = { isAuthenticated = true },
                            onFailure = { errorCode ->
                                if (errorCode == BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL) {
                                    android.widget.Toast.makeText(
                                        context,
                                        "Please set up a PIN or biometrics in your device settings.",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        )
                    } else {
                        android.widget.Toast.makeText(
                            context,
                            "Oops, something went wrong.",
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }

        else -> {
            NavigationSuiteScaffold(
                navigationSuiteItems = {
                    AppDestinations.entries.forEach { destination ->
                        item(
                            icon = {
                                Icon(
                                    destination.icon,
                                    contentDescription = destination.label
                                )
                            },
                            label = { Text(destination.label) },
                            selected = destination == currentDestination,
                            onClick = { currentDestination = destination }
                        )
                    }
                } )
            {
                when(currentDestination) {
                    AppDestinations.HOME -> HomeScreen()
                    AppDestinations.CALENDAR -> CalendarScreen()
                    AppDestinations.JOURNAL -> JournalScreen()
                    AppDestinations.PROFILE -> ProfileScreen()
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    CALENDAR("Calendar", Icons.Default.DateRange),
    JOURNAL("Journal", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.AccountBox),
}
