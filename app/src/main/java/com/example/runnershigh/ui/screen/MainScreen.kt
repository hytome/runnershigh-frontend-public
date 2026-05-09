package com.example.runnershigh.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.runnershigh.R
import com.example.runnershigh.ui.theme.RacingSansOne

@Composable
fun MainScreen(onLoginClick: () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF21212))
    ) {

        // Runner's - 왼쪽 여백 줄이고, 상단 유지
        Text(
            text = "Runner's",
            color = Color.White,
            fontSize = 80.sp,
            fontFamily = RacingSansOne,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 10.dp, y = 72.dp)    // ← 왼쪽 여백 줄임
        )

        // High. - 위로 좀 당겨서 Runner’s와 간격 좁힘
        Text(
            text = "High.",
            color = Color.White,
            fontSize = 80.sp,
            fontFamily = RacingSansOne,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-16).dp, y = (-150).dp)  // ← 위로 올림
        )

        // Login 화살표
        Image(
            painter = painterResource(id = R.drawable.login_arrow),
            contentDescription = "Login",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-96).dp)
                .clickable { onLoginClick() }
        )
    }
}
