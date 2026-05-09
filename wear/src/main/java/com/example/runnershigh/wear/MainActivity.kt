package com.example.runnershigh.wear

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.google.android.gms.wearable.Wearable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestBodySensorsPermissionIfNeeded()
        setContent {
            WearMainEntry()
        }
    }

    private fun requestBodySensorsPermissionIfNeeded() {
        if (checkSelfPermission(Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED) return
        requestPermissions(arrayOf(Manifest.permission.BODY_SENSORS), BODY_SENSORS_REQUEST_CODE)
    }

    companion object {
        private const val BODY_SENSORS_REQUEST_CODE = 1001
    }
}

@Composable
private fun WearMainEntry() {
    val context = LocalContext.current
    var isPhoneConnected by remember { mutableStateOf(true) }
    val prefs = remember { context.getSharedPreferences(HealthTileService.PREFS_NAME, Context.MODE_PRIVATE) }
    val bpm = prefs.getInt(HealthTileService.KEY_CURRENT_BPM, 0)

    LaunchedEffect(Unit) {
        Wearable.getNodeClient(context).connectedNodes
            .addOnSuccessListener { nodes ->
                isPhoneConnected = nodes.isNotEmpty()
            }
            .addOnFailureListener {
                isPhoneConnected = false
            }
    }

    MaterialTheme {
        Scaffold {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ActiveLime)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = stringResource(R.string.wear_title),
                        color = Color.Black,
                        fontFamily = RacingSansOne,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isPhoneConnected) {
                            stringResource(R.string.wear_subtitle_ready)
                        } else {
                            stringResource(R.string.wear_subtitle_disconnected)
                        },
                        textAlign = TextAlign.Center,
                        color = Color.Black.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.caption2
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    HeartRatePanel(bpm = bpm)

                    Spacer(modifier = Modifier.height(7.dp))

                    Button(
                        onClick = {},
                        enabled = isPhoneConnected,
                        modifier = Modifier
                            .width(104.dp)
                            .height(34.dp)
                            .clip(CircleShape),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Black,
                            contentColor = ActiveLime,
                            disabledBackgroundColor = Color.Black.copy(alpha = 0.24f),
                            disabledContentColor = Color.Black.copy(alpha = 0.52f)
                        )
                    ) {
                        Text(
                            stringResource(R.string.wear_waiting_button),
                            fontFamily = RacingSansOne,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeartRatePanel(bpm: Int) {
    val bpmText = if (bpm > 0) bpm.toString() else "--"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = bpmText,
            color = Color.Black,
            fontFamily = RacingSansOne,
            fontSize = 58.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
        Text(
            text = "BPM",
            color = Color.Black,
            fontFamily = RacingSansOne,
            fontSize = 23.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Color.Black)
        )
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            MiniMetric(
                value = if (bpm > 0) stringResource(R.string.wear_stat_hr_value, bpm) else stringResource(R.string.wear_stat_empty),
                label = stringResource(R.string.wear_stat_label_hr)
            )
            MiniMetric(value = "LIVE", label = "WEAR")
        }
    }
}

@Composable
private fun MiniMetric(
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = Color.Black,
            fontFamily = RacingSansOne,
            fontSize = 17.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            color = Color.Black,
            fontFamily = RacingSansOne,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

private val RacingSansOne = FontFamily(
    Font(R.font.racing_sans_one, FontWeight.Normal)
)

private val ActiveLime = Color(0xFF73F212)
