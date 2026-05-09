package com.example.runnershigh.ui.screen

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import java.util.Locale
import java.util.Calendar

import com.example.runnershigh.data.remote.ApiClient
import com.example.runnershigh.data.remote.dto.TodayPlanResponse
import com.example.runnershigh.data.remote.dto.UserIdRequest
import com.example.runnershigh.data.remote.dto.RunningCourseDto
import com.example.runnershigh.domain.model.RunningPlanGoal

import com.example.runnershigh.domain.model.RunningStats
import com.example.runnershigh.ui.RunningViewModel
import com.example.runnershigh.ui.theme.RacingSansOne
import com.example.runnershigh.ui.map.*
import com.example.runnershigh.ui.screen.mate.ActiveRunningMateReservation
import com.example.runnershigh.ui.screen.mate.MatchParticipationType
import com.example.runnershigh.ui.screen.mate.RunningMateMapScreen
import com.example.runnershigh.ui.screen.level.LevelActivity
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private enum class NavTab { LEVEL, RUNNING, START, COURSE, STATS }
private enum class ScreenState { NAVIGATION, COUNTDOWN, ACTIVE_RUNNING }


/**
 * 하단 네비게이션이 있는 메인 러닝 화면
 */
@Composable
fun RunningScreen(
    navController: NavController,
    runningViewModel: RunningViewModel,
    userUuid: String,
    onMenuClick: () -> Unit = {},
    onStatsClick: () -> Unit = {},
    // 러닝이 끝난 뒤 결과 화면으로 넘어갈 때 사용 (값 전달 X, "끝났다" 신호만)
    onRunningFinish: () -> Unit = {}
) {
    var activeTab by remember { mutableStateOf(NavTab.RUNNING) }
    var screenState by remember { mutableStateOf(ScreenState.NAVIGATION) }
    val context = LocalContext.current   // 🔹 Activity 실행에 사용할 Context

    var todayPlan by remember { mutableStateOf<TodayPlanResponse?>(null) }
    var todayPlanError by remember { mutableStateOf<String?>(null) }
    val planGoal by runningViewModel.planGoal.collectAsState(initial = RunningPlanGoal())
    val selectedCourse by runningViewModel.selectedCourse.collectAsState()
    val selectedCoursePath = remember(selectedCourse) {
        selectedCourse?.toLatLngPath().orEmpty()
    }
    var showRunningMateScreen by remember { mutableStateOf(false) }
    var activeMateReservation by remember { mutableStateOf<ActiveRunningMateReservation?>(null) }

    LaunchedEffect(userUuid) {
        if (userUuid.isBlank()) return@LaunchedEffect

        try {
            val response = withContext(Dispatchers.IO) {
                ApiClient.userService.getHomeDashboard(UserIdRequest(user_uuid = userUuid))
            }

            if (response.isSuccessful) {
                val dashboard = response.body()
                todayPlan = dashboard?.todayPlan
                todayPlan?.toRunningPlanGoal()?.let { goal ->
                    runningViewModel.applyPlanGoalFromPlan(goal)
                }
                todayPlanError = null
            } else {
                todayPlanError = "오늘의 플랜을 불러오지 못했어요 (${response.code()})"
            }
        } catch (e: Exception) {
            todayPlanError = "오늘의 플랜을 불러오는 중 오류가 발생했습니다."
            Log.e("RunningScreen", "Failed to fetch home dashboard", e)
        }
    }

    // Start 버튼 → 세션 시작 + 카운트다운
    val handleStartClick = handleStartClick@{
        if (userUuid.isBlank()) {
            Toast.makeText(
                context,
                "로그인이 필요합니다. 다시 시도해주세요.",
                Toast.LENGTH_SHORT
            ).show()
            return@handleStartClick
        }
        runningViewModel.startSession(userUuid)
        runningViewModel.startTracking()   // 위치 추적 상태 초기화 + ON
        screenState = ScreenState.COUNTDOWN
    }

    val handleCountdownComplete = {
        screenState = ScreenState.ACTIVE_RUNNING
    }

    fun handleStopOnly() {
        // 러닝은 중단하지만 결과 저장은 안 하는 경우
        runningViewModel.stopTracking()
        screenState = ScreenState.NAVIGATION
        activeTab = NavTab.RUNNING
    }

    // 러닝 완전 종료(정지 버튼 길게 눌렀을 때)
    fun handleFinish(stats: RunningStats) {
        // 1) ViewModel 에 결과 저장 + 서버로 전송 시도
        runningViewModel.finishSession(stats, userUuid)
        // 2) 위치 추적 종료
        runningViewModel.stopTracking()

        // 3) 상위에 알림 (NavGraph 에서 runningResult 로 네비게이션)
        onRunningFinish()

        // 4) Running 탭 화면 상태 초기화
        screenState = ScreenState.NAVIGATION
        activeTab = NavTab.RUNNING
    }

    // 상태별 분기
    when (screenState) {
        ScreenState.COUNTDOWN -> {
            CountdownScreen(onComplete = handleCountdownComplete)
            return
        }

        ScreenState.ACTIVE_RUNNING -> {
            ActiveRunningScreen(
                runningViewModel = runningViewModel,   // ViewModel 전달
                onStop = { handleStopOnly() },
                onMenuClick = onMenuClick,
                onFinish = { stats -> handleFinish(stats) },
                selectedCoursePath = selectedCoursePath
            )
            return
        }

        ScreenState.NAVIGATION -> Unit
    }

    if (showRunningMateScreen) {
        RunningMateMapScreen(
            onClose = { showRunningMateScreen = false },
            coursePath = selectedCoursePath,
            initialReservation = activeMateReservation,
            onReservationChanged = { activeMateReservation = it }
        )
        return
    }

    val shouldShowMateReminder = remember(activeMateReservation) {
        activeMateReservation?.let { reservation ->
            isReminderWindow(
                startHour = reservation.startHour,
                startMinute = reservation.startMinute
            )
        } == true
    }

    // ───── 기본 러닝 네비게이션 화면 ─────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // 상단 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Runner’s High",
                fontFamily = RacingSansOne,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                tint = Color.Black,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onMenuClick() }
            )
        }

        // 오늘의 플랜 + 지도 영역
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // 🔵 지도: Box 영역 전체를 채우도록 배경에 깔기
            RunningMapSection(
                modifier = Modifier
                    .matchParentSize(),
                coursePath = selectedCoursePath
            )

            // 🔵 오늘의 플랜 카드: 지도 위에 떠 있는 형태
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TodayPlanCard(
                    todayPlan = todayPlan,
                    errorMessage = todayPlanError,
                    planGoal = planGoal,
                    compact = shouldShowMateReminder,
                    modifier = Modifier.fillMaxWidth()
                )

                if (shouldShowMateReminder) {
                    activeMateReservation?.let { reservation ->
                        RunningMateReminderCard(
                            reservation = reservation,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            RunningMateButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                onClick = {
                    showRunningMateScreen = true
                }
            )
        }

        selectedCourse?.let { course ->
            SelectedCourseInfo(course = course)
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Color.Black)
        )

        // 하단 네비게이션
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            BottomNavItem(
                icon = Icons.Filled.EmojiEvents,
                label = "Level",
                selected = activeTab == NavTab.LEVEL,
                onClick = {
                    activeTab = NavTab.LEVEL
                    if (userUuid.isBlank()) {
                        Log.e("RunningScreen", "사용자 UUID가 비어 있어 LevelActivity를 실행할 수 없습니다.")
                        Toast.makeText(
                            context,
                            "로그인이 필요합니다. 다시 시도해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@BottomNavItem
                    }
                    // 🔥 Level 탭 클릭 시 배지/레벨 Activity 실행
                    context.startActivity(
                        Intent(context, LevelActivity::class.java).apply {
                            putExtra("userUuid", userUuid)
                        }
                    )
                }
            )

            BottomNavItem(
                icon = Icons.Filled.FavoriteBorder,
                label = "Running",
                selected = activeTab == NavTab.RUNNING,
                onClick = { activeTab = NavTab.RUNNING }
            )

            BottomNavItem(
                icon = Icons.Filled.PlayCircleOutline,
                label = "Start.",
                selected = activeTab == NavTab.START,
                onClick = {
                    activeTab = NavTab.START
                    handleStartClick()
                }
            )

            BottomNavItem(
                icon = Icons.Filled.Map,
                label = "Course",
                selected = activeTab == NavTab.COURSE,
                onClick = {
                    activeTab = NavTab.COURSE
                    navController.navigate("course/main") {
                        launchSingleTop = true
                    }
                }
            )

            BottomNavItem(
                icon = Icons.Filled.ShowChart,
                label = "Active",
                selected = activeTab == NavTab.STATS,
                onClick = {
                    activeTab = NavTab.STATS
                    // 🔥 실제 화면 전환은 NavGraph 쪽 onStatsClick 에서 처리
                    onStatsClick()
                }
            )
        }
    }
}

