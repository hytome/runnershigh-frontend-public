package com.example.runnershigh.data.remote.dto

data class FinishSessionResponse(
    val ok: Boolean,
    val sessionId: String,
    val avgHeartRate: Int?,
    val kmPace: Map<String, Int>,
    val newBadges: List<AcquiredBadge> = emptyList(),
    val levelInfo: LevelInfoResponse? = null
)

data class LevelInfoResponse(
    val gained_xp: Int = 0,
    val current_level: Int? = null,
    val is_levelup: Boolean = false,
    val message: String? = null
)
