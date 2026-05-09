package com.example.runnershigh.domain.model

data class RunningPlanGoal(
    val targetDistanceKm: Double = 0.0,
    val targetPaceSecPerKm: Int? = null,
    val planTitle: String? = null
)
