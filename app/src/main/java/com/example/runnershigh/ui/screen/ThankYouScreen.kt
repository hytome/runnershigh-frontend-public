package com.example.runnershigh.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.runnershigh.ui.theme.RacingSansOne
import kotlinx.coroutines.delay

@Composable
fun ThankYouScreen(
    onComplete: () -> Unit  // 3초 뒤에 불러줄 콜백
) {
    // 이 Composable이 처음 보여질 때 한 번 실행
    LaunchedEffect(Unit) {
        delay(3000L)   // 3초
        onComplete()   // 3초 후 다음 화면으로 넘김
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF73F212)),   // 배경색 73F212
        color = Color(0xFF73F212)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // 가운데 체크 + 텍스트
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "완료",
                    tint = Color.White,
                    modifier = Modifier.size(96.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Thank you!",
                    fontFamily = RacingSansOne,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "입력해 준 정보를 기반으로\n러닝 플랜을 준비 중이에요.",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            // 아래 Runner's High 로고 텍스트
            Text(
                text = "Runner's High.",
                fontFamily = RacingSansOne,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 32.dp)
            )
        }
    }
}
