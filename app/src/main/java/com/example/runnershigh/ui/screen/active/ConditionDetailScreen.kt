package com.example.runnershigh.ui.screen.active

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

private data class ConditionPoint(
    val label: String,
    val score: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConditionDetailScreen(
    onBackClick: () -> Unit,
    userUuid: String,
    viewModel: ActivityViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedPeriod by remember { mutableStateOf(PeriodType.MONTH) }

    LaunchedEffect(userUuid) {
        if (userUuid.isNotBlank()) {
            viewModel.loadActivityData(userUuid)
        }
    }

    val trendData = remember(uiState.conditionTrend, selectedPeriod) {
        uiState.conditionTrend
            .mapNotNull { raw ->
                val score = raw.score ?: return@mapNotNull null
                val date = raw.date ?: return@mapNotNull null
                runCatching { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date) }
                    .getOrNull()
                    ?.let { parsed ->
                        val cal = Calendar.getInstance().apply { time = parsed }
                        ConditionPoint(
                            label = when (selectedPeriod) {
                                PeriodType.MONTH -> "${cal.get(Calendar.DAY_OF_MONTH)}일"
                                PeriodType.YEAR -> "${cal.get(Calendar.MONTH) + 1}월"
                                PeriodType.ALL -> "${cal.get(Calendar.YEAR)}"
                            },
                            score = score
                        ) to cal
                    }
            }
            .filter { (_, cal) ->
                val now = Calendar.getInstance()
                when (selectedPeriod) {
                    PeriodType.ALL -> true
                    PeriodType.YEAR -> cal.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                    PeriodType.MONTH -> cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                        cal.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                }
            }
            .groupBy({ it.second }, { it.first })
            .entries
            .sortedBy { it.key.timeInMillis }
            .map { (_, points) ->
                val label = points.last().label
                val avgScore = points.map { it.score }.average().toInt()
                ConditionPoint(label = label, score = avgScore)
            }
    }

    val currentScore = uiState.conditionScore ?: uiState.conditionLevel.takeIf { it > 0 }
    val statusText = uiState.conditionStatus?.takeIf { it.isNotBlank() }
        ?: uiState.conditionAnalysis?.takeIf { it.isNotBlank() }
        ?: "컨디션 상태를 계산 중입니다."
    val changeText = remember(trendData) {
        val recentTwo = trendData.takeLast(2)
        if (recentTwo.size < 2) {
            "최근 추이 데이터가 충분하지 않습니다"
        } else {
            val diff = recentTwo.last().score - recentTwo.first().score
            when {
                diff > 0 -> "최근 컨디션 +${diff} 상승"
                diff < 0 -> "최근 컨디션 ${abs(diff)} 하락"
                else -> "최근 컨디션 변화 없음"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("컨디션 레벨") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF4F6FA),
                    titleContentColor = Color(0xFF111827),
                    navigationIconContentColor = Color(0xFF111827)
                )
            )
        },
        containerColor = Color(0xFFF6F8FC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PeriodSelector(selectedPeriod = selectedPeriod, onPeriodSelected = { selectedPeriod = it })

            when {
                uiState.isLoading && trendData.isEmpty() && currentScore == null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFFF8A3D))
                    }
                }
                uiState.errorMessage?.isNotBlank() == true && trendData.isEmpty() && currentScore == null -> {
                    ErrorCard(uiState.errorMessage ?: "컨디션 데이터를 불러오지 못했습니다.")
                }
            }

            ConditionTrendCard(
                periodType = selectedPeriod,
                data = trendData,
                isLoading = uiState.isLoading
            )

            CurrentConditionLevelCard(
                currentScore = currentScore,
                status = statusText,
                recentChangeText = changeText
            )

            ConditionCoachAdviceCard(
                advice = uiState.conditionRecommendation,
                isLoading = uiState.isLoading
            )
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4F4))
    ) {
        Text(
            text = message,
            color = Color(0xFFC62828),
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ConditionTrendCard(
    periodType: PeriodType,
    data: List<ConditionPoint>,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = when (periodType) {
                    PeriodType.MONTH -> "월간 컨디션 레벨 추이"
                    PeriodType.YEAR -> "연간 컨디션 레벨 추이"
                    PeriodType.ALL -> "전체 컨디션 레벨 추이"
                },
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )
            Text("실제 서버 trend_graph 데이터 기반", color = Color(0xFF6B7280), fontSize = 12.sp)
            Spacer(Modifier.height(12.dp))

            if (data.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (isLoading) "데이터 로딩 중..." else "표시할 컨디션 추이 데이터가 없습니다.",
                        color = Color(0xFF9CA3AF)
                    )
                }
            } else {
                ConditionTrendLineChart(data)
            }
        }
    }
}

