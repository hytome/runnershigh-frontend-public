package com.example.runnershigh.data.remote.dto

data class HeartRateData(
    val bpm: Double,
    val time: String
)

data class HealthData(
    val userId: String? = null,
    val user_uuid: String? = null,
    val heartRates: List<HeartRateData> = emptyList(),
    val steps: Int? = null,
    val calories: Double? = null,
    val sleepDurationMinutes: Int? = null,
    val avgRestingHeartRate: Double? = null,
    val avgHRV: Double? = null,
    val measuredAt: String? = null, // yyyy-MM-dd HH:mm:ss
    val healthConnectConsented: Boolean? = null,
    val consentedAt: String? = null
)