@Composable
private fun RunningMateButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        color = Color(0xFFE5F3CC),
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "함께 뛰고 싶어요.",
                fontFamily = RacingSansOne,
                fontSize = 20.sp,
                color = Color.Black
            )
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "러닝메이트",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun SelectedCourseInfo(course: RunningCourseDto) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFE8F5E8),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "선택한 코스", fontSize = 14.sp, color = Color(0xFF2E7D32))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = course.name.ifBlank { "이름 없는 코스" }, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = String.format(Locale.getDefault(), "%.2f km", course.distance))
                if (course.totalTime > 0) {
                    Text(text = formatCourseTime(course.totalTime))
                }
            }
        }
    }
}

private fun formatCourseTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}

private fun RunningCourseDto.toLatLngPath(): List<LatLng> {
    val path = if (pathData.isNotEmpty()) {
        pathData.map { LatLng(it.lat, it.lng) }
    } else {
        waypoints.map { LatLng(it.lat, it.lng) }
    }

    return path
        .filter { point ->
            !point.latitude.isNaN() && !point.longitude.isNaN()
        }
}

private fun TodayPlanResponse.toRunningPlanGoal(): RunningPlanGoal? {
    val distance = targetDistance ?: distance
    val pace = targetPaceSecPerKm

    if (distance == null && pace == null) return null

    return RunningPlanGoal(
        targetDistanceKm = distance ?: 0.0,
        targetPaceSecPerKm = pace,
        planTitle = title ?: "오늘의 플랜"
    )
}

