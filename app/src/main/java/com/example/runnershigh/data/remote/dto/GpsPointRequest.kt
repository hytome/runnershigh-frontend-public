package com.example.runnershigh.data.remote.dto

data class GpsPointRequest(
    val latitude: Double,
    val longitude: Double,
    val timestamp: String   // ISO 8601 문자열 (예: 2025-01-21T14:03:05Z)
)