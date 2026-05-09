package com.example.runnershigh.data.remote.dto
import com.squareup.moshi.Json

data class UserCondition(
    @Json(name = "healthConnectConsented")
    val healthConnectConsented: Boolean? = null,

    @Json(name = "healthConnectConsentedAt")
    val healthConnectConsentedAt: String? = null,

    @Json(name = "conditionLevel")
    val conditionLevel: String? = null,

    @Json(name = "today_score")
    val todayScore: Int? = null,

    @Json(name = "today_status")
    val todayStatus: String? = null,

    @Json(name = "recommendation_text")
    val recommendationText: String? = null,

    @Json(name = "injury_feedback")
    val injuryFeedback: String? = null,

    @Json(name = "trend_graph")
    val trendGraph: List<ConditionTrend> = emptyList(),

    @Json(name = "injury_stats")
    val injuryStats: List<InjuryStat> = emptyList()
)

data class ConditionTrend(
    @Json(name = "date")
    val date: String? = null,

    @Json(name = "score")
    val score: Int? = null,

    @Json(name = "load_modifier")
    val loadModifier: Double? = null,

    // 하위 호환: 기존 백엔드에서 modifier를 hrv 필드에 담아 내려주던 케이스
    @Json(name = "hrv")
    val legacyHrvModifier: Double? = null
)

data class InjuryStat(
    @Json(name = "part")
    val part: String? = null,

    @Json(name = "type")
    val type: String? = null,

    @Json(name = "painPart")
    val painPart: String? = null,

    @Json(name = "count")
    val count: Int? = null
)
