package com.example.runnershigh.domain.model

data class RunningFeedback(
    val courseRating: Int,
    val painAreas: List<String>,
    val difficulty: Int,
    val comment: String
)
