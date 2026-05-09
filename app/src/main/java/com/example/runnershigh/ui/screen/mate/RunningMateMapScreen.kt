package com.example.runnershigh.ui.screen.mate

import android.widget.EditText
import android.widget.NumberPicker
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PersonPinCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.example.runnershigh.ui.map.RunningMapSection
import com.example.runnershigh.ui.theme.RacingSansOne
import com.naver.maps.geometry.LatLng

private data class RunnerPresenceChip(
    val label: String,
    val alignX: Float,
    val alignY: Float
)

private data class MatchingConfigCard(
    val iconLabel: String,
    val title: String,
    val description: String,
    val value: String,
    val cardColor: Color,
    val textColor: Color
)

private enum class RunningMateEntryMode { CREATE, FIND }
enum class MatchParticipationType { CREATED, JOINED }

private data class RunningMatchPost(
    val title: String,
    val summary: String,
    val meta: String,
    val accentColor: Color,
    val currentMembers: Int,
    val maxMembers: Int,
    val startHour: Int,
    val startMinute: Int
)

data class ActiveRunningMateReservation(
    val title: String,
    val participationType: MatchParticipationType,
    val currentMembers: Int,
    val maxMembers: Int,
    val startHour: Int,
    val startMinute: Int
)


@Composable
fun RunningMateMapScreen(
    onClose: () -> Unit,
    coursePath: List<LatLng>,
    initialReservation: ActiveRunningMateReservation? = null,
    onReservationChanged: (ActiveRunningMateReservation) -> Unit = {}
) {
    var showMatchingSetupScreen by rememberSaveable { mutableStateOf(false) }
    var entryMode by rememberSaveable { mutableStateOf(RunningMateEntryMode.CREATE) }
    var activeReservation by remember(initialReservation) { mutableStateOf(initialReservation) }

    if (showMatchingSetupScreen) {
        RunningMateMatchingSetupScreen(
            mode = entryMode,
            onClose = onClose,
            onBack = { showMatchingSetupScreen = false },
            onMatchConfirmed = { reservation ->
                activeReservation = reservation
                onReservationChanged(reservation)
                showMatchingSetupScreen = false
            },
            coursePath = coursePath
        )
        return
    }

    val paletteBackground = Color(0xFFF7F3E6)
    val accentYellow = Color(0xFFFFD84D)
    val deepNavy = Color(0xFF1F2337)
    val chipBlue = Color(0xFF1780E6)

    val activeRunnerChips = remember {
        listOf(
            RunnerPresenceChip("24명", -0.72f, -0.34f),
            RunnerPresenceChip("8명", -0.12f, -0.54f),
            RunnerPresenceChip("15명", 0.56f, -0.20f),
            RunnerPresenceChip("6명", -0.46f, 0.06f),
            RunnerPresenceChip("11명", 0.18f, 0.22f),
            RunnerPresenceChip("4명", 0.72f, 0.44f)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(paletteBackground)
    ) {
        RunningMapSection(
            modifier = Modifier.matchParentSize(),
            coursePath = coursePath
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Text(
                text = "running mate",
                fontFamily = RacingSansOne,
                fontSize = 30.sp,
                color = deepNavy
            )

            Spacer(modifier = Modifier.size(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MateActionButton(
                    modifier = Modifier.weight(1f),
                    text = "러닝 매칭 만들기",
                    icon = Icons.Filled.AddCircleOutline,
                    backgroundColor = accentYellow,
                    contentColor = deepNavy,
                    onClick = {
                        entryMode = RunningMateEntryMode.CREATE
                        showMatchingSetupScreen = true
                    }
                )
                MateActionButton(
                    modifier = Modifier.weight(1f),
                    text = "매칭 찾기 시작",
                    icon = Icons.Filled.Search,
                    backgroundColor = Color.White,
                    contentColor = deepNavy,
                    onClick = {
                        entryMode = RunningMateEntryMode.FIND
                        showMatchingSetupScreen = true
                    }
                )
            }

            activeReservation?.let { reservation ->
                Spacer(modifier = Modifier.height(10.dp))
                ActiveReservationCard(reservation = reservation)
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 24.dp, end = 18.dp)
                .clickable { onClose() },
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 8.dp
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "닫기",
                tint = Color(0xFFE83838),
                modifier = Modifier.padding(10.dp)
            )
        }

        activeRunnerChips.forEach { chip ->
            RunnerPresenceBadge(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = (chip.alignX * 150).dp, y = (chip.alignY * 260).dp),
                label = chip.label,
                iconTint = chipBlue
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 18.dp),
            color = Color.White.copy(alpha = 0.9f),
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 10.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Groups,
                    contentDescription = null,
                    tint = chipBlue
                )
                Text(
                    text = "현재 주변 러너 68명 활동 중",
                    color = deepNavy,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun RunningMateMatchingSetupScreen(
    mode: RunningMateEntryMode,
    onClose: () -> Unit,
    onBack: () -> Unit,
    onMatchConfirmed: (ActiveRunningMateReservation) -> Unit,
    coursePath: List<LatLng>
) {
    var selectedCardIndex by remember { mutableIntStateOf(0) }
    var startLocation by remember { mutableStateOf<LatLng?>(null) }
    var pendingStartLocation by remember { mutableStateOf<LatLng?>(null) }
    var isStartLocationPickerMode by remember { mutableStateOf(false) }
    var locationSearchQuery by remember { mutableStateOf("") }

    var distanceKm by remember { mutableIntStateOf(5) }
    var runnerCount by remember { mutableIntStateOf(2) }
    var showDistancePicker by remember { mutableStateOf(false) }
    var showRunnerCountPicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val levelModes = listOf("동일 레벨을 원해요", "멘토를 원해요", "제가 멘토가 되고싶어요")
    var levelModeIndex by remember { mutableIntStateOf(0) }
    var timeHour by remember { mutableIntStateOf(6) }
    var timeMinute by remember { mutableIntStateOf(0) }
    var showFindResults by remember { mutableStateOf(false) }

    val cards = listOf(
        MatchingConfigCard("S", "Start Location", "만날 출발 지점을 선택하세요", if (startLocation == null) "현재 위치" else "지도에서 지정됨", Color(0xFFE4E9F2), Color(0xFF111318)),
        MatchingConfigCard("D", "Running Distance", "원하는 러닝 거리를 고르세요", "$distanceKm km", Color(0xFFFFC80A), Color(0xFF1B1500)),
        MatchingConfigCard("L", "Runner Level Range", levelModes[levelModeIndex], "", Color(0xFF0E8AD9), Color.White),
        MatchingConfigCard("M", "Runner Count", "함께 뛸 인원 수를 정하세요", "$runnerCount 명", Color(0xFF7D5CFA), Color.White),
        MatchingConfigCard("T", "Time Slot", "원하는 시간대", String.format("%02d:%02d", timeHour, timeMinute), Color(0xFF1F2337), Color.White)
    )

    val runningMatchPosts = remember(distanceKm, runnerCount, levelModeIndex, timeHour, timeMinute, startLocation) {
        listOf(
            RunningMatchPost(
                title = "한강 야간 러닝 ${distanceKm}km",
                summary = "출발 ${if (startLocation == null) "현재 위치 기준" else "지도 지정 위치 기준"} · ${runnerCount}명 모집 중",
                meta = "${String.format("%02d:%02d", timeHour, timeMinute)} 출발 · ${levelModes[levelModeIndex]}",
                accentColor = Color(0xFF1F2337),
                currentMembers = (runnerCount - 1).coerceAtLeast(1),
                maxMembers = runnerCount,
                startHour = timeHour,
                startMinute = timeMinute
            ),
            RunningMatchPost(
                title = "도심 템포런 메이트",
                summary = "중급 러너 환영 · ${distanceKm + 2}km 페이스 러닝",
                meta = "모집 인원 ${runnerCount}명 · 출발 ${String.format("%02d:%02d", timeHour, timeMinute)}",
                accentColor = Color(0xFF0E8AD9),
                currentMembers = runnerCount.coerceAtLeast(2),
                maxMembers = (runnerCount + 2).coerceAtMost(10),
                startHour = timeHour,
                startMinute = timeMinute
            ),
            RunningMatchPost(
                title = "주말 회복 조깅 크루",
                summary = "가볍게 달릴 메이트 모집 게시글",
                meta = "${levelModes[levelModeIndex]} · 함께 ${runnerCount}명",
                accentColor = Color(0xFF7D5CFA),
                currentMembers = (runnerCount / 2).coerceAtLeast(1),
                maxMembers = runnerCount.coerceAtLeast(2),
                startHour = timeHour,
                startMinute = timeMinute
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        RunningMapSection(
            modifier = Modifier.matchParentSize(),
            coursePath = coursePath,
            selectedLocation = if (isStartLocationPickerMode) pendingStartLocation else startLocation,
            onMapTap = { tapped ->
                if (isStartLocationPickerMode) {
                    pendingStartLocation = tapped
                }
            }
        )

        Text(
            text = "Runner’s High",
            fontFamily = RacingSansOne,
            fontSize = 30.sp,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp, top = 26.dp)
        )

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 20.dp, end = 16.dp)
                .clickable { onClose() },
            color = Color.White.copy(alpha = 0.95f),
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 8.dp
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "닫기",
                tint = Color(0xFFE83838),
                modifier = Modifier.padding(10.dp)
            )
        }


        if (showFindResults) {
            RunningMateResultsOverlay(
                posts = runningMatchPosts,
                onBack = { showFindResults = false },
                onJoinPost = { joinedPost ->
                    onMatchConfirmed(
                        ActiveRunningMateReservation(
                            title = joinedPost.title,
                            participationType = MatchParticipationType.JOINED,
                            currentMembers = (joinedPost.currentMembers + 1).coerceAtMost(joinedPost.maxMembers),
                            maxMembers = joinedPost.maxMembers,
                            startHour = joinedPost.startHour,
                            startMinute = joinedPost.startMinute
                        )
                    )
                }
            )
            return@Box
        }

        if (isStartLocationPickerMode) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 10.dp)
            ) {
                TextField(
                    value = locationSearchQuery,
                    onValueChange = { locationSearchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("도로명 주소로 검색") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "주소 검색",
                            tint = Color(0xFF1F2337)
                        )
                    }
                )
            }

            Button(
                onClick = {
                    pendingStartLocation?.let {
                        startLocation = it
                        isStartLocationPickerMode = false
                    }
                },
                enabled = pendingStartLocation != null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111318))
            ) {
                Text("시작 위치 확정", color = Color.White)
            }

            return@Box
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy((-8).dp)
        ) {
            cards.forEachIndexed { index, card ->
                RunningMateConfigCard(
                    card = card,
                    isSelected = selectedCardIndex == index,
                    onClick = {
                        selectedCardIndex = index
                        when (index) {
                            0 -> {
                                pendingStartLocation = startLocation
                                isStartLocationPickerMode = true
                            }
                            1 -> showDistancePicker = true
                            3 -> showRunnerCountPicker = true
                            4 -> showTimePicker = true
                        }
                    },
                    modifier = if (index == 2) {
                        Modifier.pointerInput(levelModeIndex) {
                            detectHorizontalDragGestures(
                                onHorizontalDrag = { _, dragAmount ->
                                    if (dragAmount > 20) {
                                        levelModeIndex = (levelModeIndex - 1).coerceAtLeast(0)
                                    } else if (dragAmount < -20) {
                                        levelModeIndex = (levelModeIndex + 1).coerceAtMost(levelModes.lastIndex)
                                    }
                                }
                            )
                        }
                    } else Modifier
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (mode == RunningMateEntryMode.CREATE) {
                        onMatchConfirmed(
                            ActiveRunningMateReservation(
                                title = "내가 만든 ${distanceKm}km 러닝 매칭",
                                participationType = MatchParticipationType.CREATED,
                                currentMembers = 1,
                                maxMembers = runnerCount,
                                startHour = timeHour,
                                startMinute = timeMinute
                            )
                        )
                    } else {
                        showFindResults = true
                    }
                },
                color = Color(0xFF111318),
                shape = RoundedCornerShape(18.dp),
                shadowElevation = 10.dp
            ) {
                Text(
                    text = if (mode == RunningMateEntryMode.CREATE) "러닝 매칭 만들기" else "러닝 매칭 찾기",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 16.dp),
                    textAlign = TextAlign.Center
                )
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp)
                    .clickable { onBack() },
                color = Color.Black.copy(alpha = 0.45f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "이전 화면으로",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }

    if (showDistancePicker) {
        KmPickerDialog(
            title = "목표 거리 설정",
            range = 1..43,
            initialValue = distanceKm,
            panelColor = Color(0xFFFFC80A),
            onDismiss = { showDistancePicker = false },
            onConfirm = {
                distanceKm = it
                showDistancePicker = false
            }
        )
    }

    if (showRunnerCountPicker) {
        KmPickerDialog(
            title = "러닝 인원 수 설정",
            range = 1..10,
            initialValue = runnerCount,
            panelColor = Color(0xFF7D5CFA),
            onDismiss = { showRunnerCountPicker = false },
            onConfirm = {
                runnerCount = it
                showRunnerCountPicker = false
            }
        )
    }

    if (showTimePicker) {
        TimeWheelPickerDialog(
            panelColor = Color(0xFF1F2337),
            initialHour = timeHour,
            initialMinute = timeMinute,
            onDismiss = { showTimePicker = false },
            onConfirm = { hour, minute ->
                timeHour = hour
                timeMinute = minute
                showTimePicker = false
            }
        )
    }

}

