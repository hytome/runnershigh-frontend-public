package com.example.runnershigh.ui.screen.course

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.runnershigh.data.remote.dto.RecentActivity
import com.example.runnershigh.ui.RunningViewModel
import com.example.runnershigh.ui.screen.course.components.CourseCard
import com.example.runnershigh.ui.screen.course.components.GuideCard
import com.example.runnershigh.ui.screen.course.components.RecentCourseItem
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunningCourseScreen(
    onBackClick: () -> Unit,
    onLocationCourseClick: () -> Unit,
    onPopularCourseClick: () -> Unit,
    onMyCourseClick: () -> Unit,
    runningViewModel: RunningViewModel,
    recentActivities: List<RecentActivity>,
    isLoading: Boolean,
    errorMessage: String?
) {
    val context = LocalContext.current
    val currentRegionLabel = runningViewModel.currentRegionLabel.collectAsState()
    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val fusedLocationClient = rememberFusedLocationClient(context)
    var hasAttemptedLocation by remember { mutableStateOf(false) }

    LaunchedEffect(hasLocationPermission, currentRegionLabel.value, hasAttemptedLocation) {
        if (!hasLocationPermission || !currentRegionLabel.value.isNullOrBlank() || hasAttemptedLocation) {
            return@LaunchedEffect
        }
        val location = fusedLocationClient.getLastLocationOrNull()
            ?: fusedLocationClient.getCurrentLocationOrNull()
        if (location != null) {
            runningViewModel.fetchCurrentRegionLabel(location.latitude, location.longitude)
            hasAttemptedLocation = true
        } else {
            hasAttemptedLocation = true
        }
    }

    val locationText = when {
        !hasLocationPermission -> "위치 권한이 필요합니다"
        !currentRegionLabel.value.isNullOrBlank() -> currentRegionLabel.value.orEmpty()
        !hasAttemptedLocation -> "위치 확인 중..."
        else -> "현재 위치를 불러올 수 없습니다"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = { Text("러닝 코스", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "현재 위치",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = locationText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }


        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CourseCard(
                icon = "navigation",
                title = "위치 기반 맞춤형 코스",
                description = "현재 위치에 기반한 최적화 코스를 추천합니다!",
                badges = listOf("#개인 맞춤형", "#실시간 분석"),
                color = Color(0xFFB8E6B8),
                onClick = onLocationCourseClick
            )

            CourseCard(
                icon = "trending",
                title = "인기 러닝 코스",
                description = "다른 러너들의 추천 코스를 골라보세요!",
                badges = listOf("#주간 TOP 100", "#후기"),
                color = Color(0xFFD4F1D4),
                onClick = onPopularCourseClick
            )

            CourseCard(
                icon = "map",
                title = "나만의 코스 관리",
                description = "코스 추가, 삭제, 선택하기",
                badges = listOf("#커스텀", "#공유 가능"),
                color = Color(0xFFE8F5E8),
                onClick = onMyCourseClick
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        GuideCard()

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "최근에 달린 코스",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when {
                isLoading -> {
                    Text(text = "최근 코스를 불러오는 중입니다...", color = Color.Gray)
                }

                errorMessage != null -> {
                    Text(text = errorMessage, color = Color(0xFFE53935))
                }

                recentActivities.isEmpty() -> {
                    Text(text = "최근에 달린 코스가 없습니다.", color = Color.Gray)
                }

                else -> {
                    recentActivities.forEachIndexed { index, activity ->
                        RecentCourseItem(
                            name = formatDateLabel(activity.date),
                            distance = String.format("%.1fkm", activity.distance),
                            type = "|   러닝 세션",
                            color = recentCourseColors[index % recentCourseColors.size]
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

private val recentCourseColors = listOf(
    Color(0xFFFFF4E6),
    Color(0xFFE8F5E8),
    Color(0xFFFFE6E6)
)

private fun formatDateLabel(date: String): String {
    if (date.isBlank()) return "최근 러닝"
    return try {
        val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val output = SimpleDateFormat("M월 d일", Locale.KOREA)
        val parsed = input.parse(date)
        if (parsed != null) {
            output.format(parsed)
        } else {
            date
        }
    } catch (e: Exception) {
        date
    }
}

@Composable
private fun rememberFusedLocationClient(context: android.content.Context): FusedLocationProviderClient {
    return androidx.compose.runtime.remember(context) {
        LocationServices.getFusedLocationProviderClient(context)
    }
}

private suspend fun FusedLocationProviderClient.getLastLocationOrNull(): Location? {
    return suspendCancellableCoroutine { continuation ->
        lastLocation
            .addOnSuccessListener { location ->
                continuation.resume(location)
            }
            .addOnFailureListener {
                continuation.resume(null)
            }
    }
}

private suspend fun FusedLocationProviderClient.getCurrentLocationOrNull(): Location? {
    return suspendCancellableCoroutine { continuation ->
        val cancellationTokenSource = CancellationTokenSource()
        getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                continuation.resume(location)
            }
            .addOnFailureListener {
                continuation.resume(null)
            }
    }
}
