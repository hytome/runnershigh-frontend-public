package com.example.runnershigh.data.heartrate

import android.content.Context
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.example.runnershigh.data.wear.WearHeartRateContract
import com.example.runnershigh.data.wear.WearHeartRatePayloadParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Wear OS Data Layer 수신 소스.
 *
 * 워치 앱(센서 측)에서 `/heart_rate` 경로로 전송한 DataItem을 수신하고
 * BPM을 `StateFlow`로 내보냅니다.
 *
 * 포인트:
 * - 이벤트 기반(리스너)이라 폴링보다 지연이 낮음
 * - 워치와 폰 연결이 끊기면 데이터가 늦거나 끊길 수 있으므로
 *   코디네이터에서 신선도(freshness) 판단이 필요함
 */
class WearDataLayerHeartRateSource(
    context: Context
) : HeartRateSource, DataClient.OnDataChangedListener {

    private val appContext = context.applicationContext
    private val dataClient: DataClient by lazy { Wearable.getDataClient(appContext) }

    /** 현재 수신된 BPM */
    private val _bpm = MutableStateFlow(0)
    override val bpm: StateFlow<Int> = _bpm.asStateFlow()

    /** 마지막 수신 시각(epoch ms) - 폰 수신 시각 기준으로 폴백 판단에 사용 */
    private val _lastUpdateEpochMs = MutableStateFlow(0L)
    val lastUpdateEpochMs: StateFlow<Long> = _lastUpdateEpochMs.asStateFlow()

    override fun start() {
        dataClient.addListener(this)
    }

    override fun stop() {
        dataClient.removeListener(this)
    }

    /**
     * Data Layer 이벤트 처리.
     * 기대 포맷:
     * - path: /heart_rate
     * - key: bpm/heart_rate/heartRate, timestamp(Long)
     */
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.use { buffer ->
            for (event in buffer) {
                if (event.type != DataEvent.TYPE_CHANGED) continue
                val path = event.dataItem.uri.path
                val isHeartRatePath = path == WearHeartRateContract.HEART_RATE_PATH ||
                    path == WearHeartRateContract.HEART_RATE_PATH_LEGACY ||
                    path == WearHeartRateContract.HEART_RATE_PATH_ALT
                if (!isHeartRatePath) continue

                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val sample = WearHeartRatePayloadParser.fromDataMap(dataMap) ?: continue

                _bpm.update { sample.bpm }
                _lastUpdateEpochMs.update { System.currentTimeMillis() }
            }
        }
    }
}
