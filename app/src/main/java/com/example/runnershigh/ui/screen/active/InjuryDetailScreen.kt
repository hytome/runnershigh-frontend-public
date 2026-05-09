package com.example.runnershigh.ui.screen.active

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.runnershigh.R
import com.example.runnershigh.data.remote.ApiClient
import com.example.runnershigh.data.remote.dto.SubmittedFeedback
import com.example.runnershigh.data.remote.dto.UserIdRequest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource

private data class InjuryBubbleItem(
    val part: String,
    val count: Int,
    val anchorX: Dp,
    val anchorY: Dp,
    val tint: Color
)

private val requiredBodyParts = listOf("허리", "무릎", "발목", "종아리", "허벅지", "발바닥", "정강이")

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun InjuryDetailScreen(
    onBackClick: () -> Unit,
    userUuid: String,
    viewModel: ActivityViewModel = viewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value
    var selectedPeriod by remember { mutableStateOf(PeriodType.ALL) }
    val feedbackCache = remember { mutableStateListOf<SubmittedFeedback>() }
    val backendCounts = remember { mutableStateMapOf<String, Int>() }
    var backendAdvice by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userUuid) {
        if (userUuid.isNotBlank()) {
            viewModel.loadInjuryDetailData(userUuid)
        }
    }

    LaunchedEffect(userUuid, uiState.allActivities) {
        if (userUuid.isBlank()) return@LaunchedEffect

        runCatching {
            ApiClient.userService.getUserCondition(UserIdRequest(user_uuid = userUuid))
        }.onSuccess { response ->
            if (response.isSuccessful) {
                val body = response.body()
                val mapped = body?.injuryStats
                    ?.mapNotNull { stat ->
                        val key = listOf(stat.part, stat.type, stat.painPart)
                            .firstOrNull { !it.isNullOrBlank() }
                            ?.trim()
                            .orEmpty()
                        val count = stat.count ?: 0
                        key.takeIf { it.isNotBlank() }?.let { it to count }
                    }
                    .orEmpty()
                    .toMap()

                backendCounts.clear()
                backendCounts.putAll(mapped)
                backendAdvice = listOf(
                    body?.injuryFeedback,
                    body?.recommendationText,
                    body?.todayStatus
                ).firstOrNull { !it.isNullOrBlank() }?.trim()
            }
        }

        val shouldLoadFeedbackDetails = backendCounts.isEmpty()
        if (!shouldLoadFeedbackDetails) {
            feedbackCache.clear()
            return@LaunchedEffect
        }

        val sessions = uiState.allActivities
            .map { it.sessionId to it.date }
            .filter { it.first.isNotBlank() }
            .distinctBy { it.first }
            .take(120)

        val loaded = mutableListOf<SubmittedFeedback>()

        sessions.forEach { (sessionId, fallbackDate) ->
            runCatching { ApiClient.runningApi.getSubmittedFeedback(userUuid, sessionId) }
                .onSuccess { response ->
                    if (response.isSuccessful) {
                        response.body()?.let { feedback ->
                            loaded += if (feedback.createdAt.isBlank()) {
                                feedback.copy(createdAt = fallbackDate)
                            } else {
                                feedback
                            }
                        }
                    }
                }
        }

        feedbackCache.clear()
        feedbackCache.addAll(loaded)
    }

    val validParts = remember(feedbackCache.size, backendCounts.toMap()) {
        resolveQueryableInjuryParts(
            feedbacks = feedbackCache,
            backendCounts = backendCounts.toMap()
        )
    }

    val feedbackCounts = remember(feedbackCache, selectedPeriod) {
        aggregateInjuryCounts(
            feedbacks = feedbackCache,
            periodType = selectedPeriod,
            validParts = validParts
        )
    }

    val selectedCounts = remember(feedbackCounts, backendCounts.toMap(), selectedPeriod) {
        val filteredBackend = backendCounts.toMap().filterKeys { it in validParts }
        if (selectedPeriod == PeriodType.ALL && feedbackCounts.isEmpty()) filteredBackend else feedbackCounts
    }

    val knownParts = remember(selectedCounts, validParts) {
        val backendExtras = validParts.filterNot { it in requiredBodyParts }
            .sortedByDescending { selectedCounts[it] ?: 0 }
        requiredBodyParts + backendExtras
    }

    val totalReports = selectedCounts.values.sum()
    val bubbleItems = remember(selectedCounts, knownParts) {
        mapBubbleItems(selectedCounts = selectedCounts, knownParts = knownParts)
    }
    val advice = remember(backendAdvice, selectedPeriod) {
        backendAdvice?.takeIf { it.isNotBlank() }
            ?: "${selectedPeriod.toKoreanLabel()} 부상 분석 텍스트를 서버에서 아직 받지 못했습니다."
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("부상 통계") },
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
            InjuryPeriodTabs(
                selectedPeriod = selectedPeriod,
                onSelect = { selectedPeriod = it }
            )

            InjurySummaryCard(
                totalReports = totalReports,
                period = selectedPeriod,
                partCount = selectedCounts.count { it.value > 0 }
            )

            InjuryBodyDistributionCard(
                items = bubbleItems,
                allPartCounts = selectedCounts,
                knownParts = knownParts
            )

            InjuryAdviceCard(
                advice = advice,
                title = "AI 부상 조언"
            )
        }
    }
}

