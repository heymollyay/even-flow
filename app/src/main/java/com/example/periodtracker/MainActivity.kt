package com.example.periodtracker

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.example.periodtracker.data.calculatePhaseLengths
import com.example.periodtracker.ui.theme.PeriodTrackerTheme
import com.example.periodtracker.ui.HomeScreen
import com.example.periodtracker.ui.Onboarding.OnboardingScreen
import com.example.periodtracker.ui.ProfileScreen
import com.example.periodtracker.ui.JournalScreen
import androidx.fragment.app.FragmentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button


class MainActivity : FragmentActivity() { //updated for biometrics activity tracking DO NOT CHANGE: FRAGMENT IS NEEDED
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PeriodTrackerTheme {
                PeriodTrackerApp(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun PeriodTrackerApp(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    var shouldShowOnboarding by rememberSaveable { mutableStateOf(true)}
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    if (shouldShowOnboarding) {
        OnboardingScreen(
            onFinish = {
                calculatePhaseLengths(context)
                shouldShowOnboarding = false}
        )
        return
    }

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
@Composable
fun OnboardingScreen(onContinueClicked: () -> Unit, modifier: Modifier = Modifier)
{
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

@Composable fun HomeScreen() { Text("Home") }
@Composable fun CalendarScreen() { Text("Calendar")}
@Composable fun JournalScreen() { Text("Journal") }
@Composable fun ProfileScreen() { Text("Profile")}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    CALENDAR("Calendar", Icons.Default.DateRange),
    JOURNAL("Journal", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.AccountBox),
}
