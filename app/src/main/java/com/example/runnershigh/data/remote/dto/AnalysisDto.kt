package com.example.runnershigh.data.remote.dto

data class InjuryAnalysisItem(
    val part: String,
    val percentage: Int,
    val count: Int
)

data class InjuryAnalysisResponse(
    val userId: String,
    val injuries: List<InjuryAnalysisItem>,
    val totalCount: Int
)

data class PaceDropSection(
    val distance: String,
    val avgPace: String,
    val paceChange: String?,
    val status: String,
    val severity: String
)

data class PaceDropAnalysisResponse(
    val sessionId: String,
    val sections: List<PaceDropSection>
)

data class HRVStatsResponse(
    val sessionId: String,
    val avgHRV: Int,
    val minHRV: Int,
    val maxHRV: Int,
    val trend: String,
    val dailyHRV: List<DailyHRV>
)

data class DailyHRV(
    val date: String,
    val hrv: Int
)

data class FeedbackItem(
    val category: String,
    val title: String,
    val description: String,
    val tips: List<String>
)

data class CustomFeedbackResponse(
    val userId: String,
    val sessionId: String,
    val feedbacks: List<FeedbackItem>
)

data class WeeklyConditionResponse(
    val userId: String,
    val year: Int,
    val month: Int,
    val weeklyScores: List<WeeklyConditionScore>
)

data class WeeklyConditionScore(
    val week: String,
    val score: Int
)

data class OverallConditionResponse(
    val userId: String,
    val conditionLevel: Int,
    val evaluation: String
)

data class PerformanceComparisonResponse(
    val userId: String,
    val categories: List<PerformanceCategory>
)

data class PerformanceCategory(
    val name: String,
    val target: Int,
    val actual: Int
)