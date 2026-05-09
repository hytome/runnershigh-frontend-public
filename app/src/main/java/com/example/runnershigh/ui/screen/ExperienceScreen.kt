package com.example.runnershigh.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.runnershigh.ui.AuthViewModel
import com.example.runnershigh.ui.theme.RacingSansOne

// 러닝 경험 단계에서 사용할 경험 구분 값
enum class Experience {
    Beginner,        // 전혀 없음
    Experienced,     // 경험 있음
    Competitor       // 대회 참가 경력자
}

/**
 * 온보딩 3단계: 러닝 경험 선택 화면
 *
 * - 옵션 세 개 중 하나만 선택
 * - 선택 결과는 AuthViewModel.updateRunningExperience(code) 로 저장
 * - onNext(code) 로 NavGraph 에도 전달 (register 화면으로 이동)
 */
@Composable
fun ExperienceScreen(
    onNext: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel()
) {
    val backgroundColor = Color(0xFFF29C12)    // 피그마에서 쓰던 주황색

    // 현재 선택된 경험 값
    var selectedExperience by remember { mutableStateOf<Experience?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp, vertical = 40.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            // 제목
            Text(
                text = "러닝 경험이 있으십니까?",
                color = Color.White,
                fontFamily = RacingSansOne,
                fontSize = 32.sp,  // 37sp → 32sp로 줄임
                fontWeight = FontWeight.Normal,
                maxLines = 1,  // 한 줄로 강제
                overflow = TextOverflow.Visible,  // 잘리지 않고 표시
                softWrap = false,  // 줄바꿈 방지
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 1) 전혀 없음
            ExperienceOption(
                label = "전혀 없음.",
                selected = selectedExperience == Experience.Beginner,
                backgroundColor = backgroundColor
            ) {
                selectedExperience = Experience.Beginner
                val code = experienceToCode(Experience.Beginner)
                viewModel.updateRunningExperience(code)
                onNext(code)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2) 경험 있음
            ExperienceOption(
                label = "경험 있음.",
                selected = selectedExperience == Experience.Experienced,
                backgroundColor = backgroundColor
            ) {
                selectedExperience = Experience.Experienced
                val code = experienceToCode(Experience.Experienced)
                viewModel.updateRunningExperience(code)
                onNext(code)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3) 대회 참가 경력자
            ExperienceOption(
                label = "대회 참가 경력자.",
                selected = selectedExperience == Experience.Competitor,
                backgroundColor = backgroundColor
            ) {
                selectedExperience = Experience.Competitor
                val code = experienceToCode(Experience.Competitor)
                viewModel.updateRunningExperience(code)
                onNext(code)
            }

            Spacer(modifier = Modifier.weight(1f))

            // 하단 Runner's High. 로고 텍스트 (디자인 유지용)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Runner's",
                    color = Color.White,
                    fontFamily = RacingSansOne,
                    fontSize = 40.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "High.",
                    color = Color.White,
                    fontFamily = RacingSansOne,
                    fontSize = 40.sp
                )
            }
        }
    }
}

/**
 * Experience 값을 서버 코드로 변환
 *  - API 명세에 맞춰서 문자열만 바꾸면 됨
 */
private fun experienceToCode(exp: Experience): String =
    when (exp) {
        Experience.Beginner    -> "beginner"
        Experience.Experienced -> "experienced"
        Experience.Competitor  -> "competitor"
    }

/**
 * 개별 경험 옵션 UI (체크박스 + 텍스트)
 */
@Composable
private fun ExperienceOption(
    label: String,
    selected: Boolean,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = selected,
            onCheckedChange = { onClick() },
            colors = CheckboxDefaults.colors(
                checkedColor = Color.White,
                uncheckedColor = Color.White,
                checkmarkColor = backgroundColor
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            color = Color.White,
            fontFamily = RacingSansOne,
            fontSize = 28.sp
        )
    }
}
