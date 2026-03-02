package com.example.periodtracker.ui

import android.accessibilityservice.GestureDescription
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import com.example.periodtracker.data.UserData
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.example.periodtracker.ui.theme.bodyLargeBold

import java.time.LocalDate
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters

import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.atan2


@Composable
fun HomeScreen() {

    val context = LocalContext.current
    val username = UserData.getUsername(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (username.isBlank()) "Hello, User." else "Hello, $username.",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )

        WeeklyCalendarHeader()
    }


}

@Composable
fun WeeklyCalendarHeader() {
    val today = LocalDate.now()
    val sunday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
    val weekDates = (0..6).map {  sunday.plusDays(it.toLong())}

    val weekDays = listOf("sun", "mon", "tue", "wed", "thu", "fri", "sat")

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            weekDays.forEachIndexed {
                index, day ->

                Text (
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = if (weekDates[index]==today) bodyLargeBold
                            else MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            weekDates.forEach {
                date ->
                val isToday = date == today

                Text (
                    text = date.dayOfMonth.toString(),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = if (isToday) bodyLargeBold
                    else MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            }
        }
    } }
}

@Composable
fun PhaseChart(
    modifier: Modifier = Modifier,
    radius:Float = 500f,
    transparentWidth:Float = 70f,
    input:List<PhaseInput>,
    centerText:String = "days until menstruation"
) {

}

data class PhaseInput(
    val color: Color,
    val value:Int,
    val description:String,
    val isTapped:Boolean = false
)