@Composable
private fun RunningMateResultsOverlay(
    posts: List<RunningMatchPost>,
    onBack: () -> Unit,
    onJoinPost: (RunningMatchPost) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FC).copy(alpha = 0.96f))
            .padding(top = 112.dp, start = 16.dp, end = 16.dp, bottom = 20.dp)
    ) {
        Text(
            text = "현재 모집 중인 러닝",
            fontFamily = RacingSansOne,
            fontSize = 28.sp,
            color = Color(0xFF1F2337)
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(posts) { post ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(22.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Surface(
                            color = post.accentColor.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "모집중",
                                color = post.accentColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = post.title, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF111318))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = post.summary, color = Color(0xFF4F566B), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = post.meta, color = post.accentColor, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onJoinPost(post) },
                            colors = ButtonDefaults.buttonColors(containerColor = post.accentColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("이 매칭 참여하기", color = Color.White)
                        }
                    }
                }
            }
        }
        Button(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .navigationBarsPadding(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111318))
        ) {
            Text("조건 다시 설정", color = Color.White)
        }
    }
}

@Composable
private fun ActiveReservationCard(reservation: ActiveRunningMateReservation) {
    val participationText = if (reservation.participationType == MatchParticipationType.CREATED) {
        "내가 만든 매칭"
    } else {
        "참여 중인 매칭"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xEEFFFFFF)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = participationText,
                color = Color(0xFF1F2337),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(
                text = reservation.title,
                color = Color(0xFF111318),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "현재 ${reservation.currentMembers} / ${reservation.maxMembers}명 모였어요",
                color = Color(0xFF0E8AD9),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${formatKoreanTime(reservation.startHour, reservation.startMinute)} 러닝 메이트 예약 · 30분 전 알림",
                color = Color(0xFF4F566B),
                fontSize = 13.sp
            )
        }
    }
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

