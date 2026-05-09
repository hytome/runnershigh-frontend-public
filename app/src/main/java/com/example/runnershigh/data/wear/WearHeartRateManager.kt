package com.example.runnershigh.data.wear

import android.content.Context
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Wear OS 워치앱에서 전달되는 심박수 데이터를 수신하고,
 * 워치앱에 START/STOP 명령을 보내는 매니저.
 */
class WearHeartRateManager(context: Context) : DataClient.OnDataChangedListener {

    private val appContext = context.applicationContext

    private val _heartRateBpm = MutableStateFlow(0)
    val heartRateBpm: StateFlow<Int> = _heartRateBpm.asStateFlow()

    private val _lastUpdatedAtMillis = MutableStateFlow(0L)
    val lastUpdatedAtMillis: StateFlow<Long> = _lastUpdatedAtMillis.asStateFlow()

    private val _commandState = MutableStateFlow(CommandState.IDLE)
    val commandState: StateFlow<CommandState> = _commandState.asStateFlow()

    fun startListening() {
        Wearable.getDataClient(appContext).addListener(this)
    }

    fun stopListening() {
        Wearable.getDataClient(appContext).removeListener(this)
    }

    fun sendStartCommand() {
        sendCommand(
            listOf(
                WearHeartRateContract.CMD_START,
                WearHeartRateContract.CMD_START_LOWER
            )
        )
    }

    fun sendStopCommand() {
        sendCommand(
            listOf(
                WearHeartRateContract.CMD_STOP,
                WearHeartRateContract.CMD_STOP_LOWER
            )
        )
    }

    /**
     * 최근 수신 심박 데이터가 유효한지(신선한지) 판단.
     *
     * @param freshnessWindowMs 마지막 수신 시각 허용 윈도우(ms)
     */
    fun isDataFresh(freshnessWindowMs: Long = DEFAULT_FRESHNESS_WINDOW_MS): Boolean {
        val last = _lastUpdatedAtMillis.value
        if (last <= 0L) return false
        return System.currentTimeMillis() - last <= freshnessWindowMs
    }

    private fun sendCommand(commands: List<String>) {
        val messageClient: MessageClient = Wearable.getMessageClient(appContext)
        val nodeClient = Wearable.getNodeClient(appContext)
        _commandState.value = CommandState.SENDING

        nodeClient.connectedNodes
            .addOnSuccessListener { nodes ->
                if (nodes.isEmpty()) {
                    _commandState.value = CommandState.NO_CONNECTED_WATCH
                    return@addOnSuccessListener
                }
                var remaining = nodes.size
                var successCount = 0
                nodes.forEach { node ->
                    val payloads = commands.map { it.toByteArray() }
                    val paths = listOf(
                        WearHeartRateContract.COMMAND_PATH,
                        WearHeartRateContract.COMMAND_PATH_LEGACY
                    )

                    val sendTasks = buildList {
                        for (path in paths) {
                            for (payload in payloads) {
                                add(messageClient.sendMessage(node.id, path, payload))
                            }
                        }
                    }

                    var pending = sendTasks.size
                    var nodeSucceeded = false
                    sendTasks.forEach { task ->
                        task.addOnSuccessListener {
                            nodeSucceeded = true
                            pending -= 1
                            if (pending == 0) {
                                if (nodeSucceeded) successCount += 1
                                remaining -= 1
                                if (remaining == 0) {
                                    _commandState.value = if (successCount > 0) CommandState.SENT else CommandState.FAILED
                                }
                            }
                        }.addOnFailureListener {
                            pending -= 1
                            if (pending == 0) {
                                if (nodeSucceeded) successCount += 1
                                remaining -= 1
                                if (remaining == 0) {
                                    _commandState.value = if (successCount > 0) CommandState.SENT else CommandState.FAILED
                                }
                            }
                        }
                    }
                }
            }
            .addOnFailureListener {
                _commandState.value = CommandState.FAILED
            }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            if (event.type != DataEvent.TYPE_CHANGED) return@forEach

            val item = event.dataItem
            val path = item.uri.path
            val isHeartRatePath = path == WearHeartRateContract.HEART_RATE_PATH ||
                path == WearHeartRateContract.HEART_RATE_PATH_LEGACY ||
                path == WearHeartRateContract.HEART_RATE_PATH_ALT
            if (!isHeartRatePath) return@forEach

            val dataMap = DataMapItem.fromDataItem(item).dataMap
            val sample = WearHeartRatePayloadParser.fromDataMap(dataMap) ?: return@forEach

            _heartRateBpm.value = sample.bpm
            // 워치/폰 시계 오차 영향을 줄이기 위해 "수신 시각" 기준으로 freshness를 관리
            _lastUpdatedAtMillis.value = System.currentTimeMillis()
        }
    }

    companion object {
        const val DEFAULT_FRESHNESS_WINDOW_MS = 15_000L
    }
}

enum class CommandState {
    IDLE,
    SENDING,
    SENT,
    NO_CONNECTED_WATCH,
    FAILED
}
