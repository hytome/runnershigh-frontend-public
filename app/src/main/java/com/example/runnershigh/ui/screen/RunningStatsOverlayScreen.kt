package com.example.runnershigh.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.runnershigh.ui.theme.RacingSansOne
import java.util.Locale
import androidx.compose.foundation.verticalScroll
import com.example.runnershigh.ui.RunningViewModel
import com.example.runnershigh.util.parsePaceToSeconds
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import kotlin.math.roundToInt
import androidx.compose.runtime.getValue
/**
 * 러닝 중 비교/요약 화면 (Figma에 있던 비교창)
 *
 * - 러닝 중 아무 곳이나 터치했을 때 이 화면으로 네비게이트 해서 사용하면 됨.
 * - onBack() 에 navController.popBackStack() 같은 걸 연결.
 */
@Composable
fun RunningStatsOverlayScreen(
    currentPaceSecPerKm: Int,
    targetPaceSecPerKm: Int,
    currentDistanceKm: Double,
    targetDistanceKm: Double,
    planGoalDistanceKm: Double?,
    planGoalPaceSecPerKm: Int?,
    planGoalTitle: String?,
    elapsedSeconds: Int,
    onBack: () -> Unit
) {
    val hasPaceData = currentPaceSecPerKm > 0 && targetPaceSecPerKm > 0
    // 페이스 차이 (초)
    val paceDiffText = if (hasPaceData) {
        val paceDiff = currentPaceSecPerKm - targetPaceSecPerKm
        if (paceDiff >= 0) "${paceDiff} 초 느림." else "${kotlin.math.abs(paceDiff)} 초 빠름."
    } else {
        "없습니다."
    }

    // 거리 차이
    val distanceDiff = (targetDistanceKm - currentDistanceKm).coerceAtLeast(0.0)
    val distanceDiffText =
        String.format(Locale.getDefault(), "%.2fKm 남음.", distanceDiff)

    // 남은 거리 기준 예상 추가 시간 (현재 페이스 유지 시)
    val remainingSeconds =
        if (distanceDiff > 0 && currentPaceSecPerKm > 0) (distanceDiff * currentPaceSecPerKm).toInt() else 0
    val remainingTimeText = when {
        remainingSeconds <= 0 -> "없습니다."
        remainingSeconds < 60 -> "< 1분"
        else -> {
            val remainingMin = remainingSeconds / 60
            val remainingSec = remainingSeconds % 60
            String.format(Locale.getDefault(), "+%d:%02d", remainingMin, remainingSec)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable { onBack() }, // 아무 곳이나 탭 → 뒤로
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),   // ✅ 스크롤 추가
            horizontalAlignment = Alignment.CenterHorizontally        ) {
            /* 상단 로고 + 뒤로가기 */

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Runner's Hign.",
                    fontFamily = RacingSansOne,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 0. 오늘의 플랜 목표 요약
            if ((planGoalDistanceKm != null && planGoalDistanceKm > 0) || planGoalPaceSecPerKm != null || !planGoalTitle.isNullOrBlank()) {
                ComparisonCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "오늘의 플랜",
                            fontSize = 18.sp,
                            fontFamily = RacingSansOne,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        planGoalTitle?.takeIf { it.isNotBlank() }?.let { title ->
                            Text(
                                text = title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        planGoalDistanceKm?.takeIf { it > 0 }?.let { distance ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OverlayTag(text = String.format(Locale.getDefault(), "목표 거리 %.2fKm", distance))
                            }
                        }

                        planGoalPaceSecPerKm?.let { paceSec ->
                            Text(
                                text = "목표 페이스: ${formatPace(paceSec)} /Km",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // 1. 페이스 비교 카드
            ComparisonCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("현재 페이스", fontSize = 14.sp)
                        Text(
                            text = formatPaceOrEmpty(currentPaceSecPerKm),
                            fontFamily = RacingSansOne,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.DirectionsRun,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text("목표 페이스", fontSize = 14.sp)
                        Text(
                            text = formatPaceOrEmpty(targetPaceSecPerKm),
                            fontFamily = RacingSansOne,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                CenterBadge(text = paceDiffText)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. 거리 비교 카드
            ComparisonCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("현재 거리.", fontSize = 14.sp)
                        Text(
                            text = String.format(Locale.getDefault(), "%.2fKm", currentDistanceKm),
                            fontFamily = RacingSansOne,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text("목표 거리.", fontSize = 14.sp)
                        Text(
                            text = String.format(Locale.getDefault(), "%.2fKm", targetDistanceKm),
                            fontFamily = RacingSansOne,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                CenterBadge(text = distanceDiffText)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. 예상 시간 카드
            ComparisonCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("현재 페이스 유지시.", fontSize = 14.sp)
                        Text(
                            text = "예상 추가 시간",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.TrendingDown,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black
                    ) {
                        Text(
                            text = remainingTimeText,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontFamily = RacingSansOne,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. 러닝팁 카드
            ComparisonCard {
                Text(
                    text = "러닝팁",
                    fontSize = 20.sp,
                    fontFamily = RacingSansOne
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "일정한 페이스 유지를 위해 호흡을 집중하세요.\n\n" +
                            "코로 들이마시고 입으로 뱉는 리듬을 유지하세요.",
                    fontSize = 15.sp
                )
            }
        }
    }
}

/* 공통 카드 UI */
@Composable
private fun ComparisonCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            content = content
        )
    }
}

@Composable
private fun OverlayTag(text: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE5F3CC)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

/* 중앙 검은 배지 */
@Composable
private fun CenterBadge(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.Black
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
            )
        }
    }
}

/* 포맷 함수들 */

private fun formatPace(secPerKm: Int): String {
    val min = secPerKm / 60
    val sec = secPerKm % 60
    return String.format(Locale.getDefault(), "%d:%02d /Km", min, sec)
}

private fun formatPaceOrEmpty(secPerKm: Int): String {
    return if (secPerKm <= 0) "없습니다." else formatPace(secPerKm)
}

@Composable
fun RunningStatsOverlayRoute(
    runningViewModel: RunningViewModel,
    distanceKm: Double,
    elapsedSeconds: Int,
    onBack: () -> Unit
) {
    // 1) 화면에 들어오면 서버 비교 API 호출
    LaunchedEffect(distanceKm, elapsedSeconds) {
        runningViewModel.loadRunningComparison(distanceKm * 1000, elapsedSeconds)
    }

    // 2) ViewModel 의 compareState 구독
    val compare = runningViewModel.compareState.collectAsState().value
    val planGoal = runningViewModel.planGoal.collectAsState().value

    // 3) 아직 로딩 중이면 간단한 텍스트 표시 (원하면 로딩 스피너로 교체)
    if (compare == null && planGoal.targetDistanceKm <= 0 && planGoal.targetPaceSecPerKm == null) {
        Text("러닝 비교 데이터를 불러오는 중...")
        return
    }

    // 4) 서버 응답을 RunningStatsOverlayScreen 포맷으로 변환
    val targetDistanceKm = planGoal.targetDistanceKm
        .takeIf { it > 0 }
        ?: compare?.targetDistanceKm
            ?.takeIf { it > 0 }
        ?: distanceKm

    val targetPaceSec = planGoal.targetPaceSecPerKm
        ?: compare?.targetPaceSec
        ?: 0
    val minDistanceForPaceKm = 0.05
    val currentPaceSec = if (distanceKm >= minDistanceForPaceKm && elapsedSeconds > 0) {
        (elapsedSeconds / distanceKm).roundToInt()
    } else {
        0
    }

    // TODO: elapsedSeconds 는 아직 API에 없으니까,
    //       나중에 RunningViewModel에서 실제 경과시간을 들고 오면 좋아.
    val currentDistanceKm = distanceKm


    // 5) 실제 UI 컴포저블 호출
    RunningStatsOverlayScreen(
        currentPaceSecPerKm = currentPaceSec,
        targetPaceSecPerKm = targetPaceSec,
        currentDistanceKm = currentDistanceKm,
        targetDistanceKm = targetDistanceKm,
        planGoalDistanceKm = planGoal.targetDistanceKm.takeIf { it > 0 },
        planGoalPaceSecPerKm = planGoal.targetPaceSecPerKm,
        planGoalTitle = planGoal.planTitle,
        elapsedSeconds = elapsedSeconds,
        onBack = onBack
    )
}
