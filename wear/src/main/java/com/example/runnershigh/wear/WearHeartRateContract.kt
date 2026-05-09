package com.example.runnershigh.wear

object WearHeartRateContract {
    const val HEART_RATE_PATH = "/heart_rate"
    const val HEART_RATE_PATH_LEGACY = "/heartRate"
    const val HEART_RATE_PATH_ALT = "/wear/heart_rate"

    const val COMMAND_PATH = "/match_command"
    const val COMMAND_PATH_LEGACY = "/heart_rate_command"

    const val KEY_BPM = "bpm"
    const val KEY_BPM_HEART_RATE = "heart_rate"
    const val KEY_BPM_HEART_RATE_CAMEL = "heartRate"
    const val KEY_TIMESTAMP = "timestamp"

    const val CMD_START = "START"
    const val CMD_STOP = "STOP"
    const val CMD_START_LOWER = "start"
    const val CMD_STOP_LOWER = "stop"
}
