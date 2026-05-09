package com.example.runnershigh.data.remote.dto
import com.squareup.moshi.Json

data class RunningCompareResponse(

    val targetDistanceKm: Double = 0.0,
    val gpsLogs: List<RunningGpsLog> = emptyList(),
    val status: String? = null,
    val startTime: String? = null,
    val targetPaceSec: Int? = null,
    val userId: String? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    val heartLogs: List<RunningHeartLog> = emptyList()
)
data class RunningGpsLog(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val timestamp: String? = null
)

data class RunningHeartLog(
    val heartRate: Int? = null,
    val timestamp: String? = null
)