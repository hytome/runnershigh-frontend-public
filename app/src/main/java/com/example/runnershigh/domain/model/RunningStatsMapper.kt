package com.example.runnershigh.domain.model

import com.example.runnershigh.data.remote.dto.*
// parsePaceToSeconds 를 만든 패키지에 맞게 import 해줘
import com.example.runnershigh.util.parsePaceToSeconds

/**
 * 서버에서 받은 세션 결과 → 앱에서 쓰는 RunningStats 로 변환하는 확장 함수
 */
/**
 * Pace 문자열("6'45\"") → 초(Int) 변환
 */
// SessionResultResponse -> RunningStats로 변환
fun SessionResultResponse.toRunningStats(): RunningStats {
    val resolvedDistance = when {
        distance > 0 -> distance
        distanceKm != null && distanceKm > 0 -> distanceKm
        distanceKmCamel != null && distanceKmCamel > 0 -> distanceKmCamel
        totalDistanceKm != null && totalDistanceKm > 0 -> totalDistanceKm
        totalDistanceKmCamel != null && totalDistanceKmCamel > 0 -> totalDistanceKmCamel
        else -> 0.0
    }

    val resolvedDurationSeconds = when {
        duration.isNotBlank() -> parseDurationToSeconds(duration)
        durationSeconds != null && durationSeconds > 0 -> durationSeconds
        durationSecondsCamel != null && durationSecondsCamel > 0 -> durationSecondsCamel
        totalTimeSeconds != null && totalTimeSeconds > 0 -> totalTimeSeconds
        totalTimeSecondsCamel != null && totalTimeSecondsCamel > 0 -> totalTimeSecondsCamel
        else -> 0
    }

    val resolvedPaceSeconds = when {
        averagePace.isNotBlank() -> parsePaceToSeconds(averagePace)
        !averagePaceAlt.isNullOrBlank() -> parsePaceToSeconds(averagePaceAlt)
        averagePaceSecondsPerKm != null && averagePaceSecondsPerKm > 0 -> averagePaceSecondsPerKm
        averagePaceSecondsPerKmCamel != null && averagePaceSecondsPerKmCamel > 0 -> averagePaceSecondsPerKmCamel
        else -> 0
    }

    val resolvedElevation = when {
        elevationGainMeters != null && elevationGainMeters > 0 -> elevationGainMeters
        elevationGainMetersCamel != null && elevationGainMetersCamel > 0 -> elevationGainMetersCamel
        else -> elevationGain
    }

    return RunningStats(
        distanceKm = resolvedDistance,
        durationSec = resolvedDurationSeconds,
        paceSecPerKm = resolvedPaceSeconds,
        calories = calories,
        avgHeartRate = avgHeartRateCamel ?: avgHeartRate,
        elevationGainM = resolvedElevation,
        cadence = cadence
    )
}

// "MM:SS" 형식 문자열을 초 단위로 변환하는 헬퍼
private fun parseDurationToSeconds(text: String): Int {
    // 예: "20:15" -> 20분 15초
    val parts = text.split(":")
    if (parts.size != 2) return 0
    val min = parts[0].toIntOrNull() ?: 0
    val sec = parts[1].toIntOrNull() ?: 0
    return min * 60 + sec
}
