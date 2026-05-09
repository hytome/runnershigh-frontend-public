package com.example.runnershigh.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.runnershigh.ui.theme.RacingSansOne
import android.util.Patterns

@Composable
fun ForgotPasswordScreen(
    onBackClick: () -> Unit,
    onResetClick: (email: String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    fun handleReset() {
        // 이메일 형식 체크
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            message = "유효한 이메일 주소를 입력해주세요."
            return
        }

        // TODO:
        // 여기서 실제 서버에 해당 이메일 존재 여부를 확인해야 함.
        // 예: POST /password/reset { email } 호출 후
        // - 존재하지 않으면: "해당하는 이메일이 없습니다."
        // - 존재하면: "초기화 메일을 전송했습니다."

        // 일단은 더미 로직 (나중에 제거 예정)
        val emailExists = true  // 서버 연동 전까지는 항상 존재한다고 가정

        if (emailExists) {
            message = "초기화 메일을 전송했습니다."
            onResetClick(email)
        } else {
            message = "해당하는 이메일이 없습니다."
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Runner's",
            fontFamily = RacingSansOne,
            fontSize = 96.sp,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 8.dp, y = 40.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onBackClick
                ) {
                    Text(text = "Cancel", color = Color.Black)
                }

                Spacer(modifier = Modifier.width(24.dp))

                Button(
                    onClick = { handleReset() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(text = "Reset Password")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 결과 메시지
            message?.let { msg ->
                Text(
                    text = msg,
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
        }

        Text(
            text = "High.",
            fontFamily = RacingSansOne,
            fontSize = 96.sp,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-16).dp, y = (-24).dp)
        )
    }
}
