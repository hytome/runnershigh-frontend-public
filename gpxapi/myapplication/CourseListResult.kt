package com.pack.myapplication

import com.google.gson.annotations.SerializedName

/**
 * 코스 목록 GET 요청의 서버 응답 최상위 데이터 모델입니다.
 */
data class CourseListResult(
    @SerializedName("success")
    val success: Boolean, // 서버 처리 성공 여부

    // 핵심 필드: 서버 응답의 "courses" 배열을 CourseData 리스트로 매핑합니다.
    @SerializedName("courses")
    val courses: List<CourseData>?, // 코스 데이터 리스트 (데이터가 없을 경우 null)

    @SerializedName("message")
    val message: String? // 통신 결과 메시지 (성공 또는 오류 메시지)
)