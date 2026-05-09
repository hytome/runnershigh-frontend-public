package com.example.runnershigh.data.remote.dto

import com.squareup.moshi.Json

data class RunningFeedbackRequest(
    val sessionId: String,
    val userId: String,
    @Json(name = "user_uuid") val userUuid: String? = null,
    val rating: Int,
    val difficulty: Int,
    val injuryParts: List<String>,
    val comment: String
)