@Composable
private fun InjuryPeriodTabs(
    selectedPeriod: PeriodType,
    onSelect: (PeriodType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        listOf(PeriodType.MONTH to "월", PeriodType.YEAR to "년", PeriodType.ALL to "전체").forEach { (type, label) ->
            val selected = selectedPeriod == type
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .background(
                        color = if (selected) Color(0xFF111827) else Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onSelect(type) }
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Text(
                    text = label,
                    modifier = Modifier,
                    color = if (selected) Color.White else Color(0xFF6B7280),
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun InjurySummaryCard(
    totalReports: Int,
    period: PeriodType,
    partCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.MonitorHeart,
                contentDescription = null,
                tint = Color(0xFF0E8AD9),
                modifier = Modifier.size(26.dp)
            )
            Column {
                Text(
                    text = "${period.toKoreanLabel()} 기준 총 ${totalReports}회",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    fontSize = 16.sp
                )
                Text(
                    text = "통증 호소 부위 ${partCount}개 · 피드백/컨디션 데이터 기반",
                    color = Color(0xFF6B7280),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun InjuryBodyDistributionCard(
    items: List<InjuryBubbleItem>,
    allPartCounts: Map<String, Int>,
    knownParts: List<String>
) {
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(items) {
        startAnimation = false
        startAnimation = true
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "신체 부위별 부상 분포",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827),
                fontSize = 16.sp
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(330.dp)
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.injury_runner_silhouette),
                    contentDescription = "러너 신체 아이콘",
                    modifier = Modifier.size(180.dp).alpha(0.78f)
                )

                items.forEachIndexed { index, item ->
                    val progress by animateFloatAsState(
                        targetValue = if (startAnimation) 1f else 0f,
                        animationSpec = tween(
                            durationMillis = 550,
                            delayMillis = index * 70,
                            easing = FastOutSlowInEasing
                        ),
                        label = "bubbleAnim"
                    )

                    InjuryBubbleBadge(
                        item = item,
                        progress = progress
                    )
                }
            }

            val activePartText = knownParts
                .map { part -> "$part ${allPartCounts[part] ?: 0}회" }
                .joinToString(" · ")
                .ifBlank { "선택한 기간의 부상 데이터가 없습니다." }

            Text(
                text = activePartText,
                color = Color(0xFF6B7280),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun InjuryBubbleBadge(
    item: InjuryBubbleItem,
    progress: Float
) {
    Card(
        modifier = Modifier
            .offset(x = item.anchorX * progress, y = item.anchorY * progress)
            .scale(0.75f + (0.25f * progress)),
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = item.tint)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = item.part,
                color = Color(0xFF111827),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
            Text(
                text = "${item.count}회",
                color = Color(0xFF111827),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun InjuryAdviceCard(
    title: String,
    advice: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFF4F46E5)
                )
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF111827))
            }

            Text(
                text = advice,
                color = Color(0xFF374151),
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }
    }
}

private fun mapBubbleItems(
    selectedCounts: Map<String, Int>,
    knownParts: List<String>
): List<InjuryBubbleItem> {
    val colors = listOf(
        Color(0xFFE8F2FF),
        Color(0xFFEAF7EA),
        Color(0xFFFFF4E5),
        Color(0xFFF6ECFF),
        Color(0xFFFFECEC),
        Color(0xFFE7F8F9),
        Color(0xFFEDEFFB),
        Color(0xFFF5F5F5)
    )

    val predefined = mapOf(
        "어깨" to (0.dp to (-126).dp),
        "허리" to ((-130).dp to 22.dp),
        "무릎" to (76.dp to 76.dp),
        "발목" to ((-86).dp to 124.dp),
        "종아리" to ((-110).dp to 72.dp),
        "허벅지" to (98.dp to 26.dp),
        "발바닥" to ((-30).dp to 146.dp),
        "정강이" to (42.dp to 132.dp)
    )

    val sortedParts = knownParts
        .map { it to (selectedCounts[it] ?: 0) }
        .take(8)

    return sortedParts.mapIndexed { index, (part, count) ->
        val fallback = when (index) {
            0 -> 0.dp to (-128).dp
            1 -> (-118).dp to (-34).dp
            2 -> 120.dp to (-24).dp
            3 -> (-106).dp to 66.dp
            4 -> 110.dp to 68.dp
            5 -> (-82).dp to 126.dp
            6 -> 72.dp to 128.dp
            else -> 0.dp to 150.dp
        }

        val (x, y) = predefined[part] ?: fallback
        InjuryBubbleItem(
            part = part,
            count = count,
            anchorX = x,
            anchorY = y,
            tint = colors[index % colors.size]
        )
    }
}

private fun PeriodType.toKoreanLabel(): String = when (this) {
    PeriodType.MONTH -> "이번 달"
    PeriodType.YEAR -> "올해"
    PeriodType.ALL -> "전체"
}

private fun aggregateInjuryCounts(
    feedbacks: List<SubmittedFeedback>,
    periodType: PeriodType,
    validParts: Set<String>
): Map<String, Int> {
    if (feedbacks.isEmpty() || validParts.isEmpty()) return emptyMap()

    val now = Calendar.getInstance()
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun matchesPeriod(createdAt: String): Boolean {
        if (periodType == PeriodType.ALL) return true
        if (createdAt.isBlank()) return false

        val targetDate = runCatching {
            val dateOnly = createdAt.take(10)
            sdf.parse(dateOnly)
        }.getOrNull() ?: return false

        val cal = Calendar.getInstance().apply { time = targetDate }
        return when (periodType) {
            PeriodType.MONTH -> cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                cal.get(Calendar.MONTH) == now.get(Calendar.MONTH)
            PeriodType.YEAR -> cal.get(Calendar.YEAR) == now.get(Calendar.YEAR)
            PeriodType.ALL -> true
        }
    }

    val counts = linkedMapOf<String, Int>()
    feedbacks
        .filter { matchesPeriod(it.createdAt) }
        .forEach { feedback ->
            feedback.injuryParts.forEach { rawPart ->
                val part = rawPart.trim()
                if (part.isNotBlank() && part in validParts) {
                    counts[part] = (counts[part] ?: 0) + 1
                }
            }
        }

    return counts
}

private fun resolveQueryableInjuryParts(
    feedbacks: List<SubmittedFeedback>,
    backendCounts: Map<String, Int>
): Set<String> {
    val fromFeedback = feedbacks
        .flatMap { it.injuryParts }
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .toSet()
    val fromBackend = backendCounts.keys
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .toSet()
    return requiredBodyParts.toSet() + fromFeedback + fromBackend
}