private fun styleTimeNumberPicker(numberPicker: NumberPicker) {
    val textColor = android.graphics.Color.WHITE

    for (i in 0 until numberPicker.childCount) {
        val child = numberPicker.getChildAt(i)
        if (child is EditText) {
            child.setTextColor(textColor)
            child.textSize = 24f
        }
    }

    runCatching {
        val selectorWheelPaintField = NumberPicker::class.java.getDeclaredField("mSelectorWheelPaint")
        selectorWheelPaintField.isAccessible = true
        val paint = selectorWheelPaintField.get(numberPicker) as android.graphics.Paint
        paint.color = textColor
        paint.alpha = 255
        paint.textSize = 48f
    }

    runCatching {
        val inputTextField = NumberPicker::class.java.getDeclaredField("mInputText")
        inputTextField.isAccessible = true
        val inputText = inputTextField.get(numberPicker) as? EditText
        inputText?.setTextColor(textColor)
    }

    numberPicker.invalidate()
    numberPicker.requestLayout()
}

@Composable
private fun TimeWheelPickerDialog(
    panelColor: Color,
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var pickedHour by remember { mutableIntStateOf(initialHour.coerceIn(0, 23)) }
    var pickedMinute by remember { mutableIntStateOf((initialMinute / 5) * 5) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = panelColor,
        title = {
            Text(
                text = "원하는 시간대 설정",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AndroidView(
                    factory = { context ->
                        NumberPicker(context).apply {
                            minValue = 0
                            maxValue = 23
                            value = pickedHour
                            wrapSelectorWheel = true
                            setOnValueChangedListener { _, _, newVal -> pickedHour = newVal }
                            styleTimeNumberPicker(this)
                        }
                    },
                    update = { picker ->
                        picker.minValue = 0
                        picker.maxValue = 23
                        if (picker.value != pickedHour) picker.value = pickedHour
                        styleTimeNumberPicker(picker)
                    },
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = ":",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                AndroidView(
                    factory = { context ->
                        NumberPicker(context).apply {
                            minValue = 0
                            maxValue = 11
                            displayedValues = Array(12) { index -> String.format("%02d", index * 5) }
                            value = (pickedMinute / 5).coerceIn(0, 11)
                            wrapSelectorWheel = true
                            setOnValueChangedListener { _, _, newVal -> pickedMinute = newVal * 5 }
                            styleTimeNumberPicker(this)
                        }
                    },
                    update = { picker ->
                        picker.minValue = 0
                        picker.maxValue = 11
                        val minuteIndex = (pickedMinute / 5).coerceIn(0, 11)
                        if (picker.value != minuteIndex) picker.value = minuteIndex
                        styleTimeNumberPicker(picker)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(pickedHour, pickedMinute) }) {
                Text("적용", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", color = Color.White)
            }
        }
    )
}

@Composable
private fun KmPickerDialog(
    title: String,
    range: IntRange,
    initialValue: Int,
    panelColor: Color,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var selected by remember { mutableIntStateOf(initialValue.coerceIn(range.first, range.last)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = panelColor,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        text = {
            AndroidView(
                factory = { context ->
                    NumberPicker(context).apply {
                        minValue = range.first
                        maxValue = range.last
                        value = selected
                        wrapSelectorWheel = false
                        setOnValueChangedListener { _, _, newVal -> selected = newVal }
                    }
                },
                update = { picker ->
                    picker.minValue = range.first
                    picker.maxValue = range.last
                    if (picker.value != selected) picker.value = selected
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selected) }) {
                Text("적용", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", color = Color.Black)
            }
        }
    )
}

@Composable
private fun RunningMateConfigCard(
    card: MatchingConfigCard,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val focused by interactionSource.collectIsFocusedAsState()
    val lifted = isSelected || pressed || focused

    val scale by animateFloatAsState(
        targetValue = if (lifted) 1.07f else 1f,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "cardScale"
    )
    val offsetY by animateDpAsState(
        targetValue = if (lifted) (-8).dp else 0.dp,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "cardOffsetY"
    )
    val elevation by animateDpAsState(
        targetValue = if (lifted) 12.dp else 6.dp,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "cardElevation"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(92.dp)
            .zIndex(if (lifted) 1f else 0f)
            .offset(y = offsetY)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        color = card.cardColor,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = elevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = card.textColor.copy(alpha = 0.16f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = card.iconLabel,
                        color = card.textColor,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.title,
                    color = card.textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                Text(
                    text = card.description,
                    color = card.textColor.copy(alpha = 0.82f),
                    fontSize = 13.sp
                )
            }

            Text(
                text = card.value,
                color = card.textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun MateActionButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = backgroundColor,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = text,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun RunnerPresenceBadge(
    modifier: Modifier = Modifier,
    label: String,
    iconTint: Color
) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.PersonPinCircle,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = label,
                color = Color(0xFF293040),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
