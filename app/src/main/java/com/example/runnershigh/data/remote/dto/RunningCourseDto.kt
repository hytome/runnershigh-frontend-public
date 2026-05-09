package com.example.runnershigh.data.remote.dto

import com.squareup.moshi.Json

/**
 * 코스 크리에이터 API 요청/응답 모델
 */
data class WaypointDto(
    val lat: Double,
    val lng: Double
)

data class CoursePathPointDto(
    val lat: Double,
    val lng: Double,
    val time: String? = null
)

data class RunningCourseRequest(
    val userId: String,
    val name: String,
    val distance: Double,
    val totalTime: Int,
    val waypoints: List<WaypointDto>,
    val cumulativeDistances: List<Double>,
    val gpxFileBase64: String,
    val gpxFileName: String? = null
)

data class RunningCourseDto(
    val courseId: String? = null,
    val name: String = "",
    val distance: Double = 0.0,
    val totalTime: Int = 0,
    @Json(name = "reviewRating") val reviewRating: Double? = null,
    val waypoints: List<WaypointDto> = emptyList(),
    val cumulativeDistances: List<Double> = emptyList(),
    @Json(name = "pathPoints") val pathData: List<CoursePathPointDto> = emptyList()
)

data class RunningCourseResponse(
    val courseId: String? = null,
    val pathData: List<CoursePathPointDto> = emptyList()
)
