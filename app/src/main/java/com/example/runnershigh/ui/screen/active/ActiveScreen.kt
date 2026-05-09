package com.example.runnershigh.ui.screen.active

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.collections.LinkedHashMap
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin

enum class PeriodType { MONTH, YEAR, ALL }

data class RunningStats(
    val totalDistanceKm: Double,
    val runCount: Int,
    val avgPace: String,
    val avgHeartRate: Int,
    val avgCadence: Int
)

data class DailyActivityData(val day: Int, val distanceKm: Double)
data class MonthlyActivityData(val month: Int, val distanceKm: Double)
data class TotalActivityData(val year: Int, val month: Int, val distanceKm: Double)

data class RunningData(
    val dateLabel: String,
    val distanceKm: Double,
    val calories: Int,
    val paceSeconds: Int,
    val heartRate: Int,
    val cadence: Int
)

private data class CombinedTrendData(
    val label: String,
    val distanceKm: Double,
    val paceSeconds: Int
)

private data class ConditionTrendData(
    val label: String,
    val score: Int
)

private data class StreakStats(
    val currentStreak: Int,
    val longestStreak: Int,
    val twoDayStreakCount: Int,
    val threeDayStreakCount: Int,
    val activeDays: Int,
    val periodDays: Int,
    val consistencyRate: Int,
    val nextMilestone: Int,
    val daysToNextMilestone: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("UNUSED_PARAMETER")
fun ActiveScreen(
    navController: NavController,
    userUuid: String,
    userHeightCm: Double?,
    userWeightKg: Double?,
    errorMessage: String? = null,
    isLoading: Boolean = false,
    recentActivities: List<RunningData> = emptyList(),
    viewModel: ActivityViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var selectedPeriod by remember { mutableStateOf(PeriodType.MONTH) }
    val scrollState = rememberScrollState()

    LaunchedEffect(userUuid, userHeightCm, userWeightKg) {
        if (userUuid.isNotBlank()) {
            viewModel.loadActivityData(userUuid, userHeightCm, userWeightKg)
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val periodStats = uiState.statsFor(selectedPeriod)
    val trendData = remember(uiState.allActivities, selectedPeriod) {
        uiState.combinedTrendFor(selectedPeriod)
    }
    val recentRuns = uiState.allActivities.take(5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F4F8))
    ) {
        TopAppBar(
            title = { Text("러닝 기록/통계") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "뒤로")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFB8ABFF),
                titleContentColor = Color.Black,
                navigationIconContentColor = Color.Black
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            HeroSummaryCard(periodStats = periodStats)

            PeriodSelector(selectedPeriod = selectedPeriod, onPeriodSelected = { selectedPeriod = it })

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF7A67F8))
                }
            }

            uiState.errorMessage
                ?.takeIf { it.isNotBlank() }
                ?.let { ErrorCard(it) }

            CombinedDistancePaceSection(selectedPeriod, trendData)
            MetricsGrid(stats = periodStats)
            RecentCoursesSection(recentRuns)
        }
    }
}

private fun ActivityUiState.statsFor(periodType: PeriodType): RunningStats {
    val source = when (periodType) {
        PeriodType.MONTH -> summary
        PeriodType.YEAR -> yearly
        PeriodType.ALL -> total
    }
    val periodActivities = filteredActivities(periodType)
    val fallbackDistance = periodActivities.sumOf { it.distance }
    val fallbackCount = periodActivities.size
    val fallbackPaceSec = periodActivities
        .filter { it.paceSeconds > 0 }
        .map { it.paceSeconds }
        .average()
        .takeUnless { it.isNaN() }
        ?.roundToInt()
        ?: 0
    val fallbackHeartRate = periodActivities
        .filter { it.heartRate > 0 }
        .map { it.heartRate }
        .average()
        .takeUnless { it.isNaN() }
        ?.roundToInt()
        ?: 0

    val paceSec = source?.monthlySummary?.averagePaceSeconds
        ?.takeIf { it > 0 }
        ?: fallbackPaceSec
    val cadence = periodActivities.map { it.cadence }.filter { it > 0 }.average().let {
        if (it.isNaN()) 0 else it.roundToInt()
    }

    val resolvedDistance = source?.monthlySummary?.totalDistance
        ?.takeIf { it > 0.0 }
        ?: fallbackDistance
    val resolvedRunCount = source?.monthlySummary?.totalCount
        ?.takeIf { it > 0 }
        ?: fallbackCount
    val resolvedHeartRate = source?.monthlySummary?.averageHeartRate
        ?.takeIf { it > 0 }
        ?: fallbackHeartRate

    return RunningStats(
        totalDistanceKm = resolvedDistance,
        runCount = resolvedRunCount,
        avgPace = formatPaceSeconds(paceSec),
        avgHeartRate = resolvedHeartRate,
        avgCadence = cadence
    )
}

