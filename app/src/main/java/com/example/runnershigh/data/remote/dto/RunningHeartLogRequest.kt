package com.example.runnershigh.data.remote.dto

data class RunningHeartLogRequest(
    val heartRate: Int,
    val timestamp: String,
    val distanceKm: Double? = null,
    val isEvent: Boolean = false
)
