package com.example.runnershigh.data.repository

import com.example.runnershigh.data.remote.dto.ConditionLevelResponse
import com.example.runnershigh.data.remote.dto.ConditionTrend
import com.example.runnershigh.data.remote.dto.UserCondition

data class ConditionSnapshot(
    val score: Int?,
    val status: String?,
    val analysis: String?,
    val recommendation: String?,
    val trend: List<ConditionTrend>
)

internal fun mergeConditionSnapshot(
    level: ConditionLevelResponse?,
    detail: UserCondition?
): ConditionSnapshot {
    val normalizedDetailStatus = detail?.todayStatus
        ?.takeIf { it.isNotBlank() }
        ?: detail?.conditionLevel?.takeIf { it.isNotBlank() }

    val levelStatus = level?.analysis?.takeIf { it.isNotBlank() }

    return ConditionSnapshot(
        score = detail?.todayScore ?: level?.conditionLevel?.takeIf { it > 0 },
        status = normalizedDetailStatus ?: levelStatus,
        analysis = levelStatus ?: normalizedDetailStatus,
        recommendation = detail?.recommendationText?.takeIf { it.isNotBlank() },
        trend = detail?.trendGraph.orEmpty()
    )
}