private fun ActivityUiState.filteredActivities(periodType: PeriodType): List<com.example.runnershigh.data.remote.dto.RecentActivity> {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val now = Calendar.getInstance()
    return allActivities.filter { activity ->
        val date = runCatching { formatter.parse(activity.date) }.getOrNull() ?: return@filter false
        val cal = Calendar.getInstance().apply { time = date }
        when (periodType) {
            PeriodType.ALL -> true
            PeriodType.YEAR -> cal.get(Calendar.YEAR) == now.get(Calendar.YEAR)
            PeriodType.MONTH -> cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                cal.get(Calendar.MONTH) == now.get(Calendar.MONTH)
        }
    }
}

private fun ActivityUiState.combinedTrendFor(periodType: PeriodType): List<CombinedTrendData> {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val grouped = LinkedHashMap<String, MutableList<com.example.runnershigh.data.remote.dto.RecentActivity>>()
    filteredActivities(periodType)
        .sortedBy { it.date }
        .forEach { activity ->
            val parsed = runCatching { formatter.parse(activity.date) }.getOrNull()
            val bucketKey = when (periodType) {
                PeriodType.MONTH -> parsed?.let { Calendar.getInstance().apply { time = it }.get(Calendar.DAY_OF_MONTH).toString() } ?: activity.date
                PeriodType.YEAR -> parsed?.let { "${Calendar.getInstance().apply { time = it }.get(Calendar.MONTH) + 1}월" } ?: activity.date
                PeriodType.ALL -> parsed?.let { Calendar.getInstance().apply { time = it }.get(Calendar.YEAR).toString() } ?: activity.date
            }
            grouped.getOrPut(bucketKey) { mutableListOf() }.add(activity)
        }

    return grouped.map { (label, values) ->
        val avgPace = values
            .map { it.paceSeconds }
            .filter { it > 0 }
            .average()
            .takeUnless { it.isNaN() }
            ?.roundToInt()
            ?: 0
        CombinedTrendData(
            label = label,
            distanceKm = values.sumOf { it.distance },
            paceSeconds = avgPace
        )
    }
}

private fun ActivityUiState.conditionTrendFor(periodType: PeriodType): List<ConditionTrendData> {
    if (conditionTrend.isEmpty()) return emptyList()
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val now = Calendar.getInstance()
    return conditionTrend.mapNotNull { item ->
        val dateString = item.date ?: return@mapNotNull null
        val score = item.score ?: return@mapNotNull null
        val parsed = runCatching { formatter.parse(dateString) }.getOrNull() ?: return@mapNotNull null
        val cal = Calendar.getInstance().apply { time = parsed }
        val visible = when (periodType) {
            PeriodType.ALL -> true
            PeriodType.YEAR -> cal.get(Calendar.YEAR) == now.get(Calendar.YEAR)
            PeriodType.MONTH -> cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                cal.get(Calendar.MONTH) == now.get(Calendar.MONTH)
        }
        if (!visible) return@mapNotNull null
        val label = when (periodType) {
            PeriodType.MONTH -> "${cal.get(Calendar.DAY_OF_MONTH)}일"
            PeriodType.YEAR -> "${cal.get(Calendar.MONTH) + 1}월"
            PeriodType.ALL -> "${cal.get(Calendar.YEAR)}"
        }
        ConditionTrendData(label, score)
    }
}

