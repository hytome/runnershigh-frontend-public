package com.pack.myapplication



import com.google.gson.annotations.SerializedName

/**
 * 서버에서 GET 요청으로 가져올 코스 데이터의 개별 항목입니다.
 */


data class CourseData(
    // ✨ 코스 식별자
    @SerializedName("courseId")
    val courseId: String,

    // ✨ 코스 이름
    @SerializedName("name")
    val name: String,

    // ✨ 핵심 통계: 거리 (미터)
   /* @SerializedName("distanceMeters")
    val distanceMeters: Double,

    // ✨ 핵심 통계: 총 시간 (밀리초)
    @SerializedName("totalTimeMillis")
    val totalTimeMillis: Long,*/

    // 러닝 경로의 개별 지점 리스트
    @SerializedName("pathPoints")
    val pathPoints: List<LocationPoint>,

    @SerializedName("createdAt")
    val createdAt: String
)
