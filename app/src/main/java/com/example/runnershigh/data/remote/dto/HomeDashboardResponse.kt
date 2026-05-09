package com.example.runnershigh.data.remote.dto

import com.squareup.moshi.Json

data class HomeDashboardResponse(
    @Json(name = "condition")
    val condition: HomeConditionSummary? = null,

    @Json(name = "today_plan")
    val todayPlan: TodayPlanResponse? = null
)

data class HomeConditionSummary(
    @Json(name = "condition_score")
    val conditionScore: Int? = null,

    @Json(name = "status")
    val status: String? = null,

    @Json(name = "recommendation_type")
    val recommendationType: String? = null,

    @Json(name = "load_modifier")
    val loadModifier: Double? = null,

    @Json(name = "recommendation_text")
    val recommendationText: String? = null,

    @Json(name = "breakdown")
    val breakdown: ConditionBreakdown? = null
)

data class ConditionBreakdown(
    @Json(name = "sleep")
    val sleep: Double? = null,

    @Json(name = "heart_rhr")
    val heartRhr: Int? = null,

    @Json(name = "heart_hrv")
    val heartHrv: Int? = null,

    @Json(name = "feeling")
    val feeling: Int? = null
)

data class TodayPlanResponse(
    @Json(name = "title")
    val title: String? = null,

    @Json(name = "text")
    val text: String? = null,

    @Json(name = "type")
    val type: String? = null,

    @Json(name = "distance")
    val distance: Double? = null,

    @Json(name = "target_distance")
    val targetDistance: Double? = null,

    @Json(name = "target_pace_sec_per_km")
    val targetPaceSecPerKm: Int? = null,

    @Json(name = "xp_reward")
    val xpReward: Int? = null,

    @Json(name = "is_completed")
    val isCompleted: Boolean? = null,

    @Json(name = "condition_score")
    val conditionScore: Int? = null
)