@Composable
private fun ConditionTrendLineChart(data: List<ConditionPoint>) {
    Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        val left = 30f
        val right = 16f
        val top = 16f
        val bottom = 28f
        val width = size.width - left - right
        val height = size.height - top - bottom

        drawLine(
            color = Color(0xFFD1D5DB),
            start = Offset(left, size.height - bottom),
            end = Offset(size.width - right, size.height - bottom),
            strokeWidth = 2f
        )

        repeat(4) { idx ->
            val y = top + (height * idx / 3f)
            drawLine(
                color = Color(0xFFE5E7EB),
                start = Offset(left, y),
                end = Offset(size.width - right, y),
                strokeWidth = 1f
            )
        }

        val maxScore = data.maxOf { it.score }.coerceAtLeast(100)
        val minScore = data.minOf { it.score }.coerceAtMost(0)
        val range = (maxScore - minScore).coerceAtLeast(1)
        val step = width / data.size.coerceAtLeast(1)

        val points = data.mapIndexed { index, point ->
            val x = left + index * step + (step / 2f)
            val y = top + ((maxScore - point.score).toFloat() / range.toFloat()) * height
            Offset(x, y)
        }

        if (points.size > 1) {
            val path = Path().apply {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { lineTo(it.x, it.y) }
            }
            drawPath(path = path, color = Color(0xFFFF8A3D), style = Stroke(width = 4f, cap = StrokeCap.Round))
        }
        points.forEach { drawCircle(color = Color(0xFFFF8A3D), radius = 5f, center = it) }
    }

    Spacer(Modifier.height(8.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(data.firstOrNull()?.label.orEmpty(), fontSize = 11.sp, color = Color(0xFF6B7280))
        Text(data.lastOrNull()?.label.orEmpty(), fontSize = 11.sp, color = Color(0xFF6B7280))
    }
}

@Composable
private fun CurrentConditionLevelCard(
    currentScore: Int?,
    status: String,
    recentChangeText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0x1AFF8A3D), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.MonitorHeart, contentDescription = null, tint = Color(0xFFFF8A3D))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("현재 컨디션 레벨", fontSize = 13.sp, color = Color(0xFF6B7280))
                Text(
                    text = currentScore?.let { "$it 점" } ?: "데이터 없음",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                    color = Color(0xFF111827)
                )
                Spacer(Modifier.height(4.dp))
                Text(status, color = Color(0xFF374151), fontSize = 13.sp)
                Text(recentChangeText, color = Color(0xFF0E8AD9), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ConditionCoachAdviceCard(advice: String?, isLoading: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color(0xFF7A67F8))
                Text("AI 러닝 코치의 조언", fontWeight = FontWeight.Bold, color = Color(0xFF111827))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = when {
                    isLoading && advice.isNullOrBlank() -> "오늘의 컨디션 기반 추천을 불러오는 중입니다..."
                    advice.isNullOrBlank() -> "오늘의 컨디션 기반 추천이 준비 중입니다. 곧 맞춤 조언이 표시됩니다."
                    else -> advice
                },
                color = Color(0xFF4B5563),
                lineHeight = 20.sp
            )
        }
    }
}
