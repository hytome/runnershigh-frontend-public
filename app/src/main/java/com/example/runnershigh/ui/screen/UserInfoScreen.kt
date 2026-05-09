package com.example.runnershigh.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.runnershigh.R
import com.example.runnershigh.ui.theme.RacingSansOne
import androidx.compose.material3.TextFieldDefaults
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.runnershigh.ui.AuthViewModel

@Composable
fun UserInfoScreen(
    onNextClick: (height: String, weight: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel()
) {
    val (height, setHeight) = remember { mutableStateOf("") }
    val (weight, setWeight) = remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF21212)) // 빨간 배경
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // 상단 Welcome 화살표
            Image(
                painter = painterResource(id = R.drawable.welcome_arrow),
                contentDescription = "Welcome",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Height(신장) 라벨
            Text(
                text = "Height(신장)",
                fontFamily = RacingSansOne,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Normal,
                fontSize = 40.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Height 입력창
            OutlinedTextField(
                value = height,
                onValueChange = {
                    setHeight(it)
                    // ✅ ViewModel 에도 저장해서 나중에 회원가입 API 때 같이 사용
                    viewModel.onBodyHeightChange(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                placeholder = { Text("Cm") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Black,
                    cursorColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Body Weight(체중) 라벨
            Text(
                text = "Body Weight(체중)",
                fontFamily = RacingSansOne,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Normal,
                fontSize = 40.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Weight 입력창
            OutlinedTextField(
                value = weight,
                onValueChange = {
                    setWeight(it)
                    // ✅ ViewModel 에도 저장
                    viewModel.onBodyWeightChange(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                placeholder = { Text("Kg") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Black,
                    cursorColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Next 버튼
            Button(
                onClick = {
                    if (height.isNotBlank() && weight.isNotBlank()) {
                        // ✅ 다음 화면으로 넘겨줄 콜백
                        onNextClick(height, weight)
                    }
                    // TODO: 필요하면 else 에서 Toast/다이얼로그로 "둘 다 입력해 주세요" 추가
                },
                modifier = Modifier
                    .width(180.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFFF21212)
                )
            ) {
                Text(
                    text = "Next",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 밑으로 쭉 밀어버림
            Spacer(modifier = Modifier.weight(1f))

            // 하단 Runner's High.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF21212))
            ) {

                // 전체 내용 (위쪽 배치)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    // … 기존 Welcome, Height, Body Weight, Next 버튼 등 …
                }

                // 오른쪽 아래 Runner's High.
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Runner's",
                        fontFamily = RacingSansOne,
                        fontStyle = FontStyle.Italic,
                        fontSize = 40.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "High.",
                        fontFamily = RacingSansOne,
                        fontStyle = FontStyle.Italic,
                        fontSize = 40.sp,
                        color = Color.White
                    )
                }
            }

        }
    }
}
