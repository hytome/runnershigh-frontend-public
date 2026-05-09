package com.example.runnershigh.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.runnershigh.ui.theme.RacingSansOne
import com.example.runnershigh.ui.AuthViewModel

/**
 * 러닝 목적 선택 화면
 *
 * @param onGoalSelected  목적 선택 후 다음 화면으로 넘어갈 때 호출 (네비게이션 용)
 */
@Composable
fun GoalSelectionScreen(
    onGoalSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel()
) {
    // 현재 선택된 목적 (라벨 텍스트 기준)
    var selectedGoal by remember { mutableStateOf<String?>(null) }

    // 우리 앱에서 서버로 보낼 코드 값 (필요하면 한/영 매핑 바꿔도 좋음)
    fun goalLabelToCode(label: String): String = when (label) {
        "다이어트 / 체중 감량" -> "diet"
        "투기종목/체력증진" -> "combat_fitness"
        "마라톤 / 장거리" -> "marathon"
        else -> label       // 혹시 모를 기본값
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFF440B)) // 브랜딩 배경색
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {
            // 제목
            Text(
                text = "러닝 목적은 무엇인가요?",
                color = Color.White,
                fontFamily = RacingSansOne,
                fontSize = 33.sp,  // 37sp → 32sp로 줄임
                fontWeight = FontWeight.Normal,

                overflow = TextOverflow.Visible,  // 잘리지 않고 표시
                softWrap = false,  // 줄바꿈 방지
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 옵션 1
            GoalOptionRow(
                label = "다이어트 / 체중 감량",
                selected = selectedGoal == "다이어트 / 체중 감량",
                onClick = { clicked ->
                    selectedGoal = clicked
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 옵션 2
            GoalOptionRow(
                label = "투기종목/체력증진",
                selected = selectedGoal == "투기종목/체력증진",
                onClick = { clicked ->
                    selectedGoal = clicked
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 옵션 3
            GoalOptionRow(
                label = "마라톤 / 장거리",
                selected = selectedGoal == "마라톤 / 장거리",
                onClick = { clicked ->
                    selectedGoal = clicked
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Next 버튼
            Button(
                onClick = {
                    selectedGoal?.let { label ->
                        // 1) 선택된 목표를 ViewModel에 저장
                        val code = goalLabelToCode(label)
                        authViewModel.onPurposeSelected(code)

                        // 2) 네비게이션 콜백 호출 (경험 입력 화면으로 이동)
                        onGoalSelected(code)
                    }
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .width(160.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFFFF440B)
                )
            ) {
                Text(
                    text = "Next",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun GoalOptionRow(
    label: String,
    selected: Boolean,
    onClick: (String) -> Unit
) {
    val background = if (selected) Color.White else Color.Transparent
    val textColor = if (selected) Color(0xFFFF440B) else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(background)
            .clickable { onClick(label) }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = selected,
            onCheckedChange = { onClick(label) },
            colors = CheckboxDefaults.colors(
                checkedColor = Color.White,
                uncheckedColor = Color.White,
                checkmarkColor = Color(0xFFFF440B)
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontFamily = RacingSansOne,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}
