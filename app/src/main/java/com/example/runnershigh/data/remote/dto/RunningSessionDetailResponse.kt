package com.example.runnershigh.data.remote.dto

import com.squareup.moshi.Json

data class RunningSessionDetailResponse(
    @Json(name = "totalDistanceKm") val totalDistanceKm: Double = 0.0,
    @Json(name = "totalTimeSec") val totalTimeSec: Int = 0,
    @Json(name = "avgPaceSecPerKm") val avgPaceSecPerKm: Int = 0,
    val calories: Int = 0,
    @Json(name = "avgHeartRate") val avgHeartRate: Int = 0,
    @Json(name = "elevationGainM") val elevationGainM: Int = 0,
    val cadence: Int = 0,
    @Json(name = "gpsLogs") val gpsLogs: List<RunningGpsLog> = emptyList()
)
