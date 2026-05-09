package com.example.runnershigh.ui.screen.course

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.width
import com.example.runnershigh.ui.CourseSaveState
import com.example.runnershigh.ui.RunningViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterCourseScreen(
    date: String,
    location: String,
    distance: String,
    time: String,
    sessionId: String,
    onBackClick: () -> Unit,
    runningViewModel: RunningViewModel,
    userUuid: String,
    onRegisterSuccess: () -> Unit = {},
    currentLevelLabel: String? = null,
    currentLevelName: String? = null
) {
    val lastRun by runningViewModel.lastRunForCourse.collectAsState()
    val saveState by runningViewModel.courseSaveState.collectAsState()
    var courseName by remember { mutableStateOf(location.ifBlank { "" }) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        runningViewModel.resetCourseSaveState()
        errorMessage = null
    }

    LaunchedEffect(sessionId, lastRun) {
        if (lastRun == null && sessionId.isNotBlank()) {
            runningViewModel.loadRunForCourseFromSession(sessionId)
        }
    }

    LaunchedEffect(saveState) {
        when (saveState) {
            is CourseSaveState.Error -> errorMessage = (saveState as CourseSaveState.Error).message
            CourseSaveState.Idle -> errorMessage = null
            CourseSaveState.Loading -> errorMessage = null
            is CourseSaveState.Success -> {
                errorMessage = null
                onRegisterSuccess()
            }
        }
    }

    val displayDistance = lastRun?.stats?.distanceKm?.let { String.format("%.2f km", it) }
        ?: distance.ifBlank { "-" }
    val displayTime = lastRun?.stats?.durationSec?.let { formatDuration(it) }
        ?: time.ifBlank { "-" }

    val isSaving = saveState is CourseSaveState.Loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = { Text("나만의 코스 관리", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        if (!currentLevelLabel.isNullOrBlank() || !currentLevelName.isNullOrBlank()) {
            Surface(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF4A7C4E)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        currentLevelLabel?.let { label ->
                            Text(
                                text = label,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                        currentLevelName?.let { level ->
                            Text(
                                text = level,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        Surface(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFE8F5E8)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (date.isNotBlank()) "$date 금요일" else "날짜 정보 없음",
                        fontSize = 13.sp,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = location.ifBlank { "러닝 위치" },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "거리:",
                            fontSize = 13.sp,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            text = displayDistance,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Timer,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = displayTime,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "코스 제목",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = courseName,
            onValueChange = {
                if (it.length <= 30) {
                    courseName = it
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("코스 이름을 입력해주세요!", color = Color.Gray) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = Color(0xFF4A7C4E)
            ),
            trailingIcon = {
                Text(
                    text = "${'$'}{courseName.length}/30",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        )

        if (lastRun == null) {
            Text(
                text = "최근 러닝 기록을 불러올 수 없어 코스를 등록할 수 없습니다.",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        errorMessage?.let { message ->
            Text(
                text = message,
                color = Color(0xFFE53935),
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Button(
            onClick = {
                runningViewModel.saveCourseFromLastRun(userUuid, courseName)
            },
            enabled = !isSaving && userUuid.isNotBlank() && lastRun != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A7C4E),
                disabledContainerColor = Color(0xFFB0BEC5)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (isSaving) "등록 중..." else "코스 등록 완료",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

private fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val remain = seconds % 60
    return String.format("%d:%02d", minutes, remain)
}
