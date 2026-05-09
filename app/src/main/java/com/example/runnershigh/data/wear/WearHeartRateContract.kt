package com.example.runnershigh.data.wear

import com.google.android.gms.wearable.DataMap

/**
 * 폰/워치 간 심박 Data Layer 계약(Contract).
 *
 * 즉시 도입 원칙:
 * - path 및 key를 단일 소스로 관리해 폰/워치 구현 불일치 방지
 * - bpm(float/int) 혼용 입력을 허용해 점진적 마이그레이션 지원
 */
object WearHeartRateContract {
    const val HEART_RATE_PATH = "/heart_rate"
    const val HEART_RATE_PATH_LEGACY = "/heartRate"
    const val HEART_RATE_PATH_ALT = "/wear/heart_rate"
    const val COMMAND_PATH = "/match_command"
    const val COMMAND_PATH_LEGACY = "/heart_rate_command"

    const val KEY_BPM_FLOAT = "bpm"
    const val KEY_BPM_INT = "bpm_int"
    const val KEY_BPM_HEART_RATE = "heart_rate"
    const val KEY_BPM_HEART_RATE_CAMEL = "heartRate"
    const val KEY_TIMESTAMP = "timestamp"
    const val KEY_SCHEMA_VERSION = "schema_version"
    const val KEY_SOURCE = "source"

    const val DEFAULT_SCHEMA_VERSION = 1
    const val SOURCE_EXERCISE_CLIENT = "exercise_client"
    const val SOURCE_SENSOR_MANAGER = "sensor_manager"

    const val CMD_START = "START"
    const val CMD_STOP = "STOP"
    const val CMD_START_LOWER = "start"
    const val CMD_STOP_LOWER = "stop"
}

data class WearHeartRateSample(
    val bpm: Int,
    val timestampMs: Long,
    val schemaVersion: Int,
    val source: String?
)

object WearHeartRatePayloadParser {

    fun fromDataMap(dataMap: DataMap, nowMs: Long = System.currentTimeMillis()): WearHeartRateSample? {
        val rawBpm = when {
            dataMap.containsKey(WearHeartRateContract.KEY_BPM_INT) -> {
                dataMap.getInt(WearHeartRateContract.KEY_BPM_INT)
            }

            dataMap.containsKey(WearHeartRateContract.KEY_BPM_HEART_RATE) -> {
                dataMap.getInt(WearHeartRateContract.KEY_BPM_HEART_RATE)
            }

            dataMap.containsKey(WearHeartRateContract.KEY_BPM_HEART_RATE_CAMEL) -> {
                dataMap.getInt(WearHeartRateContract.KEY_BPM_HEART_RATE_CAMEL)
            }

            dataMap.containsKey(WearHeartRateContract.KEY_BPM_FLOAT) -> {
                dataMap.getFloat(WearHeartRateContract.KEY_BPM_FLOAT).toInt()
            }

            else -> 0
        }

        val timestamp = if (dataMap.containsKey(WearHeartRateContract.KEY_TIMESTAMP)) {
            dataMap.getLong(WearHeartRateContract.KEY_TIMESTAMP)
        } else {
            0L
        }

        val schemaVersion = if (dataMap.containsKey(WearHeartRateContract.KEY_SCHEMA_VERSION)) {
            dataMap.getInt(WearHeartRateContract.KEY_SCHEMA_VERSION)
        } else {
            WearHeartRateContract.DEFAULT_SCHEMA_VERSION
        }

        val source = if (dataMap.containsKey(WearHeartRateContract.KEY_SOURCE)) {
            dataMap.getString(WearHeartRateContract.KEY_SOURCE)
        } else {
            null
        }

        return normalize(
            rawBpm = rawBpm,
            rawTimestampMs = timestamp,
            nowMs = nowMs,
            schemaVersion = schemaVersion,
            source = source
        )
    }

    internal fun normalize(
        rawBpm: Int,
        rawTimestampMs: Long,
        nowMs: Long,
        schemaVersion: Int,
        source: String?
    ): WearHeartRateSample? {
        if (rawBpm <= 0) return null
        val safeTimestamp = if (rawTimestampMs > 0L) rawTimestampMs else nowMs
        return WearHeartRateSample(
            bpm = rawBpm,
            timestampMs = safeTimestamp,
            schemaVersion = schemaVersion,
            source = source
        )
    }
}
