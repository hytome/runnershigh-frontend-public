package com.example.runnershigh.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.HealthConnectClient
import com.example.runnershigh.data.health.HealthConnectManager
import com.example.runnershigh.data.remote.dto.HealthData
import com.example.runnershigh.data.remote.dto.HeartRateData
import com.example.runnershigh.ui.AuthViewModel
import com.example.runnershigh.ui.theme.RacingSansOne
import kotlinx.coroutines.launch
import androidx.health.connect.client.PermissionController
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    onGoogleLoginClick: () -> Unit,   // 구글 로그인
    viewModel: AuthViewModel

) {
    val uiState by viewModel.loginUiState.collectAsState()
    var localErrorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val healthManager = remember { HealthConnectManager(context) }
    var showHealthConsentDialog by remember { mutableStateOf(false) }
    var pendingLoginNavigation by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val timeFormatter = remember {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }

    fun formatInstant(instant: Instant): String =
        timeFormatter.withZone(ZoneId.systemDefault()).format(instant)

    suspend fun syncHealthDataIfAvailable() {
        val userUuid = viewModel.userUuid ?: return

        val measuredAt = LocalDateTime.now().format(timeFormatter)
        val steps = healthManager.getTodayStepCount()
        val calories = healthManager.getTodayCaloriesBurned().toDouble()
        val heartRateRecords = healthManager.readHeartRates()
        val heartRateSamples = heartRateRecords.flatMap { record ->
            record.samples.map { sample ->
                HeartRateData(
                    bpm = sample.beatsPerMinute.toDouble(),
                    time = formatInstant(sample.time)
                )
            }
        }
        val sleepDurationMinutes = healthManager.getLatestSleepDuration()
        val restingRates = healthManager.readRestingHeartRates()
        val avgRestingHeartRate = restingRates
            .map { it.beatsPerMinute.toDouble() }
            .takeIf { it.isNotEmpty() }
            ?.average()
        val hrvRecords = healthManager.readHrvRmssd()
        val avgHrv = hrvRecords
            .map { it.heartRateVariabilityMillis.toDouble() }
            .takeIf { it.isNotEmpty() }
            ?.average()

        viewModel.syncHealthData(
            HealthData(
                userId = userUuid,
                user_uuid = userUuid,
                heartRates = heartRateSamples,
                steps = steps,
                calories = calories,
                sleepDurationMinutes = sleepDurationMinutes,
                avgRestingHeartRate = avgRestingHeartRate,
                avgHRV = avgHrv,
                measuredAt = measuredAt
            )
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        coroutineScope.launch {
            val isGranted = granted.containsAll(healthManager.permissions)
            if (isGranted) {
                viewModel.syncHealthConnectConsent(consented = true)
            }
            if (pendingLoginNavigation) {
                pendingLoginNavigation = false
                if (isGranted) {
                    syncHealthDataIfAvailable()
                }
                onLoginSuccess()
                viewModel.consumeLoginCompletedFlag()
            }
        }
    }

    // 로그인 성공 시 네비게이션
    LaunchedEffect(uiState.loginCompleted) {
        if (uiState.loginCompleted) {
            if (uiState.healthConnectConsented == true) {
                coroutineScope.launch {
                    val needsPermission = when (healthManager.getSdkStatus()) {
                        HealthConnectClient.SDK_AVAILABLE -> {
                            val granted = healthManager.getGrantedPermissions()
                            !granted.containsAll(healthManager.permissions)
                        }
                        else -> false
                    }
                    if (needsPermission) {
                        pendingLoginNavigation = true
                        showHealthConsentDialog = true
                    } else {
                        coroutineScope.launch {
                            syncHealthDataIfAvailable()
                            onLoginSuccess()
                            viewModel.consumeLoginCompletedFlag()
                        }
                    }
                }
            } else {
                pendingLoginNavigation = true
                showHealthConsentDialog = true
            }
        }
    }

    fun handleLogin() {
        if (uiState.email.isBlank() || uiState.password.isBlank()) {
            localErrorMessage = "이메일과 비밀번호를 입력해주세요."
            return
        }
        localErrorMessage = null
        viewModel.login()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 상단 Runner's 로고
        Text(
            text = "Runner's",
            fontFamily = RacingSansOne,
            fontWeight = FontWeight.Normal,
            fontSize = 56.sp,
            color = Color(0xFF1E1E1E),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 24.dp)
        )

        // 하단 High. 로고
        Text(
            text = "High.",
            fontFamily = RacingSansOne,
            fontWeight = FontWeight.Normal,
            fontSize = 56.sp,
            color = Color(0xFF1E1E1E),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 32.dp, bottom = 32.dp)
        )

        // 뒤로가기 (화살표 대신 텍스트 아이콘으로)
        Text(
            text = "←",
            fontSize = 28.sp,
            color = Color(0xFF1E1E1E),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .clickable { onBack() }
        )

        // 가운데 로그인 폼
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(horizontal = 24.dp)
                .padding(top = 140.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Email
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Email",
                    fontSize = 16.sp,
                    color = Color(0xFF1E1E1E)
                )
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { viewModel.onLoginEmailChange(it) },
                    placeholder = { Text("") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // Password
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Password",
                    fontSize = 16.sp,
                    color = Color(0xFF1E1E1E)
                )
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.onLoginPasswordChange(it) },
                    placeholder = { Text("") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // 에러 메시지 (클라이언트 검증 + 서버/네트워크)
            localErrorMessage?.let { msg ->
                Text(
                    text = msg,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }
            uiState.errorMessage?.let { msg ->
                Text(
                    text = msg,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }

            // Login 버튼
            Button(
                onClick = { handleLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2C2C2C),
                    contentColor = Color(0xFFF5F5F5)
                ),
                shape = ButtonDefaults.shape
            ) {
                Text(
                    text = if (uiState.isLoading) "Loading..." else "Login",
                    fontSize = 16.sp
                )
            }

            // 비밀번호 찾기
            Text(
                text = "비밀번호 찾기.",
                fontSize = 16.sp,
                color = Color(0xFF1E1E1E),
                modifier = Modifier
                    .clickable { onForgotPasswordClick() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sign in 버튼 (회원가입)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { onSignUpClick() },
                    modifier = Modifier
                        .width(256.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2C2C2C),
                        contentColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Text(text = "Sign in", fontSize = 16.sp)
                }
            }

            // Google 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(
                    onClick = { onGoogleLoginClick() },
                    modifier = Modifier
                        .width(96.dp)
                        .height(48.dp),
                ) {
                    Text(text = "G", fontSize = 20.sp)
                }
            }
        }
    }

    if (showHealthConsentDialog) {
        AlertDialog(
            onDismissRequest = {
                showHealthConsentDialog = false
                if (pendingLoginNavigation) {
                    pendingLoginNavigation = false
                    onLoginSuccess()
                    viewModel.consumeLoginCompletedFlag()
                }
            },
            title = { Text(text = "헬스커넥트 동의") },
            text = { Text(text = "건강 정보 동의를 진행하시겠습니까? 동의 시 헬스커넥트 권한 요청이 표시됩니다.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showHealthConsentDialog = false
                        when (healthManager.getSdkStatus()) {
                            HealthConnectClient.SDK_AVAILABLE -> {
                                if (!healthManager.isReadHealthDataInBackgroundAvailable()) {
                                    localErrorMessage =
                                        "헬스커넥트 기능을 사용할 수 없습니다. 앱을 업데이트한 뒤 다시 시도해주세요."
                                    return@TextButton
                                }
                                coroutineScope.launch {
                                    val granted =
                                        healthManager.getGrantedPermissions()
                                    if (granted.containsAll(healthManager.permissions)) {
                                        Log.i("HealthConnect", "이미 권한이 부여되어 있습니다.")
                                        viewModel.syncHealthConnectConsent(consented = true)
                                        if (pendingLoginNavigation) {
                                            pendingLoginNavigation = false
                                            onLoginSuccess()
                                            viewModel.consumeLoginCompletedFlag()
                                        }
                                    } else {
                                        Log.i("HealthConnect", "권한 요청 화면을 띄웁니다.")
                                        permissionLauncher.launch(healthManager.permissions)
                                    }
                                }
                            }

                            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                                context.startActivity(healthManager.getHealthConnectUpdateIntent())
                            }

                            else -> {
                                localErrorMessage =
                                    "헬스커넥트 앱이 설치되어 있지 않습니다. 설치 후 다시 시도해주세요."
                            }
                        }
                    }
                ) {
                    Text(text = "동의")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showHealthConsentDialog = false
                        if (pendingLoginNavigation) {
                            pendingLoginNavigation = false
                            onLoginSuccess()
                            viewModel.consumeLoginCompletedFlag()
                        }
                    }
                ) {
                    Text(text = "다음에")
                }
            }
        )
    }
}
