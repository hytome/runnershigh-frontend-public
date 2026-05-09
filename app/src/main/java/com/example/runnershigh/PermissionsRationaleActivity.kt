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

class PermissionsRationaleActivity : ComponentActivity() {
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
                        text = "건강 정보 권한 안내",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Runner's High는 걸음 수, 심박수, 수면 기록을 기반으로 러닝 리포트를 제공합니다.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "권한은 기능 제공을 위해서만 사용되며, 언제든지 설정에서 철회할 수 있습니다.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
