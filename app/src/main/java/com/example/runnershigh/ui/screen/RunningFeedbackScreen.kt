package com.example.runnershigh.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.runnershigh.domain.model.RunningFeedback
import com.example.runnershigh.data.remote.dto.SubmittedFeedback
import com.example.runnershigh.ui.theme.RacingSansOne

// 서버로 보낼 값들을 한 번에 모으는 데이터 클래스


/**
 * 러닝 피드백 화면
 *
 * @param onBack    상단 ← 버튼 눌렀을 때
 * @param onSubmit  "피드백 제출하기" 버튼 눌렀을 때 최종 값 전달
 */
@Composable
fun RunningFeedbackScreen(
    onBack: () -> Unit,
    onSubmit: (RunningFeedback) -> Unit = {},
    previousFeedback: List<SubmittedFeedback> = emptyList(),
    onCreateCourseClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val green = Color(0xFF73F212)

    // 상태들 ----------------------------------------------------
    var courseRating by remember { mutableIntStateOf(0) }        // 별점 1~5
    val painOptions = listOf("종아리", "무릎", "발목", "허벅지", "발바닥", "정강이")
    var selectedPains by remember { mutableStateOf<Set<String>>(emptySet()) }
    var difficulty by remember { mutableIntStateOf(0) }          // 1~5
    var comment by remember { mutableStateOf("") }

    LaunchedEffect(previousFeedback) {
        val latest = previousFeedback.maxByOrNull { it.createdAt }
        if (latest != null) {
            courseRating = latest.rating
            selectedPains = latest.injuryParts.toSet()
            difficulty = latest.difficulty
            comment = latest.comment
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // ---------- 상단 앱바 ----------
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

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Runner's High",
                        fontFamily = RacingSansOne,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Running Feedback",
                        fontSize = 12.sp
                    )
                }

                IconButton(onClick = { /* TODO: 메뉴 필요시 연결 */ }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (previousFeedback.isNotEmpty()) {
                val latest = previousFeedback.maxByOrNull { it.createdAt }
                Text(
                    text = "최근 제출한 피드백을 불러왔어요.",
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
                latest?.let { feedback ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF4F4F4)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "별점: ${feedback.rating} / 5")
                            Text(text = "난이도: ${feedback.difficulty} / 5")
                            if (feedback.injuryParts.isNotEmpty()) {
                                Text(text = "통증 부위: ${feedback.injuryParts.joinToString()}")
                            }
                            if (feedback.comment.isNotBlank()) {
                                Text(text = "코멘트: ${feedback.comment}")
                            }
                            if (feedback.createdAt.isNotBlank()) {
                                Text(text = "작성일: ${feedback.createdAt}")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // ---------- 1. 코스는 어떠셨나요? (별점) ----------
            Text(
                text = "코스는 어떠셨나요?",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                (1..5).forEach { star ->
                    val filled = star <= courseRating
                    Icon(
                        imageVector = if (filled) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "별점 $star",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { courseRating = star }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ---------- 2. 아픈 곳 체크 ----------
            Text(
                text = "아픈 곳이 있으신가요?",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                text = "해당 하는 부위 모두 선택해주세요",
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 두 줄로 나눠서 배치 (3개 + 3개)
            val firstRow = painOptions.take(3)
            val secondRow = painOptions.drop(3)

            PainRow(
                labels = firstRow,
                selected = selectedPains,
                onToggle = { label ->
                    selectedPains =
                        if (selectedPains.contains(label)) selectedPains - label
                        else selectedPains + label
                },
                green = green
            )

            Spacer(modifier = Modifier.height(8.dp))

            PainRow(
                labels = secondRow,
                selected = selectedPains,
                onToggle = { label ->
                    selectedPains =
                        if (selectedPains.contains(label)) selectedPains - label
                        else selectedPains + label
                },
                green = green
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ---------- 3. 난이도 ----------
            Text(
                text = "러닝 플랜의 난이도는 어떠셨나요?",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                (1..5).forEach { level ->
                    DifficultyChip(
                        value = level,
                        selected = (difficulty == level),
                        onClick = { difficulty = level },
                        green = green
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "쉬움", fontSize = 14.sp)
                Text(text = "어려움", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ---------- 4. 추가 의견 ----------
            Text(
                text = "추가로 남기실 의견이 있으신가요?",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                textStyle = MaterialTheme.typography.bodyMedium,
                placeholder = { Text("자유롭게 작성해 주세요.") }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onCreateCourseClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = green
                ),
                border = BorderStroke(1.dp, green),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "새 코스 생성",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ---------- 제출 버튼 ----------
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        onSubmit(
                            RunningFeedback(
                                courseRating = courseRating,
                                painAreas = selectedPains.toList(),
                                difficulty = difficulty,
                                comment = comment
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = green,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "피드백 제출하기",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/* ---------- 작은 컴포저블들 ---------- */

@Composable
private fun PainRow(
    labels: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit,
    green: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        labels.forEach { label ->
            val isSelected = selected.contains(label)
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .height(40.dp)
                    .clickable { onToggle(label) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) green else Color.White,
                shadowElevation = 2.dp,
                border = if (isSelected) null
                else androidx.compose.foundation.BorderStroke(1.dp, Color.Black)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun DifficultyChip(
    value: Int,
    selected: Boolean,
    onClick: () -> Unit,
    green: Color
) {
    Surface(
        modifier = Modifier
            .size(64.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (selected) green else Color.White,
        shadowElevation = 4.dp,
        border = if (selected) null
        else androidx.compose.foundation.BorderStroke(1.dp, Color.Black)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString(),
                fontFamily = RacingSansOne,
                fontSize = 24.sp
            )
        }
    }
}
