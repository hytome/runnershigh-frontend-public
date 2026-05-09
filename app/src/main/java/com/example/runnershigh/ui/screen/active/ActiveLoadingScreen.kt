package com.example.runnershigh.ui.screen.active

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.runnershigh.R
import kotlinx.coroutines.delay

@Composable
fun ActiveLoadingScreen(
    feature: String,
    onFinished: (String) -> Unit
) {
    val label = when (feature) {
        "record" -> "RECORD"
        "injury" -> "INJURY"
        else -> "CONDITION"
    }

    val textColor = when (feature) {
        "record" -> Color(0xFFB8ABFF)
        "injury" -> Color(0xFFE9E2D5)
        else -> Color(0xFFF47945)
    }

    val destination = when (feature) {
        "record" -> "active/stats"
        "injury" -> "active/injury"
        else -> "active/condition"
    }

    LaunchedEffect(feature) {
        delay(900)
        onFinished(destination)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.loading_runner_real),
            contentDescription = "loading background",
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.30f))
        )

        Text(
            text = label,
            modifier = Modifier.align(Alignment.Center),
            color = textColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 52.sp
        )

        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 64.dp)
                .alpha(0.9f),
            color = Color.White
        )
    }
}
