package com.example.runnershigh.data.remote.api

import com.example.runnershigh.data.remote.dto.*
import retrofit2.http.GET
import retrofit2.http.Query

// AnalysisApi(분석 api) 모음
interface AnalysisApi {

    @GET("analysis/injury")
    suspend fun getInjuryAnalysis(
        @Query("userId") userId: String
    ): InjuryAnalysisResponse

    @GET("analysis/pace-drop")
    suspend fun getPaceDropAnalysis(
        @Query("sessionId") sessionId: String
    ): PaceDropAnalysisResponse

    @GET("hrv_stats_api")
    suspend fun getHRVStats(
        @Query("sessionId") sessionId: String
    ): HRVStatsResponse

    @GET("analysis/feedback")
    suspend fun getCustomFeedback(
        @Query("userId") userId: String,
        @Query("sessionId") sessionId: String
    ): CustomFeedbackResponse

    @GET("analysis/weekly-condition")
    suspend fun getWeeklyCondition(
        @Query("userId") userId: String,
        @Query("year") year: Int,
        @Query("month") month: Int
    ): WeeklyConditionResponse

    @GET("overall_condition_api")
    suspend fun getOverallCondition(
        @Query("userId") userId: String
    ): OverallConditionResponse

    @GET("analysis/pace")
    suspend fun getPerformanceComparison(
        @Query("userId") userId: String,
        @Query("days") days: Int = 7
    ): PerformanceComparisonResponse
}