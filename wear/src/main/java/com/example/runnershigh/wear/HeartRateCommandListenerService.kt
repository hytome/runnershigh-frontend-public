package com.example.runnershigh.wear

import android.content.Intent
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class HeartRateCommandListenerService : WearableListenerService() {
    override fun onMessageReceived(messageEvent: MessageEvent) {
        val commandPath = messageEvent.path
        if (commandPath != WearHeartRateContract.COMMAND_PATH && commandPath != WearHeartRateContract.COMMAND_PATH_LEGACY) {
            return
        }

        val payload = messageEvent.data?.toString(Charsets.UTF_8)?.trim().orEmpty()
        when (payload) {
            WearHeartRateContract.CMD_START,
            WearHeartRateContract.CMD_START_LOWER -> {
                startService(Intent(this, HeartRateForegroundService::class.java).apply {
                    action = HeartRateForegroundService.ACTION_START
                })
            }

            WearHeartRateContract.CMD_STOP,
            WearHeartRateContract.CMD_STOP_LOWER -> {
                startService(Intent(this, HeartRateForegroundService::class.java).apply {
                    action = HeartRateForegroundService.ACTION_STOP
                })
            }
        }
    }
}
