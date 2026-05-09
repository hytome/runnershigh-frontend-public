package com.pack.myapplication

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET // GET 임포트 추가
/**
 * 러닝 기록 관련 서버 API 통신을 위한 Retrofit 인터페이스
 */
interface RunRecordService {

    /**
     * 사용자의 러닝 기록을 서버 API에 HTTP POST 요청으로 업로드합니다.
     *
     * @param request 전송할 러닝 데이터 (RunRecordRequest 객체, JSON으로 변환됨)
     * @return 서버의 응답 (RunRecordResult 객체를 포함한 Response)
     */
    @POST("api/v1/run/upload") // 서버의 실제 엔드포인트로 변경해야 합니다.
    suspend fun uploadRunRecord(
        @Body request: RunRecordRequest
    ): Response<RunRecordResult>
    // ⭐ [추가된 기능] 코스 목록을 서버 API에 HTTP GET 요청으로 가져옵니다.
    @GET("api/v1/courses/list") // 코스 목록 GET 엔드포인트 (가정)
    suspend fun getCourseList(): Response<CourseListResult>
}