@Composable
private fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val iconTint = if (selected) Color.Black else Color(0xFFCCCCCC)
    val textColor = if (selected) Color.Black else Color(0xFFCCCCCC)

    Column(
        modifier = Modifier
            .width(72.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = label,
            fontFamily = RacingSansOne,
            fontSize = 14.sp,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun TodayPlanCard(
    todayPlan: TodayPlanResponse?,
    errorMessage: String?,
    planGoal: RunningPlanGoal,
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    val subtitleText = todayPlan?.title
        ?: planGoal.planTitle
        ?: "오늘의 플랜"

    val rawDescription = todayPlan?.text
    val cleanedDescription = rawDescription
        ?.replace("(컨디션 반영: 오늘의 건강 데이터가 동기화되지 않았습니다.)", "")
        ?.replace("type:nomal", "")
        ?.replace("type:normal", "")
        ?.trim()

    val descriptionText = when {
        errorMessage != null -> errorMessage
        !cleanedDescription.isNullOrBlank() -> cleanedDescription
        else -> "오늘의 플랜을 불러오는 중이에요."
    }

    val baseDistanceText = todayPlan?.distance
        ?.takeIf { it > 0 }
        ?.let { String.format(Locale.getDefault(), "기본 거리 %.2f km", it) }

    val targetDistanceText = when {
        planGoal.targetDistanceKm > 0 -> planGoal.targetDistanceKm
        todayPlan?.targetDistance != null -> todayPlan.targetDistance
        else -> null
    }?.takeIf { it > 0 }
        ?.let { String.format(Locale.getDefault(), "목표 거리 %.2f km", it) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(if (compact) 126.dp else 150.dp),
        color = Color.White,
        shadowElevation = 6.dp,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ShowChart, // 임시 아이콘
                    contentDescription = "오늘의 플랜",
                    tint = Color.Black,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "오늘의 플랜",
                        fontFamily = RacingSansOne,
                        fontSize = if (compact) 18.sp else 20.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitleText,
                        fontFamily = RacingSansOne,
                        fontSize = if (compact) 16.sp else 18.sp,
                        color = Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.height(if (compact) 6.dp else 10.dp))

            Text(
                text = descriptionText,
                fontFamily = RacingSansOne,
                fontSize = if (compact) 14.sp else 16.sp,
                color = Color(0xFF333333),
                maxLines = if (compact) 2 else 3
            )
            Spacer(modifier = Modifier.height(if (compact) 8.dp else 12.dp))

            val tagTexts = listOfNotNull(baseDistanceText, targetDistanceText)
            if (!compact && tagTexts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    tagTexts.forEach { PlanTag(text = it) }
                }
            }
        }
    }
}

@Composable
private fun RunningMateReminderCard(
    reservation: ActiveRunningMateReservation,
    modifier: Modifier = Modifier
) {
    val participationLabel = if (reservation.participationType == MatchParticipationType.CREATED) {
        "내가 만든 매칭"
    } else {
        "참여 중인 매칭"
    }

    Surface(
        modifier = modifier,
        color = Color.White,
        shadowElevation = 6.dp,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.NotificationsActive,
                contentDescription = "러닝메이트 알림",
                tint = Color(0xFF0E8AD9),
                modifier = Modifier.size(26.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "러닝메이트 예약 알림",
                    fontFamily = RacingSansOne,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${formatKoreanTime(reservation.startHour, reservation.startMinute)} 시작 · 30분 전 알림",
                    fontFamily = RacingSansOne,
                    fontSize = 14.sp,
                    color = Color(0xFF4F566B)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Groups,
                        contentDescription = null,
                        tint = Color(0xFF0E8AD9),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "$participationLabel · ${reservation.currentMembers}/${reservation.maxMembers}명",
                        fontFamily = RacingSansOne,
                        fontSize = 13.sp,
                        color = Color(0xFF0E8AD9)
                    )
                }
            }
        }
    }
}

private fun isReminderWindow(startHour: Int, startMinute: Int): Boolean {
    val now = Calendar.getInstance()
    val nowMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
    val startMinutes = startHour * 60 + startMinute
    val minutesUntilStart = startMinutes - nowMinutes
    return minutesUntilStart in 0..30
}

private fun formatKoreanTime(hour: Int, minute: Int): String {
    val amPm = if (hour < 12) "오전" else "오후"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return "$amPm $displayHour:${String.format("%02d", minute)}"
}
@Composable
private fun PlanTag(text: String) {
    Box(
        modifier = Modifier
            .background(color = Color(0xFFE5F3CC), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontFamily = RacingSansOne,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}