private fun buildStreakStats(
    activities: List<com.example.runnershigh.data.remote.dto.RecentActivity>,
    periodType: PeriodType
): StreakStats {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val now = Calendar.getInstance()

    val dateSet = activities.mapNotNull {
        runCatching { formatter.parse(it.date) }.getOrNull()
    }.filter { date ->
        val cal = Calendar.getInstance().apply { time = date }
        when (periodType) {
            PeriodType.ALL -> true
            PeriodType.YEAR -> cal.get(Calendar.YEAR) == now.get(Calendar.YEAR)
            PeriodType.MONTH -> cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                cal.get(Calendar.MONTH) == now.get(Calendar.MONTH)
        }
    }.map {
        Calendar.getInstance().apply {
            time = it
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }.toSet().toList().sorted()

    if (dateSet.isEmpty()) {
        return StreakStats(
            currentStreak = 0,
            longestStreak = 0,
            twoDayStreakCount = 0,
            threeDayStreakCount = 0,
            activeDays = 0,
            periodDays = 1,
            consistencyRate = 0,
            nextMilestone = 3,
            daysToNextMilestone = 3
        )
    }

    var longest = 1
    var current = 1
    var twoPlus = 0
    var threePlus = 0

    for (i in 1 until dateSet.size) {
        val diffDay = (dateSet[i] - dateSet[i - 1]) / (24 * 60 * 60 * 1000)
        if (diffDay == 1L) {
            current += 1
        } else {
            if (current >= 2) twoPlus += 1
            if (current >= 3) threePlus += 1
            longest = maxOf(longest, current)
            current = 1
        }
    }
    if (current >= 2) twoPlus += 1
    if (current >= 3) threePlus += 1
    longest = maxOf(longest, current)

    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val currentStreak = if (dateSet.last() == today || dateSet.last() == today - (24 * 60 * 60 * 1000)) current else 0

    val activeDays = dateSet.size
    val periodDays = when (periodType) {
        PeriodType.MONTH -> now.getActualMaximum(Calendar.DAY_OF_MONTH)
        PeriodType.YEAR -> now.getActualMaximum(Calendar.DAY_OF_YEAR)
        PeriodType.ALL -> {
            val spanDays = ((dateSet.last() - dateSet.first()) / (24 * 60 * 60 * 1000)).toInt() + 1
            spanDays.coerceAtLeast(1)
        }
    }
    val consistencyRate = ((activeDays.toFloat() / periodDays.toFloat()) * 100f).roundToInt().coerceIn(0, 100)
    val milestones = listOf(3, 7, 14, 21, 30, 50, 100)
    val nextMilestone = milestones.firstOrNull { it > currentStreak } ?: (currentStreak + 10)
    val daysToNextMilestone = (nextMilestone - currentStreak).coerceAtLeast(0)

    return StreakStats(
        currentStreak = currentStreak,
        longestStreak = longest,
        twoDayStreakCount = twoPlus,
        threeDayStreakCount = threePlus,
        activeDays = activeDays,
        periodDays = periodDays,
        consistencyRate = consistencyRate,
        nextMilestone = nextMilestone,
        daysToNextMilestone = daysToNextMilestone
    )
}

@Composable
private fun HeroSummaryCard(periodStats: RunningStats) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFB8ABFF)), shape = RoundedCornerShape(28.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text("Record", fontWeight = FontWeight.Bold, color = Color(0xCC000000))
            Spacer(Modifier.height(8.dp))
            Text(
                text = String.format(Locale.getDefault(), "%.1fkm", periodStats.totalDistanceKm),
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text("선택 구간 누적 거리", color = Color(0xAA000000))
        }
    }
}

