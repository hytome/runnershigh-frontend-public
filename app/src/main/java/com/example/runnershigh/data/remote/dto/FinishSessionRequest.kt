package com.example.runnershigh.data.remote.dto

/**
 * 러닝 종료 API (PATCH /sessions/{sessionId}/finish) 요청 바디
 *
 * 예시 (API 문서):
 * {
 *   "totalDistance": 3.0,
 *   "averagePace": "6'45\"",
 *   "duration": "20:15",
 *   "calories": 216,
 *   "elevationGain": 28,
 *   "cadence": 152
 * }
 */
data class FinishSessionRequest(
    val sessionId: String,        // 백엔드 로직 매핑용
    val userId: String,
    val totalDistanceKm: Double,   // 총 거리 (km)
    val totalTimeSec: Int,         // 총 러닝 시간 (초)
    val calories: Int?,            // 선택 필드라서 nullable 로
    val avgPaceSecPerKm: Int,      // 평균 페이스 (초/킬로)
    val avgHeartRate: Int?,
    val elevationGainM: Int?,
    val cadence: Int?,
    val targetDistanceKm: Double?,
    val targetPaceSecPerKm: Int?,
    val goalCompleted: Boolean,
    val gpsLogs: List<RunningGpsLog> = emptyList(),
    val heartLogs: List<RunningHeartLogRequest> = emptyList()
)
