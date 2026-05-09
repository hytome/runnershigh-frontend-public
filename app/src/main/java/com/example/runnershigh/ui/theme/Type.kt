package com.example.runnershigh.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.runnershigh.R

// Racing Sans One 폰트 정의
val RacingSansOne = FontFamily(
    Font(R.font.racing_sans_one, FontWeight.Normal)
)

// 앱에서 사용할 기본 타이포그래피
val Typography = Typography(
    // 큰 제목
    displayLarge = TextStyle(
        fontFamily = RacingSansOne,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = 0.sp
    ),

    // 화면 제목 같은 곳
    headlineLarge = TextStyle(
        fontFamily = RacingSansOne,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),

    // 기본 본문
    bodyLarge = TextStyle(
        fontFamily = RacingSansOne,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    // 좀 더 작은 본문
    bodyMedium = TextStyle(
        fontFamily = RacingSansOne,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )
)
