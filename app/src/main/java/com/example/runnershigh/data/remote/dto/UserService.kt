package com.example.runnershigh.data.remote.dto

import com.example.runnershigh.data.remote.ApiEndpoints
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    //ApiEndpoints.ACQUIRE_BADGE_API

    @POST(ApiEndpoints.ACQUIRE_BADGE_API)
    suspend fun acquireBadges(
        @Body request: AcquireBadgeRequest
    ): Response<AcquireBadgeResponse>

    @POST(ApiEndpoints.GET_USER_LEVEL_API)
    suspend fun getUserLevel(
    @Body request: UserIdRequest
    ): Response<UserLevel>


    @POST(ApiEndpoints.GET_HOME_DASHBOARD_API)
    suspend fun getHomeDashboard(
        @Body request: UserIdRequest
    ): Response<HomeDashboardResponse>


    @POST(ApiEndpoints.GET_UPCOMING_MISSIONS)
    suspend fun getUserMissions(
        @Body request: UserIdRequest
    ): Response<List<Mission>>

    @POST(ApiEndpoints.GET_BADGE_LIST_API)
    suspend fun getAllBadges(
        @Body request: UserIdRequest
    ): Response<List<Badge>>



    @POST(ApiEndpoints.GET_CONDITION_DETAIL_API)
    suspend fun getUserCondition(
        @Body request: UserIdRequest
    ): Response<UserCondition>
}