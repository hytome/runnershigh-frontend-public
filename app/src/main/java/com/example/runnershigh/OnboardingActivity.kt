package com.example.runnershigh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.runnershigh.ui.theme.RunnersHighTheme

class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RunnersHighTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Health Connect 온보딩",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "러닝 분석을 위해 Health Connect 연결이 필요합니다.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "연결 후 필요한 권한을 허용하면 러닝 통계가 자동으로 동기화됩니다.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
