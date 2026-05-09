package com.example.runnershigh.data.remote.dto

import com.example.runnershigh.data.remote.ApiEndpoints
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface AuthApi {

    // 🔹 회원가입
    @POST(ApiEndpoints.SIGNUP_API)
    suspend fun signup(
        @Body request: SignupRequest
    ): Response<SignupResponse>

    // 🔹 로그인
    @POST(ApiEndpoints.LOGIN_API)
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // 🔹 이메일 중복 체크
    @GET(ApiEndpoints.CHECK_EMAIL_API)
    suspend fun checkEmail(
        @Query("email") email: String
    ): Response<EmailCheckResponse>

    // 🔹 닉네임 중복 체크
    @GET(ApiEndpoints.CHECK_USERNAME_API)
    suspend fun checkUsername(
        @Query("username") username: String
    ): Response<UsernameCheckResponse>

    // 🔹 신장 & 체중 업데이트
    @PUT(ApiEndpoints.UPDATE_BODY_API)
    suspend fun updateBody(
        @Body request: BodyUpdateRequest
    ): Response<BodyUpdateResponse>

    // 🔹 러닝 목적 업데이트
    @PUT(ApiEndpoints.UPDATE_PURPOSE_API)
    suspend fun updatePurpose(
        @Body request: UpdatePurposeRequest
    ): Response<BasicResponse>

    // 🔹 러닝 경험 업데이트
    @PUT(ApiEndpoints.UPDATE_EXPERIENCE_API)
    suspend fun updateExperience(
        @Body request: UpdateExperienceRequest
    ): Response<BasicResponse>

    // 🔹 유저 레벨 업데이트
    @POST(ApiEndpoints.UPDATE_USER_LEVEL_API)
    suspend fun updateUserLevel(
        @Body request: UserIdRequest
    ): Response<BasicResponse>

    // 🔹 유저 레벨 조회
    @GET(ApiEndpoints.GET_USER_LEVEL_API)
    suspend fun getUserLevel(
        @Query("user_uuid") userUuid: String
    ): Response<UserLevel>

    // 🔹 유저 프로필 조회
    @GET(ApiEndpoints.GET_PROFILE_API)
    suspend fun getProfile(
        @Query("user_uuid") userUuid: String
    ): Response<UserCondition>

    // 🔹 컨디션 상세 조회
    @GET(ApiEndpoints.GET_CONDITION_DETAIL_API)
    suspend fun getConditionDetail(
        @Query("user_uuid") userUuid: String
    ): Response<UserCondition>

    // 🔹 홈 대시보드 데이터

}
