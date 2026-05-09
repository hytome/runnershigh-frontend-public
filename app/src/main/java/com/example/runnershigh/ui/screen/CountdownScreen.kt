package com.example.runnershigh.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.runnershigh.ui.theme.RacingSansOne
import kotlinx.coroutines.delay

@Composable
fun CountdownScreen(
    onComplete: () -> Unit
) {
    var count by remember { mutableStateOf(3) }

    // count 값이 바뀔 때마다 타이머 동작
    LaunchedEffect(count) {
        if (count == 0) {
            // "Go!"를 잠깐 보여주고 완료 콜백
            delay(500L)
            onComplete()
        } else {
            delay(1000L)
            count -= 1
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF73F212)),   // #73F212 배경
        contentAlignment = Alignment.Center
    ) {
        val text = if (count == 0) "Go!" else count.toString()

        Text(
            text = text,
            fontFamily = RacingSansOne,
            fontSize = 200.sp,          // 필요하면 크기 조절 가능
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
    }
}
