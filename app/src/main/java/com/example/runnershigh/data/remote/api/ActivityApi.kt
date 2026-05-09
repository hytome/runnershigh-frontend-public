package com.example.runnershigh.data.remote.api

import com.example.runnershigh.data.remote.ApiEndpoints
import com.example.runnershigh.data.remote.dto.AchievementResponse
import com.example.runnershigh.data.remote.dto.ActivityStatsResponse
import com.example.runnershigh.data.remote.dto.ConditionLevelResponse
import retrofit2.http.GET
import retrofit2.http.Query

// activity(활동 api 모음)
interface ActivityApi {

    @GET("${ApiEndpoints.GET_ACTIVITY_STATS_API}/activity_summary_api")
    suspend fun getActivitySummary(
        @Query("userId") userId: String,
        @Query("year") year: Int,
        @Query("month") month: Int
    ): ActivityStatsResponse

    @GET("${ApiEndpoints.GET_ACTIVITY_STATS_API}/activity_yearly_api")
    suspend fun getActivityYearly(
        @Query("userId") userId: String,
        @Query("year") year: Int
    ): ActivityStatsResponse

    @GET("${ApiEndpoints.GET_ACTIVITY_STATS_API}/activity/summary/total")
    suspend fun getActivityTotal(
        @Query("userId") userId: String
    ): ActivityStatsResponse

    @GET("${ApiEndpoints.GET_ACTIVITY_STATS_API}/activity/recent")
    suspend fun getRecentActivities(
        @Query("userId") userId: String,
        @Query("limit") limit: Int = 5
    ): ActivityStatsResponse

    @GET("${ApiEndpoints.GET_ACTIVITY_STATS_API}/activity/monthly_average")
    suspend fun getMonthlyAverage(
        @Query("userId") userId: String,
        @Query("year") year: Int,
        @Query("month") month: Int
    ): ActivityStatsResponse

    @GET("${ApiEndpoints.GET_ACTIVITY_STATS_API}/activity/monthly-heart-zone")
    suspend fun getMonthlyHeartZone(
        @Query("userId") userId: String,
        @Query("year") year: Int,
        @Query("month") month: Int
    ): ActivityStatsResponse

    @GET("${ApiEndpoints.GET_ACTIVITY_STATS_API}/activity/marathon-feedback")
    suspend fun getMarathonFeedback(
        @Query("userId") userId: String,
        @Query("year") year: Int,
        @Query("month") month: Int
    ): ActivityStatsResponse

    @GET("${ApiEndpoints.GET_ACTIVITY_STATS_API}/activity/monthly-suggestions")
    suspend fun getMonthlySuggestions(
        @Query("userId") userId: String,
        @Query("year") year: Int,
        @Query("month") month: Int
    ): ActivityStatsResponse

    @GET("${ApiEndpoints.GET_ACTIVITY_STATS_API}/activity/monthly-overall")
    suspend fun getMonthlyOverall(
        @Query("userId") userId: String,
        @Query("year") year: Int,
        @Query("month") month: Int
    ): ActivityStatsResponse

    @GET("${ApiEndpoints.GET_ACTIVITY_STATS_API}/activity/condition")
    suspend fun getConditionLevel(
        @Query("userId") userId: String
    ): ConditionLevelResponse

    @GET("${ApiEndpoints.GET_ACTIVITY_STATS_API}/activity/achievement_api")
    suspend fun getAchievement(
        @Query("userId") userId: String
    ): AchievementResponse
}