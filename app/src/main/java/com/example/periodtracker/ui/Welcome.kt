package com.example.periodtracker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.periodtracker.R
import com.example.periodtracker.ui.theme.DeepPurple
import com.example.periodtracker.ui.theme.White

@Composable
fun Welcome(onContinueClicked: () -> Unit, modifier: Modifier = Modifier, buttonName: String) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //logo
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Even Flow logo",
            modifier = Modifier.size(260.dp)
        )

        Spacer(modifier = modifier.height(48.dp))

        //App name
        Text (
            text = "Even Flow.",
            color = White,
            fontSize = 52.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        //tagline
        Text(
            text = "Tracking your period just got easier.",
            color = White.copy(alpha = 0.85f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = modifier.height(48.dp))

        //continue
        Button(
            onClick = onContinueClicked,
            modifier = Modifier
                .padding(vertical = 24.dp)
                .height(52.dp)
                .widthIn(min = 200.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = White,
                contentColor = DeepPurple
            ),
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = buttonName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }

    }
}