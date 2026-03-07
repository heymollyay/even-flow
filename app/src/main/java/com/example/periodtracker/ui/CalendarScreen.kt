package com.example.periodtracker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.periodtracker.R
import com.example.periodtracker.data.UserData
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale


// Phase colors
val MenstruationColor = Color(0xFFE05555)
val FollicularColor = Color(0xFFE07830)
val OvulationColor = Color(0xFF9B6EC8)
val LutealColor = Color(0xFFE8609A)
val CurrentWeekColor = Color(0xFFD3EDD0).copy(alpha = 0.3f)

enum class CyclePhase { MENSTRUATION, FOLLICULAR, OVULATION, LUTEAL, UNKNOWN }

fun getPhaseForDate(date: LocalDate, context: android.content.Context): CyclePhase {
    val lastPeriodMs = UserData.getLastPeriodStart(context) ?: return CyclePhase.UNKNOWN
    if (lastPeriodMs == -1L) return CyclePhase.UNKNOWN

    val lastPeriodDate = java.time.Instant.ofEpochMilli(lastPeriodMs)
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalDate()

    val cycleLength = UserData.getCycleLength(context)
    val menstrualLength = UserData.getMenstrualLength(context)
    val follicularLength = UserData.getFollicularLength(context)
    val ovulationLength = 2

    val daysSince = java.time.temporal.ChronoUnit.DAYS.between(lastPeriodDate, date).toInt()
    val positionInCycle = ((daysSince % cycleLength) + cycleLength) % cycleLength

    return when {
        positionInCycle < menstrualLength -> CyclePhase.MENSTRUATION
        positionInCycle < menstrualLength + follicularLength -> CyclePhase.FOLLICULAR
        positionInCycle < menstrualLength + follicularLength + ovulationLength -> CyclePhase.OVULATION
        else -> CyclePhase.LUTEAL
    }
}

fun phaseColor(phase: CyclePhase): Color = when (phase) {
    CyclePhase.MENSTRUATION -> MenstruationColor
    CyclePhase.FOLLICULAR -> FollicularColor
    CyclePhase.OVULATION -> OvulationColor
    CyclePhase.LUTEAL -> LutealColor
    CyclePhase.UNKNOWN -> Color.Gray
}

fun getCurrentPhaseName(context: android.content.Context): String {
    val phase = getPhaseForDate(LocalDate.now(), context)
    return when (phase) {
        CyclePhase.MENSTRUATION -> "menstrual"
        CyclePhase.FOLLICULAR -> "follicular"
        CyclePhase.OVULATION -> "ovulation"
        CyclePhase.LUTEAL -> "luteal"
        CyclePhase.UNKNOWN -> "unknown"
    }
}

@Composable
fun CalendarScreen() {
    val context = LocalContext.current
    val today = LocalDate.now()
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        //greeting
        Text(
            text = "Calendar",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )

        // Phase indicator card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "You are in the ${getCurrentPhaseName(context)} phase.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Image(
                    painter = painterResource(id = R.drawable.basiclogo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(24.dp)
                )
            }
        }

        // Month navigation
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                        Text(
                            text = "←",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                        Text(
                            text = "→",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Day headers
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Calendar grid
                CalendarGrid(
                    yearMonth = currentMonth,
                    today = today,
                    context = context
                )
            }
        }

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(
                "Luteal" to LutealColor,
                "Period" to MenstruationColor,
                "Follicular" to FollicularColor,
                "Ovulation" to OvulationColor
            ).forEach { (label, color) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    today: LocalDate,
    context: android.content.Context
) {
    val firstDayOfMonth = yearMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Sunday = 0
    val daysInMonth = yearMonth.lengthOfMonth()

    val todayWeekStart = today.minusDays(today.dayOfWeek.value % 7L)
    val todayWeekEnd = todayWeekStart.plusDays(6)

    val totalCells = firstDayOfWeek + daysInMonth
    val totalRows = (totalCells + 6) / 7

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        for (row in 0 until totalRows) {
            val weekStart = firstDayOfMonth.minusDays(firstDayOfWeek.toLong()).plusDays(row * 7L)
            val weekEnd = weekStart.plusDays(6)
            val isCurrentWeek = today >= weekStart && today <= weekEnd

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isCurrentWeek) CurrentWeekColor
                        else Color.Transparent
                    )
            ) {
                for (col in 0 until 7) {
                    val dayIndex = row * 7 + col - firstDayOfWeek + 1
                    if (dayIndex < 1 || dayIndex > daysInMonth) {
                        Box(modifier = Modifier.weight(1f).height(44.dp))
                    } else {
                        val date = yearMonth.atDay(dayIndex)
                        val phase = getPhaseForDate(date, context)
                        val color = phaseColor(phase)
                        val isPast = date.isBefore(today)
                        val isToday = date == today

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                isPast -> {
                                    // X for past days
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(color.copy(alpha = 0.3f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "×",
                                            color = color,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                isToday -> {
                                    // Circle for today
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, color, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(18.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                        )
                                    }
                                }
                                else -> {
                                    // Filled dot for future days
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}