package com.example.runnershigh.data.remote.dto

import com.squareup.moshi.Json

data class ActivityStatsResponse(
    @Json(name = "monthly_summary") val monthlySummary: MonthlySummary = MonthlySummary(),
    @Json(name = "daily_graph") val dailyGraph: List<DailyGraphEntry> = emptyList(),
    @Json(name = "heart_rate_analysis") val heartRateAnalysis: HeartRateAnalysis = HeartRateAnalysis(),
    @Json(name = "pace_analysis") val paceAnalysis: List<PaceAnalysisEntry> = emptyList(),
    @Json(name = "recent_activities") val recentActivities: List<RecentActivity> = emptyList()
)

data class MonthlySummary(
    @Json(name = "total_distance") val totalDistance: Double = 0.0,
    @Json(name = "total_count") val totalCount: Int = 0,
    @Json(name = "avg_pace") val averagePaceSeconds: Int = 0,
    @Json(name = "avg_heart_rate") val averageHeartRate: Int = 0
)

data class DailyGraphEntry(
    val day: Int = 0,
    val distance: Double = 0.0
)

data class HeartRateAnalysis(
    val recovery: Int = 0,
    val aerobic: Int = 0,
    val tempo: Int = 0,
    val high: Int = 0
)

data class PaceAnalysisEntry(
    val date: String = "",
    val status: String = "",
    val diff: Int = 0,
    @Json(name = "actual_pace") val actualPace: Int = 0
)

data class RecentActivity(
    @Json(name = "sessionId") val sessionId: String = "",
    val date: String = "",
    val distance: Double = 0.0,
    @Json(name = "time") val durationSeconds: Int = 0,
    @Json(name = "pace") val paceSeconds: Int = 0,
    @Json(name = "heartRate") val heartRate: Int = 0,
    @Json(name = "cadence") val cadence: Int = 0,
    @Json(name = "calories") val calories: Int = 0
)

data class ConditionLevelResponse(
    val conditionLevel: Int = 0,
    val analysis: String = "",
    val lastUpdated: String = ""
)

data class AchievementResponse(
    val totalMissions: Int = 0,
    val completedMissions: Int = 0,
    val achievementRate: Int = 0
)
