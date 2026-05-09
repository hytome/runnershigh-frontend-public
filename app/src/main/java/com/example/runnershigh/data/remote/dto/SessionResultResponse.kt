package com.example.runnershigh.data.remote.dto

import com.squareup.moshi.Json

/**
 * 러닝 결과 조회 API 응답
 *
 * GET /sessions/{sessionId}/result
 *
 * 예시:
 * {
 *   "date": "2025-01-21",
 *   "distance": 3.0,
 *   "averagePace": "6'45\"",
 *   "duration": "20:15",
 *   "calories": 216,
 *   "elevationGain": 28,
 *   "cadence": 152,
 *   "completion": 97,
 *   "targetPace": "6'30\"",
 *   "targetFinishTime": "19:30",
 *   "finishTimeComparison": "목표보다 늦음",
 *   "courseName": "토요일 오후 러닝"
 * }
 */
data class SessionResultResponse(
    val date: String = "",
    val distance: Double = 0.0,
    @Json(name = "distance_km") val distanceKm: Double? = null,
    @Json(name = "distanceKm") val distanceKmCamel: Double? = null,
    @Json(name = "total_distance_km") val totalDistanceKm: Double? = null,
    @Json(name = "totalDistanceKm") val totalDistanceKmCamel: Double? = null,
    val averagePace: String = "",
    @Json(name = "average_pace") val averagePaceAlt: String? = null,
    @Json(name = "avg_pace_sec_per_km") val averagePaceSecondsPerKm: Int? = null,
    @Json(name = "avgPaceSecPerKm") val averagePaceSecondsPerKmCamel: Int? = null,
    val duration: String = "",
    @Json(name = "duration_sec") val durationSeconds: Int? = null,
    @Json(name = "durationSec") val durationSecondsCamel: Int? = null,
    @Json(name = "total_time_sec") val totalTimeSeconds: Int? = null,
    @Json(name = "totalTimeSec") val totalTimeSecondsCamel: Int? = null,
    val calories: Int = 0,
    val elevationGain: Int = 0,
    @Json(name = "elevation_gain_m") val elevationGainMeters: Int? = null,
    @Json(name = "elevationGainM") val elevationGainMetersCamel: Int? = null,
    val cadence: Int = 0,
    @Json(name = "avg_heart_rate") val avgHeartRate: Int = 0,
    @Json(name = "avgHeartRate") val avgHeartRateCamel: Int? = null,
    val completion: Int = 0,
    val targetPace: String = "",
    @Json(name = "target_pace") val targetPaceAlt: String? = null,
    @Json(name = "target_pace_sec_per_km") val targetPaceSecPerKm: Int? = null,
    @Json(name = "targetPaceSecPerKm") val targetPaceSecPerKmCamel: Int? = null,
    val targetFinishTime: String = "",
    @Json(name = "target_finish_time") val targetFinishTimeAlt: String? = null,
    val finishTimeComparison: String = "",
    @Json(name = "finish_time_comparison") val finishTimeComparisonAlt: String? = null,
    val courseName: String = "",
    @Json(name = "course_name") val courseNameAlt: String? = null,
    @Json(name = "badge_acquired") val badgeAcquired: Boolean = false,
    @Json(name = "badgeAcquired") val badgeAcquiredCamel: Boolean? = null,
    @Json(name = "gained_experience") val gainedExperience: Int = 0,
    @Json(name = "gainedExperience") val gainedExperienceCamel: Int? = null
)