@Composable
fun PeriodSelector(selectedPeriod: PeriodType, onPeriodSelected: (PeriodType) -> Unit) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.fillMaxWidth().padding(6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf(PeriodType.MONTH to "월", PeriodType.YEAR to "년", PeriodType.ALL to "전체").forEach { (type, label) ->
                val selected = selectedPeriod == type
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .background(if (selected) Color(0xFF1D1D1D) else Color.Transparent, RoundedCornerShape(14.dp))
                        .clickable { onPeriodSelected(type) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, color = if (selected) Color.White else Color(0xFF666666), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun MetricsGrid(stats: RunningStats) {
    val items = listOf(
        "러닝 횟수" to "${stats.runCount}회",
        "평균 페이스" to stats.avgPace,
        "평균 심박수" to if (stats.avgHeartRate > 0) "${stats.avgHeartRate} bpm" else "-",
        "평균 케이던스" to if (stats.avgCadence > 0) "${stats.avgCadence} spm" else "-"
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (chunk in items.chunked(2)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                chunk.forEach { (label, value) ->
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(label, color = Color.Gray, fontSize = 12.sp)
                            Spacer(Modifier.height(6.dp))
                            Text(value, fontWeight = FontWeight.Bold, fontSize = 19.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RadarSection(stats: RunningStats) {
    val paceSeconds = parsePace(stats.avgPace)
    val metrics = listOf(
        RadarMetric(
            key = "KM",
            label = "누적 거리",
            valueText = String.format(Locale.getDefault(), "%.1fkm", stats.totalDistanceKm),
            normalized = (stats.totalDistanceKm / 120.0).coerceIn(0.0, 1.0).toFloat()
        ),
        RadarMetric(
            key = "PACE",
            label = "평균 페이스",
            valueText = if (paceSeconds > 0) stats.avgPace else "-",
            normalized = if (paceSeconds > 0) (1f - ((paceSeconds - 240f) / 300f).coerceIn(0f, 1f)) else 0f
        ),
        RadarMetric(
            key = "HR",
            label = "평균 심박",
            valueText = if (stats.avgHeartRate > 0) "${stats.avgHeartRate} bpm" else "-",
            normalized = (stats.avgHeartRate / 190f).coerceIn(0f, 1f)
        ),
        RadarMetric(
            key = "CAD",
            label = "평균 케이던스",
            valueText = if (stats.avgCadence > 0) "${stats.avgCadence} spm" else "-",
            normalized = (stats.avgCadence / 200f).coerceIn(0f, 1f)
        )
    )
    val values = metrics.map { it.normalized }

    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("핵심 지표 레이더", fontWeight = FontWeight.Bold)
            Text(
                "레이더는 0~100 정규화 점수입니다. 아래 실측값과 함께 확인하세요.",
                color = Color(0xFF707070),
                fontSize = 12.sp
            )
            Spacer(Modifier.height(12.dp))
            RadarChart(values = values, labels = listOf("KM", "PACE", "HR", "CAD"))
            Spacer(Modifier.height(12.dp))
            metrics.forEach { metric ->
                RadarMetricRow(metric = metric)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

private data class RadarMetric(
    val key: String,
    val label: String,
    val valueText: String,
    val normalized: Float
)

@Composable
private fun RadarMetricRow(metric: RadarMetric) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${metric.key} · ${metric.label}", fontSize = 12.sp, color = Color(0xFF555555))
            Text(metric.valueText, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { metric.normalized.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = Color(0xFF7A67F8),
            trackColor = Color(0xFFE8E7EE)
        )
    }
}

@Composable
private fun RadarChart(values: List<Float>, labels: List<String>) {
    Box(modifier = Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(200.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = size.minDimension / 2f - 10f
            val pointCount = values.size
            val benchmarkRatio = 0.65f

            (1..4).forEach { level ->
                val ratio = level / 4f
                val ring = Path()
                repeat(pointCount) { idx ->
                    val angle = (2 * PI * idx / pointCount - PI / 2).toFloat()
                    val x = center.x + maxRadius * ratio * cos(angle)
                    val y = center.y + maxRadius * ratio * sin(angle)
                    if (idx == 0) ring.moveTo(x, y) else ring.lineTo(x, y)
                }
                ring.close()
                drawPath(ring, Color(0xFFE8E7EE), style = Stroke(1.5f))
            }

            val benchmarkPath = Path()
            repeat(pointCount) { idx ->
                val angle = (2 * PI * idx / pointCount - PI / 2).toFloat()
                val x = center.x + maxRadius * benchmarkRatio * cos(angle)
                val y = center.y + maxRadius * benchmarkRatio * sin(angle)
                if (idx == 0) benchmarkPath.moveTo(x, y) else benchmarkPath.lineTo(x, y)
            }
            benchmarkPath.close()
            drawPath(benchmarkPath, Color(0x267A67F8))
            drawPath(benchmarkPath, Color(0x807A67F8), style = Stroke(1.2f))

            repeat(pointCount) { idx ->
                val angle = (2 * PI * idx / pointCount - PI / 2).toFloat()
                drawLine(
                    color = Color(0xFFD4D2E0),
                    start = center,
                    end = Offset(center.x + maxRadius * cos(angle), center.y + maxRadius * sin(angle)),
                    strokeWidth = 1.5f
                )
            }

            val dataPath = Path()
            values.forEachIndexed { idx, value ->
                val angle = (2 * PI * idx / pointCount - PI / 2).toFloat()
                val radius = maxRadius * value
                val point = Offset(center.x + radius * cos(angle), center.y + radius * sin(angle))
                if (idx == 0) dataPath.moveTo(point.x, point.y) else dataPath.lineTo(point.x, point.y)
                drawCircle(color = Color(0xFF7A67F8), radius = 4f, center = point)
            }
            dataPath.close()
            drawPath(dataPath, Color(0x4D7A67F8))
            drawPath(dataPath, Color(0xFF7A67F8), style = Stroke(2.5f, cap = StrokeCap.Round))
        }

        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(10.dp))
                Text("100", fontSize = 9.sp, color = Color(0xFF8A88A0))
            }
            Text(labels[0], modifier = Modifier.align(Alignment.CenterHorizontally), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(labels[3], fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                Text(labels[1], fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
            Text(labels[2], modifier = Modifier.align(Alignment.CenterHorizontally), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun parsePace(pace: String): Float {
    if (!pace.contains("'")) return 0f
    val minutes = pace.substringBefore("'").toIntOrNull() ?: return 0f
    val seconds = pace.substringAfter("'").substringBefore('"').toIntOrNull() ?: 0
    return (minutes * 60 + seconds).toFloat()
}

@Composable
private fun CombinedDistancePaceSection(periodType: PeriodType, trend: List<CombinedTrendData>) {
    var selectedIndex by remember(trend) { mutableStateOf(trend.lastIndex.coerceAtLeast(0)) }
    val selectedData = trend.getOrNull(selectedIndex)

    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = when (periodType) {
                    PeriodType.MONTH -> "월간 러닝 거리/페이스"
                    PeriodType.YEAR -> "연간 러닝 거리/페이스"
                    PeriodType.ALL -> "누적 러닝 거리/페이스"
                },
                fontWeight = FontWeight.Bold
            )
            Text("왼쪽축: 거리(km) · 오른쪽축: 페이스(분:초/km)", color = Color(0xFF6E6A82), fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricPill("총 ${trend.size}구간")
                MetricPill("누적 ${formatDistanceKm(trend.sumOf { it.distanceKm })}")
                MetricPill("평균 ${formatPaceSeconds(trend.map { it.paceSeconds }.filter { it > 0 }.average().takeUnless { it.isNaN() }?.roundToInt() ?: 0)}")
            }
            Spacer(Modifier.height(12.dp))
            if (trend.isEmpty()) {
                Text("데이터가 없습니다.", color = Color.Gray)
            } else {
                selectedData?.let { point ->
                    Text(
                        text = "${point.label} · ${formatDistanceKm(point.distanceKm)} · ${formatPaceSeconds(point.paceSeconds)}/km",
                        color = Color(0xFF2E2A40),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(6.dp))
                }
                CombinedDistancePaceChart(
                    data = trend,
                    periodType = periodType,
                    selectedIndex = selectedIndex,
                    onSelectIndex = { selectedIndex = it }
                )
            }
        }
    }
}

@Composable
private fun CombinedDistancePaceChart(
    data: List<CombinedTrendData>,
    periodType: PeriodType,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .pointerInput(data) {
                detectTapGestures { offset ->
                    if (data.isEmpty()) return@detectTapGestures
                    val leftPadding = 42f
                    val rightPadding = 42f
                    val width = size.width - leftPadding - rightPadding
                    val step = width / data.size.coerceAtLeast(1)
                    val index = ((offset.x - leftPadding) / step).roundToInt()
                        .coerceIn(0, data.lastIndex)
                    onSelectIndex(index)
                }
            }
    ) {
        val leftPadding = 42f
        val rightPadding = 42f
        val topPadding = 18f
        val bottomPadding = 36f
        val chartWidth = size.width - leftPadding - rightPadding
        val chartHeight = size.height - topPadding - bottomPadding
        val distanceMax = data.maxOfOrNull { it.distanceKm }?.toFloat()?.coerceAtLeast(1f) ?: 1f
        val distanceTicks = 4
        val distanceTickStep = max(1f, distanceMax / distanceTicks)
        val paceValues = data.map { it.paceSeconds }.filter { it > 0 }
        val paceMin = ((paceValues.minOrNull() ?: 240) / 30 * 30).toFloat()
        val paceMax = (((paceValues.maxOrNull() ?: 420) + 29) / 30 * 30).toFloat().coerceAtLeast(paceMin + 30f)
        val step = chartWidth / data.size.coerceAtLeast(1)
        val barWidth = step * 0.54f
        val nativeCanvas = drawContext.canvas.nativeCanvas
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#6E6A82")
            textSize = 24f
            textAlign = android.graphics.Paint.Align.RIGHT
            isAntiAlias = true
        }
        val rightTextPaint = android.graphics.Paint(textPaint).apply {
            textAlign = android.graphics.Paint.Align.LEFT
        }
        val barLabelPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#273469")
            textSize = 22f
            textAlign = android.graphics.Paint.Align.CENTER
            isFakeBoldText = true
            isAntiAlias = true
        }

        for (tick in 0..distanceTicks) {
            val value = distanceTickStep * tick
            val ratio = (value / distanceMax).coerceIn(0f, 1f)
            val y = size.height - bottomPadding - ratio * chartHeight
            drawLine(
                color = if (tick == 0) Color(0xFFD9D7E6) else Color(0xFFECEAF4),
                start = Offset(leftPadding, y),
                end = Offset(size.width - rightPadding, y),
                strokeWidth = if (tick == 0) 2f else 1f
            )
            nativeCanvas.drawText("${value.roundToInt()}km", leftPadding - 8f, y + 7f, textPaint)
        }

        val paceTickStep = 30
        val paceTickValues = (paceMin.toInt()..paceMax.toInt() step paceTickStep).toList()
        paceTickValues.forEach { sec ->
            val ratio = ((sec - paceMin) / (paceMax - paceMin)).coerceIn(0f, 1f)
            val y = topPadding + (ratio * chartHeight)
            nativeCanvas.drawText(formatPaceSeconds(sec), size.width - rightPadding + 8f, y + 7f, rightTextPaint)
        }

        data.forEachIndexed { index, item ->
            val x = leftPadding + index * step + (step - barWidth) / 2f
            val barHeight = ((item.distanceKm.toFloat() / distanceMax) * chartHeight).coerceAtLeast(2f)
            drawRoundRect(
                color = if (index == selectedIndex) Color(0xFF2E5BFF) else Color(0xFF4C7DFF),
                topLeft = Offset(x, size.height - bottomPadding - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(8f, 8f)
            )
            if (index == selectedIndex || data.size <= 10) {
                nativeCanvas.drawText(
                    formatDistanceKm(item.distanceKm).replace("km", ""),
                    x + barWidth / 2f,
                    (size.height - bottomPadding - barHeight - 8f).coerceAtLeast(16f),
                    barLabelPaint
                )
            }
        }

        val points = data.mapIndexedNotNull { index, item ->
            if (item.paceSeconds <= 0) return@mapIndexedNotNull null
            val x = leftPadding + index * step + (step / 2f)
            val ratio = (item.paceSeconds - paceMin) / (paceMax - paceMin)
            val y = topPadding + (ratio * chartHeight)
            Offset(x, y)
        }

        if (points.size > 1) {
            val linePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { lineTo(it.x, it.y) }
            }
            drawPath(linePath, color = Color(0xFFFF8A3D), style = Stroke(width = 4f, cap = StrokeCap.Round))
        }
        points.forEachIndexed { idx, point ->
            drawCircle(color = Color.White, radius = if (idx == selectedIndex) 6f else 4f, center = point)
            drawCircle(color = Color(0xFFFF8A3D), radius = if (idx == selectedIndex) 4f else 3f, center = point)
        }
    }

    Spacer(Modifier.height(8.dp))
    val xLabels = remember(data, periodType) {
        val stride = when (periodType) {
            PeriodType.MONTH -> max(1, data.size / 7)
            PeriodType.YEAR -> 1
            PeriodType.ALL -> max(1, data.size / 6)
        }
        data.mapIndexedNotNull { idx, entry ->
            if (idx % stride == 0 || idx == data.lastIndex) idx to entry.label else null
        }
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        xLabels.forEach { (_, label) ->
            Text(label, fontSize = 11.sp, color = Color(0xFF6E6A82))
        }
    }
    Spacer(Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
        LegendItem(color = Color(0xFF4C7DFF), label = "러닝 거리 (Bar, km)")
        LegendItem(color = Color(0xFFFF8A3D), label = "평균 페이스 (Line, 분:초/km)")
    }
}

@Composable
private fun MetricPill(text: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFFF3F2F8), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text = text, fontSize = 11.sp, color = Color(0xFF48445D), fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).background(color, CircleShape))
        Text(label, fontSize = 12.sp, color = Color(0xFF4A475A), fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ConditionTrendSection(periodType: PeriodType, trend: List<ConditionTrendData>) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = when (periodType) {
                    PeriodType.MONTH -> "월간 컨디션 레벨 추이"
                    PeriodType.YEAR -> "연간 컨디션 레벨 추이"
                    PeriodType.ALL -> "누적 컨디션 레벨 추이"
                },
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(10.dp))
            if (trend.isEmpty()) {
                Text("컨디션 데이터가 없습니다.", color = Color.Gray)
            } else {
                ConditionTrendChart(trend)
            }
        }
    }
}

@Composable
private fun ConditionTrendChart(data: List<ConditionTrendData>) {
    Canvas(modifier = Modifier.fillMaxWidth().height(180.dp)) {
        val left = 24f
        val right = 20f
        val top = 20f
        val bottom = 24f
        val w = size.width - left - right
        val h = size.height - top - bottom
        val maxScore = 100f
        val step = w / (data.size - 1).coerceAtLeast(1)
        val points = data.mapIndexed { index, item ->
            val x = left + step * index
            val y = top + (1f - (item.score.coerceIn(0, 100) / maxScore)) * h
            Offset(x, y)
        }
        drawLine(Color(0xFFE2E1EA), Offset(left, top + h), Offset(size.width - right, top + h), 2f)
        if (points.size > 1) {
            val path = Path().apply {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { lineTo(it.x, it.y) }
            }
            drawPath(path, Color(0xFF37A36B), style = Stroke(width = 4f, cap = StrokeCap.Round))
        }
        points.forEach {
            drawCircle(Color(0xFF37A36B), radius = 4.5f, center = it)
        }
    }
}

@Composable
private fun CurrentConditionCard(score: Int?, status: String?, analysis: String?) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF8F0))) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("현재 컨디션 레벨", fontWeight = FontWeight.Bold)
            Text(score?.let { "${it}점" } ?: "-", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1F7A4F))
            Text(status ?: "정보 없음", fontWeight = FontWeight.SemiBold, color = Color(0xFF245C41))
            Text(analysis ?: "아직 컨디션 해석 데이터가 없습니다.", color = Color(0xFF4B5563), fontSize = 13.sp)
        }
    }
}

