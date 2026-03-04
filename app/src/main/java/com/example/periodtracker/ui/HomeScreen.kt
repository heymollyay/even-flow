package com.example.periodtracker.ui

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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.periodtracker.ui.theme.FollicularOrange
import com.example.periodtracker.ui.theme.LutealPink
import com.example.periodtracker.ui.theme.OvulationPurple
import com.example.periodtracker.ui.theme.PeriodRed
import com.example.periodtracker.ui.theme.White
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.periodtracker.R


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
        Spacer(modifier = Modifier.height(8.dp))

        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Text(
                text = if (username.isBlank()) "Hello, User." else "Hello, $username.",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )

        }

        WeeklyCalendarHeader()

        val daysUntilNextPeriod = UserData.getDaysUntilMenstruation(context)

        Spacer(modifier = Modifier.height(24.dp))

        PhaseChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            input = listOf(
                PhaseInput(
                    color = LutealPink,
                    value = UserData.getLutealLength(context),
                ),
                PhaseInput(
                    color = PeriodRed,
                    value = UserData.getMenstrualLength(context),
                ),
                PhaseInput(
                    color = FollicularOrange,
                    value = UserData.getFollicularLength(context),
                ),
                PhaseInput(
                    color = OvulationPurple,
                    value = UserData.getOvulationLength(context),
                ),
            ),
            centerText = if (daysUntilNextPeriod <= 0) "Menstruating" else
                "$daysUntilNextPeriod days until menstruation"
        )

        Spacer(modifier = Modifier.height(24.dp))

        CyclePhaseKey(
            modifier = Modifier.padding(vertical = 12.dp)
        )


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
    innerRadius:Float = 250f,
    input:List<PhaseInput>,
    centerText:String = "days until menstruation"
) {
    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val width = size.width
            val height = size.height
            circleCenter = Offset(x=width/2f,y=height/2f)

            val totalValue = input.sumOf {
                it.value
            }
            val anglePerValue = 360f/totalValue
            var currentStartAngle = 0f

            input.forEach {
                phaseInput ->
                val scale = 1.0f
                val angleToDraw = phaseInput.value * anglePerValue
                scale(scale) {
                    drawArc (
                        color = phaseInput.color,
                        startAngle = currentStartAngle,
                        sweepAngle = angleToDraw,
                        useCenter = true,
                        size = Size(
                            width = radius*2f,
                            height = radius*2f
                        ),
                        topLeft = Offset(
                            (width-radius*2f)/2f,
                            (height-radius*2f)/2f
                        )
                    )
                    currentStartAngle += angleToDraw
                }
                var rotateAngle = currentStartAngle-angleToDraw/2f-90f
                var factor = 1f
                if(rotateAngle>90f){
                    rotateAngle = (rotateAngle+180).mod(360f)
                    factor = -0.92f
                }

                val days = phaseInput.value

                drawContext.canvas.nativeCanvas.apply {
                    rotate(rotateAngle) {
                        drawText(
                            "$days days",
                            circleCenter.x,
                            circleCenter.y+(radius-(radius-innerRadius)/2f)*factor,
                            Paint().apply {
                                textSize = 13.sp.toPx()
                                textAlign = Paint.Align.CENTER
                                color = White.toArgb()
                            }
                        )
                    }
                }
            }
            drawContext.canvas.nativeCanvas.apply {
                drawCircle(
                    circleCenter.x,
                    circleCenter.y,
                    innerRadius,
                    Paint().apply {
                        color = White.toArgb()
                    }
                )
            }

        }
        Text(
            centerText,
            modifier = Modifier
                .width(Dp(innerRadius/1.5f))
                .padding(25.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class PhaseInput(
    val color: Color,
    val value:Int,
)

@Composable
fun CyclePhaseKey(modifier: Modifier = Modifier) {
    val phases = listOf(
        Pair("Luteal", LutealPink),
        Pair("Menstruation", PeriodRed),
        Pair("Follicular", FollicularOrange),
        Pair("Ovulation", OvulationPurple)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            phases.forEach { (label, color) ->
                PhaseKeyItem(label = label, color = color)
            }
        }
    }
}

@Composable
fun PhaseKeyItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Normal,
            color = White
        )
    }
}

