package com.example.runnershigh.data.remote.dto

import com.squareup.moshi.Json

/**
 * 이미 제출된 러닝 피드백을 불러올 때 사용하는 DTO
 */
data class SubmittedFeedback(
    @Json(name = "sessionId")
    val sessionId: String = "",
    @Json(name = "rating")
    val rating: Int = 0,
    @Json(name = "difficulty")
    val difficulty: Int = 0,
    @Json(name = "injuryParts")
    val injuryParts: List<String> = emptyList(),
    @Json(name = "comment")
    val comment: String = "",
    @Json(name = "createdAt")
    val createdAt: String = ""
)