@Composable
private fun AiCoachAdviceCard(advice: String?, isLoading: Boolean) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("AI 러닝 코치의 조언", fontWeight = FontWeight.Bold)
            when {
                isLoading -> Text("조언을 생성하고 있어요...", color = Color(0xFF6E6A82))
                advice.isNullOrBlank() -> Text(
                    "오늘의 컨디션 기반 추천이 준비 중입니다. 곧 개인화된 조언이 표시됩니다.",
                    color = Color(0xFF6E6A82)
                )
                else -> Text(advice, color = Color(0xFF1F2937))
            }
        }
    }
}

@Composable
private fun StreakSection(streakStats: StreakStats) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("연속 러닝 챌린지", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE9FF)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                    Text("현재 스트릭", color = Color(0xFF5B4FCF), fontSize = 12.sp)
                    Text("${streakStats.currentStreak}일", fontWeight = FontWeight.ExtraBold, fontSize = 32.sp)
                    Text(
                        text = "다음 배지 ${streakStats.nextMilestone}일까지 ${streakStats.daysToNextMilestone}일 남음",
                        color = Color(0xFF6E6A82),
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    val progress = (streakStats.currentStreak.toFloat() / streakStats.nextMilestone.toFloat())
                        .coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = Color(0xFF7A67F8),
                        trackColor = Color(0xFFD8D2FF)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StreakChip("최대 연속", "${streakStats.longestStreak}일", Modifier.weight(1f))
                StreakChip("활동일", "${streakStats.activeDays}/${streakStats.periodDays}", Modifier.weight(1f))
                StreakChip("지속률", "${streakStats.consistencyRate}%", Modifier.weight(1f))
            }

            Spacer(Modifier.height(10.dp))
            Text("스트릭 히스토리", color = Color(0xFF6E6A82), fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StreakChip("2일+", "${streakStats.twoDayStreakCount}회", Modifier.weight(1f))
                StreakChip("3일+", "${streakStats.threeDayStreakCount}회", Modifier.weight(1f))
                val longRunCount = (streakStats.longestStreak / 7).coerceAtLeast(0)
                StreakChip("7일+", "${longRunCount}회", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StreakChip(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F5FB))) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, color = Color(0xFF6E6A82), fontSize = 12.sp)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}

@Composable
private fun RecentCoursesSection(recentRuns: List<com.example.runnershigh.data.remote.dto.RecentActivity>) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("최근 러닝 코스 5개", fontWeight = FontWeight.Bold)
            recentRuns.forEach { run ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF6F5FB), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(run.date, fontWeight = FontWeight.SemiBold)
                        Text(String.format(Locale.getDefault(), "%.1fkm", run.distance), color = Color.Gray, fontSize = 12.sp)
                    }
                    Box(
                        modifier = Modifier.size(28.dp).background(Color(0xFFB8ABFF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("R", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
            if (recentRuns.isEmpty()) {
                Text("최근 코스 기록이 없습니다.", color = Color.Gray)
            }
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4F4))) {
        Text(
            message,
            color = Color(0xFFC62828),
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}
