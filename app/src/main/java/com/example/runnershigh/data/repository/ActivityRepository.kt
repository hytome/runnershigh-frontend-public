package com.example.runnershigh.data.repository

import com.example.runnershigh.data.remote.api.ActivityApi
import com.example.runnershigh.data.remote.dto.ActivityStatsResponse
import com.example.runnershigh.data.remote.dto.AchievementResponse
import com.example.runnershigh.data.remote.dto.ConditionLevelResponse
import com.example.runnershigh.data.remote.dto.UserCondition
import com.example.runnershigh.data.remote.dto.UserIdRequest
import com.example.runnershigh.data.remote.dto.UserService
import retrofit2.HttpException

class ActivityRepository(
    private val activityApi: ActivityApi,
    private val userService: UserService
) {
    suspend fun getActivitySummary(userUuid: String, year: Int, month: Int): ActivityStatsResponse =
        activityApi.getActivitySummary(userUuid, year, month)

    suspend fun getActivityTotal(userUuid: String): ActivityStatsResponse =
        activityApi.getActivityTotal(userUuid)

    suspend fun getActivityYearly(userUuid: String, year: Int): ActivityStatsResponse =
        activityApi.getActivityYearly(userUuid, year)

    suspend fun getMonthlyAverage(userUuid: String, year: Int, month: Int): ActivityStatsResponse =
        activityApi.getMonthlyAverage(userUuid, year, month)

    suspend fun getMonthlyHeartZone(userUuid: String, year: Int, month: Int): ActivityStatsResponse =
        activityApi.getMonthlyHeartZone(userUuid, year, month)

    suspend fun getRecentActivities(userUuid: String, limit: Int): ActivityStatsResponse =
        activityApi.getRecentActivities(userUuid, limit)

    suspend fun getConditionLevel(userUuid: String): ConditionLevelResponse =
        activityApi.getConditionLevel(userUuid)

    suspend fun getConditionDetail(userUuid: String): UserCondition {
        val response = userService.getUserCondition(UserIdRequest(user_uuid = userUuid))
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        return response.body() ?: UserCondition()
    }

    suspend fun getConditionSnapshot(userUuid: String): ConditionSnapshot {
        val levelResult = runCatching { getConditionLevel(userUuid) }.getOrNull()
        var detailResult = runCatching { getConditionDetail(userUuid) }.getOrNull()

        val needsPrimeCall = detailResult == null ||
            (detailResult.todayScore == null && detailResult.trendGraph.isEmpty())

        if (needsPrimeCall) {
            // 백엔드 구조상 get_home_dashboard 호출 시 condition_history가 생성되므로,
            // 상세 조회 전에 1회 프라이밍 호출을 수행한다.
            runCatching { userService.getHomeDashboard(UserIdRequest(user_uuid = userUuid)) }
            detailResult = runCatching { getConditionDetail(userUuid) }.getOrNull()
        }

        return mergeConditionSnapshot(level = levelResult, detail = detailResult)
    }

    suspend fun getAchievement(userUuid: String): AchievementResponse =
        activityApi.getAchievement(userUuid)
}
