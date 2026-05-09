package com.example.runnershigh.ui.screen
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.runnershigh.domain.model.RunningStats
import com.example.runnershigh.ui.theme.RacingSansOne
import java.util.Locale
import kotlin.math.abs

@Composable
fun RunningResultScreen(
    stats: RunningStats,
    targetDistanceKm: Double,
    targetPaceSecPerKm: Int,
    onBack: () -> Unit,
    onNext: () -> Unit,
    dateTimeLabel: String,
    titleLabel: String,
    badgeAcquired: Boolean,
    gainedExperience: Int
) {
    val scrollState = rememberScrollState()

    // ----- 계산 값 -----
    val distance = stats.distanceKm
    val pace = stats.paceSecPerKm
    val timeSec = stats.durationSec
    val goalDistance = targetDistanceKm.takeIf { it > 0 } ?: distance
    val reachedGoal = targetDistanceKm > 0 && distance >= targetDistanceKm

    // 목표 거리 달성률
    val goalProgress = if (targetDistanceKm > 0) {
        (distance / targetDistanceKm).coerceIn(0.0, 1.0)
    } else {
        0.0
    }
    val goalPercentText = if (targetDistanceKm > 0) "${(goalProgress * 100).toInt()}%" else "0%"

    // 목표 페이스 / 시간과의 차이
    val hasTargetPace = targetPaceSecPerKm > 0
    val paceDiffSec = pace - targetPaceSecPerKm
    val expectedTime = if (hasTargetPace) (goalDistance * targetPaceSecPerKm).toInt() else 0
    val timeDiffSec = if (hasTargetPace) timeSec - expectedTime else 0

    val paceDiffText = when {
        !hasTargetPace -> "목표 페이스가 설정되지 않았어요"
        paceDiffSec >= 0 -> "목표 보다 ${paceDiffSec}초 느렸어요"
        else -> "목표 보다 ${abs(paceDiffSec)}초 빨랐어요"
    }

    val timeDiffMinutes = abs(timeDiffSec) / 60
    val timeDiffText = when {
        !hasTargetPace -> "목표 페이스가 없어 예상 시간을 계산할 수 없어요"
        timeDiffSec > 0 -> "목표 보다 ${timeDiffMinutes}분 더 걸렸어요"
        timeDiffSec < 0 -> "목표 보다 ${timeDiffMinutes}분 빨랐어요"
        else -> "목표와 거의 같았어요"
    }

    // ----- UI -----
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        // 상단 앱 바
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Text(
                text = "Runner's High.",
                fontFamily = RacingSansOne,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )

            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 날짜 / 제목
        Text(
            text = dateTimeLabel,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = titleLabel,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 상단 초록 요약 카드
        SummaryCard(
            distanceKm = distance,
            paceSecPerKm = pace,
            calories = stats.calories,
            heartRate = stats.avgHeartRate,
            elevationM = stats.elevationGainM,
            timeSec = timeSec,
            cadence = stats.cadence
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 목표 플랜과 비교 타이틀
        if (targetDistanceKm > 0) {
            Text(
                text = "목표 달성률",
                fontSize = 16.sp
            )


            Spacer(modifier = Modifier.height(8.dp))

            GoalProgressBar(
                progress = goalProgress.toFloat(),
                percentText = goalPercentText
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
        // 페이스 비교
        ComparisonCard(
            titleLeft = "현재 페이스",
            titleRight = "목표 페이스",
            valueLeft = "${formatPace(pace)} /Km",
            valueRight = if (hasTargetPace) "${formatPace(targetPaceSecPerKm)} /Km" else "-",
            description = paceDiffText
        )

        Spacer(modifier = Modifier.height(16.dp))


        // 완주 시간 비교
        ComparisonCard(
            titleLeft = "완주 시간",
            titleRight = "목표 완주 시간",
            valueLeft = formatTime(timeSec),
            valueRight = if (hasTargetPace) formatTime(expectedTime) else "-",
            description = timeDiffText
        )


        // 목표 거리 카드
        Spacer(modifier = Modifier.height(24.dp))

        // 목표 거리 카드
        if (targetDistanceKm > 0) {
            GoalDistanceCard(goalDistance = goalDistance)
        }

        // 칭찬 카드
        CompletionCard(reachedGoal = reachedGoal)
        Spacer(modifier = Modifier.height(24.dp))

        AchievementInfo(badgeAcquired = badgeAcquired, gainedExperience = gainedExperience)
        Spacer(modifier = Modifier.height(24.dp))

        // 다음 버튼
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center

        ) {
            FilledTonalButton(
                onClick = onNext,
                modifier = Modifier.size(72.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = ">",

                    fontFamily = RacingSansOne,
                    fontSize = 28.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SummaryCard(
    distanceKm: Double,
    paceSecPerKm: Int,
    calories: Int,
    heartRate: Int,
    elevationM: Int,
    timeSec: Int,
    cadence: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF73F212),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                text = String.format(Locale.getDefault(), "%.2f KM", distanceKm),
                fontFamily = RacingSansOne,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Divider(thickness = 3.dp, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = formatPace(paceSecPerKm),
                        fontFamily = RacingSansOne,
                        fontSize = 28.sp
                    )
                    Text(
                        text = "Pace",
                        fontFamily = RacingSansOne,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${elevationM}M",
                        fontFamily = RacingSansOne,
                        fontSize = 24.sp
                    )
                    Text(
                        text = "고도",
                        fontFamily = RacingSansOne,
                        fontSize = 20.sp
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = calories.toString(),
                        fontFamily = RacingSansOne,
                        fontSize = 28.sp
                    )
                    Text(
                        text = "Kcal",
                        fontFamily = RacingSansOne,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatTime(timeSec),
                        fontFamily = RacingSansOne,
                        fontSize = 24.sp
                    )
                    Text(
                        text = "Time",
                        fontFamily = RacingSansOne,
                        fontSize = 20.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = heartRate.toString(),
                        fontFamily = RacingSansOne,
                        fontSize = 28.sp
                    )
                    Text(
                        text = "BPM",
                        fontFamily = RacingSansOne,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = cadence.toString(),
                        fontFamily = RacingSansOne,
                        fontSize = 24.sp
                    )
                    Text(
                        text = "케이던스",
                        fontFamily = RacingSansOne,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}
    @Composable
    private fun GoalProgressBar(
        progress: Float,
        percentText: String
    ) {
        Column {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                trackColor = Color.White,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = percentText,
                fontFamily = RacingSansOne,
                fontSize = 16.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    @Composable
    private fun ComparisonCard(
        titleLeft: String,
        titleRight: String,
        valueLeft: String,
        valueRight: String,
        description: String
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(2.dp, Color.Black)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = titleLeft, fontSize = 16.sp)
                    Text(text = titleRight, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = valueLeft,
                        fontFamily = RacingSansOne,
                        fontSize = 20.sp
                    )
                    Text(
                        text = valueRight,
                        fontFamily = RacingSansOne,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Divider(thickness = 2.dp)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = description,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Composable
    private fun GoalDistanceCard(goalDistance: Double) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF73F212),
            shape = RoundedCornerShape(32.dp),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "목표 거리",
                        fontFamily = RacingSansOne,
                        fontSize = 24.sp
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "%.2f KM", goalDistance),
                        fontFamily = RacingSansOne,
                        fontSize = 32.sp
                    )
                }

                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "Goal reached",
                    modifier = Modifier.size(64.dp)
                )
            }
        }
    }

    @Composable
    private fun CompletionCard(reachedGoal: Boolean) {
        if (reachedGoal) {
            PraiseCard()
        } else {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFEFEFEF),
                shape = RoundedCornerShape(32.dp),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = "수고하셨습니다.",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "오늘의 러닝을 마쳤어요.",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }


    @Composable
    private fun PraiseCard() {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF73F212),
            shape = RoundedCornerShape(32.dp),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Thumbs up",
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "훌륭해요.",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "목표 거리를 완주 했습니다.",
                    fontSize = 16.sp
                )
                Text(
                    text = "다음에는 페이스를 좀더 높여보는게 어떨까요?",
                    fontSize = 16.sp
                )
            }
        }
    }

    @Composable
    private fun AchievementInfo(
        badgeAcquired: Boolean,
        gainedExperience: Int
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "보상", fontSize = 18.sp, fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (badgeAcquired) "배지를 획득했어요!" else "이번에는 배지를 획득하지 못했어요.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "획득 경험치: ${gainedExperience}xp",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    /* ===== 공통 포맷 함수 ===== */

    private fun formatTime(totalSeconds: Int): String {
        val min = totalSeconds / 60
        val sec = totalSeconds % 60
        return String.format(Locale.getDefault(), "%d:%02d", min, sec)
    }

    private fun formatPace(secPerKm: Int): String {
        val min = secPerKm / 60
        val sec = secPerKm % 60
        return String.format(Locale.getDefault(), "%d:%02d", min, sec)
    }
