package com.example.runnershigh.ui.screen.active

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ActiveHubScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .padding(horizontal = 20.dp, vertical = 28.dp)
    ) {
        Text(
            text = "Runners high agent",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "더욱 건강한 러닝을 위한 당신만의 에이전트.",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF9E9E9E)
        )

        Spacer(modifier = Modifier.height(92.dp))

        HubActionCard(
            badgeText = "Record",
            title = "🏃 러닝 기록 / 통계",
            subtitle = "전체·연간·월간 활동과 최근 러닝 기록 보기",
            metric = "STATS",
            containerColor = Color(0xFFB8ABFF),
            onClick = { navController.navigate("active/loading/record") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        HubActionCard(
            badgeText = "Injury",
            title = "🩹 부상 불편함 통계",
            subtitle = "통증/불편감 분석과 관련 안내 확인",
            metric = "INJURY",
            containerColor = Color(0xFFE9E2D5),
            onClick = { navController.navigate("active/loading/injury") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        HubActionCard(
            badgeText = "Condition",
            title = "💪 컨디션 레벨 통계",
            subtitle = "오늘 컨디션 점수와 레벨 분석 보기",
            metric = "LEVEL",
            containerColor = Color(0xFFF47945),
            onClick = { navController.navigate("active/loading/condition") }
        )
    }
}

@Composable
private fun HubActionCard(
    badgeText: String,
    title: String,
    subtitle: String,
    metric: String,
    containerColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.03f else 1f,
        label = "hub_card_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(containerColor, RoundedCornerShape(28.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(18.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.BottomStart)) {
            Text(
                text = title,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                lineHeight = 34.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                color = Color(0x8A000000),
                fontSize = 14.sp
            )
        }

        Text(
            text = metric,
            modifier = Modifier.align(Alignment.BottomEnd),
            color = Color.Black,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleBadge(badgeText)
            CircleArrow()
        }
    }
}

@Composable
private fun CircleBadge(text: String) {
    Box(
        modifier = Modifier
            .background(Color(0x14FFFFFF), CircleShape)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
private fun CircleArrow() {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(Color(0x14FFFFFF), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "이동",
            tint = Color.Black
        )
    }
}
