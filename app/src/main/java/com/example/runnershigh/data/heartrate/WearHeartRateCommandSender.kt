package com.example.runnershigh.data.heartrate

import android.content.Context
import com.example.runnershigh.data.wear.WearHeartRateContract
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 폰 -> 워치 제어 명령 송신 유틸.
 *
 * 시나리오:
 * - 러닝 시작/재개 시 START 전송
 * - 일시정지/종료 시 STOP 전송
 *
 * 워치 앱은 `/match_command` 경로를 수신하여
 * foreground 심박 측정 서비스의 시작/정지를 제어합니다.
 */
class WearHeartRateCommandSender(context: Context) {

    private val appContext = context.applicationContext

    private val _commandState = MutableStateFlow(HeartRateCommandState.IDLE)
    val commandState: StateFlow<HeartRateCommandState> = _commandState.asStateFlow()

    fun sendStart() {
        sendCommand(
            listOf(
                WearHeartRateContract.CMD_START,
                WearHeartRateContract.CMD_START_LOWER
            )
        )
    }

    fun sendStop() {
        sendCommand(
            listOf(
                WearHeartRateContract.CMD_STOP,
                WearHeartRateContract.CMD_STOP_LOWER
            )
        )
    }

    /**
     * 연결된 모든 노드(워치)에 명령 브로드캐스트.
     *
     * 구버전/신버전 wear 앱이 섞여 있어도 동작하도록 command path와 payload case를
     * 모두 전송하고, 하나라도 성공하면 SENT로 간주합니다.
     */
    private fun sendCommand(commands: List<String>) {
        val nodeClient = Wearable.getNodeClient(appContext)
        val messageClient = Wearable.getMessageClient(appContext)
        _commandState.value = HeartRateCommandState.SENDING

        nodeClient.connectedNodes
            .addOnSuccessListener { nodes ->
                if (nodes.isEmpty()) {
                    _commandState.value = HeartRateCommandState.NO_CONNECTED_WATCH
                    return@addOnSuccessListener
                }

                var remaining = nodes.size
                var successCount = 0
                nodes.forEach { node ->
                    val sendTasks = buildSendTasks(messageClient, node.id, commands)
                    var pending = sendTasks.size
                    var nodeSucceeded = false

                    sendTasks.forEach { task ->
                        task.addOnSuccessListener {
                            nodeSucceeded = true
                            pending -= 1
                            if (pending == 0) {
                                if (nodeSucceeded) successCount += 1
                                remaining -= 1
                                updateFinalCommandState(remaining, successCount)
                            }
                        }.addOnFailureListener {
                            pending -= 1
                            if (pending == 0) {
                                if (nodeSucceeded) successCount += 1
                                remaining -= 1
                                updateFinalCommandState(remaining, successCount)
                            }
                        }
                    }
                }
            }
            .addOnFailureListener {
                _commandState.value = HeartRateCommandState.FAILED
            }
    }

    private fun buildSendTasks(
        messageClient: MessageClient,
        nodeId: String,
        commands: List<String>
    ) = buildList {
        val paths = listOf(
            WearHeartRateContract.COMMAND_PATH,
            WearHeartRateContract.COMMAND_PATH_LEGACY
        )

        for (path in paths) {
            for (command in commands) {
                add(messageClient.sendMessage(nodeId, path, command.toByteArray()))
            }
        }
    }

    private fun updateFinalCommandState(remaining: Int, successCount: Int) {
        if (remaining > 0) return
        _commandState.value = if (successCount > 0) {
            HeartRateCommandState.SENT
        } else {
            HeartRateCommandState.FAILED
        }
    }
}

enum class HeartRateCommandState {
    IDLE,
    SENDING,
    SENT,
    NO_CONNECTED_WATCH,
    FAILED
}
